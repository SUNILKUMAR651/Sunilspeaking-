package com.example.data.repository

import com.example.api.GeminiApiService
import com.example.api.RetrofitClient
import com.example.api.GenerateContentRequest
import com.example.api.Content
import com.example.api.Part
import com.example.BuildConfig
import com.example.data.WordObject
import com.example.data.UserProfile
import com.example.data.cache.OfflineCache
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class LexiRepository(
    private val offlineCache: OfflineCache,
    private val apiService: GeminiApiService = RetrofitClient.service
) {
    val allWords: Flow<List<WordObject>> = offlineCache.words

    fun getWordsByCategory(category: String): Flow<List<WordObject>> {
        return allWords.map { words -> words.filter { it.category == category } }
    }

    fun getWordsByStartingLetter(letter: String): Flow<List<WordObject>> {
        return allWords.map { words -> words.filter { it.word.startsWith(letter, ignoreCase = true) } }
    }
    
    suspend fun getWord(word: String): WordObject? {
        return offlineCache.getWord(word)
    }

    suspend fun insertWord(word: WordObject) {
        val updated = offlineCache.words.value + word
        offlineCache.saveToDisk(updated)
        // Optionally insert to Firebase:
        try { 
            FirebaseFirestore.getInstance().collection("words").document(word.word).set(word).await() 
        } catch (e: Exception) { 
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getLeaderboard(limit: Long = 10): List<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val snapshot = FirebaseFirestore.getInstance().collection("users")
                .orderBy("totalXp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getUserProfile(userId: String): UserProfile = withContext(Dispatchers.IO) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
            if (doc.exists()) {
                doc.toObject(UserProfile::class.java) ?: UserProfile(id = userId)
            } else {
                val newProfile = UserProfile(id = userId)
                FirebaseFirestore.getInstance().collection("users").document(userId).set(newProfile).await()
                newProfile
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UserProfile(id = userId)
        }
    }

    suspend fun updateUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        try {
            FirebaseFirestore.getInstance().collection("users").document(profile.id).set(profile).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Sync with Firebase (Data Fetch Protocol)
    suspend fun syncWithFirebase() = withContext(Dispatchers.IO) {
        FirebaseFirestore.getInstance().collection("words")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    try {
                        val remoteWords = snapshot.toObjects(WordObject::class.java)
                        if (remoteWords.isNotEmpty()) {
                            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                offlineCache.saveToDisk(remoteWords)
                            }
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
    }

    fun getWeakWords(): Flow<List<WordObject>> {
        val currentTime = System.currentTimeMillis()
        return allWords.map { words -> 
            words.filter { it.nextReviewDate in 1..currentTime || it.easinessFactor < 2.5f }
                .sortedBy { it.nextReviewDate }
                .take(10) // Get top 10 weak words
        }
    }

    suspend fun updateWordMastery(wordStr: String, quality: Int) { // quality: 0-5
        val word = getWord(wordStr) ?: return
        
        var ef = word.easinessFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        if (ef < 1.3f) ef = 1.3f
        
        var reps = word.repetitions
        var interval = word.interval
        
        if (quality < 3) {
            reps = 0
            interval = 1
        } else {
            reps += 1
            if (reps == 1) interval = 1
            else if (reps == 2) interval = 6
            else interval = (interval * ef).toInt()
        }
        
        val nextReview = System.currentTimeMillis() + (interval * 24L * 60 * 60 * 1000)
        
        val updatedWord = word.copy(
            easinessFactor = ef,
            repetitions = reps,
            interval = interval,
            nextReviewDate = nextReview
        )
        insertWord(updatedWord) // also saves to disk
    }

    suspend fun generateExampleSentence(word: String, interest: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        
        val prompt = "Generate a single, highly engaging, and contextually accurate example sentence using the vocabulary word '$word'. The sentence should be related to the user's interest: '$interest'. Make it sound natural for a business professional."
        
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "You are LexiMaster, an expert English language coach.")))
        )
        
        try {
            val response = apiService.generateContent(apiKey, request)
            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response generated."
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
