package com.example.viewmodel

import android.app.Application
import java.io.File
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.WordObject
import com.example.data.database.LexiDatabase
import com.example.data.database.LessonEntity
import com.example.data.repository.LexiRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.utils.retryWithBackoff

sealed class AuthStatus {
    object Initial : AuthStatus()
    object Loading : AuthStatus()
    data class Authenticated(val userId: String) : AuthStatus()
    object Unauthenticated : AuthStatus()
    data class Error(val message: String) : AuthStatus()
}

class LexiViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: LexiRepository
    private val auth = FirebaseAuth.getInstance()
    
    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Initial)
    val authStatus: StateFlow<AuthStatus> = _authStatus.asStateFlow()

    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _selectedLetter = MutableStateFlow<String?>(null)
    val selectedLetter: StateFlow<String?> = _selectedLetter.asStateFlow()

    val categories = listOf("Beginner level", "Intermediate level", "Pro level", "Basic Vocab", "News Vocab", "Important Vocab", "Speaking Practice")

    private val _userInterest = MutableStateFlow("Technology and Startups")
    val userInterest: StateFlow<String> = _userInterest.asStateFlow()

    private val _aiGeneratedSentence = MutableStateFlow<String?>(null)
    val aiGeneratedSentence: StateFlow<String?> = _aiGeneratedSentence.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    
    private val _selectedWord = MutableStateFlow<WordObject?>(null)
    val selectedWord: StateFlow<WordObject?> = _selectedWord.asStateFlow()
    
    private val _userProfile = MutableStateFlow(com.example.data.UserProfile())
    val userProfile: StateFlow<com.example.data.UserProfile> = _userProfile.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init {
        val database = LexiDatabase.getDatabase(application)
        repository = LexiRepository(database.wordDao(), database.lessonDao())
        
        checkAuthStatus()
        
        viewModelScope.launch {
            repository.syncWithFirebase()
        }
    }
    
    private fun checkAuthStatus() {
        val user = auth.currentUser
        if (user != null) {
            _authStatus.value = AuthStatus.Authenticated(user.uid)
            _isAdmin.value = user.email == "sittukumar8433250@gmail.com"
            loadUserProfile(user.uid)
        } else {
            _authStatus.value = AuthStatus.Unauthenticated
            _isAdmin.value = false
        }
    }
    
    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _userProfile.value = repository.getUserProfile(userId)
        }
    }
        fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            try {
                val result = retryWithBackoff { auth.signInWithEmailAndPassword(email, password).await() }
                result.user?.uid?.let { uid ->
                    _authStatus.value = AuthStatus.Authenticated(uid)
                    _isAdmin.value = result.user?.email == "sittukumar8433250@gmail.com"
                    loadUserProfile(uid)
                } ?: run {
                    _authStatus.value = AuthStatus.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _authStatus.value = AuthStatus.Error(e.message ?: "Authentication failed")
            }
        }
    }
    
    fun signUp(email: String, password: String, name: String = "") {
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            try {
                val result = retryWithBackoff { auth.createUserWithEmailAndPassword(email, password).await() }
                result.user?.uid?.let { uid ->
                    // Initialize empty profile for new user
                    val newProfile = com.example.data.UserProfile(
                        id = uid,
                        name = name.ifBlank { email.substringBefore("@") },
                        initials = name.ifBlank { email }.substring(0, 1).uppercase(),
                        level = "Beginner Level (A1)",
                        dayStreak = 0,
                        totalXp = 0,
                        vocabularyProgress = 0f,
                        grammarProgress = 0f,
                        speakingProgress = 0f,
                        listeningProgress = 0f,
                        certificationProgress = 0f,
                        unlockedCertificates = emptyList(),
                        isAdmin = email == "sittukumar8433250@gmail.com"
                    )
                    repository.updateUserProfile(newProfile)
                    
                    _authStatus.value = AuthStatus.Authenticated(uid)
                    _isAdmin.value = email == "sittukumar8433250@gmail.com"
                    loadUserProfile(uid)
                } ?: run {
                    _authStatus.value = AuthStatus.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _authStatus.value = AuthStatus.Error(e.message ?: "Authentication failed")
            }
        }
    }
    
    fun signOut() {
        auth.signOut()
        _authStatus.value = AuthStatus.Unauthenticated
        _isAdmin.value = false
        _userProfile.value = com.example.data.UserProfile() // Reset profile
    }

    suspend fun getLeaderboard(): List<com.example.data.UserProfile> {
        return repository.getLeaderboard(50) // Fetch top 50 to ensure we find the user
    }

    fun updateProfile(profile: com.example.data.UserProfile) {
        viewModelScope.launch {
            _userProfile.value = profile
            repository.updateUserProfile(profile)
        }
    }

    fun recordLessonCompletion(xpEarned: Int, skill: String) {
        val currentProfile = _userProfile.value
        val newXp = currentProfile.totalXp + xpEarned
        
        // Simple day streak logic (assuming they are playing today)
        // In a real app we'd compare dates, here we'll just increment if they complete a lesson and maybe haven't in a while.
        // For simplicity, we just increment it if it's their first lesson of the current session.
        // We'll just track if we've incremented the streak this app session.
        val newStreak = currentProfile.dayStreak + if (xpEarned > 15) 1 else 0 // Fake some streak updates based on big sessions

        val newLevel = when {
            newXp < 1000 -> "Beginner Level (A1)"
            newXp < 3000 -> "Elementary Level (A2)"
            newXp < 6000 -> "Intermediate Level (B1)"
            newXp < 10000 -> "Upper Intermediate (B2)"
            else -> "Advanced Level (C1)"
        }
        
        var newVocab = currentProfile.vocabularyProgress
        var newGrammar = currentProfile.grammarProgress
        var newSpeaking = currentProfile.speakingProgress
        var newListening = currentProfile.listeningProgress
        
        when (skill) {
            "vocabulary" -> newVocab = (newVocab + 0.05f).coerceAtMost(1.0f)
            "grammar" -> newGrammar = (newGrammar + 0.05f).coerceAtMost(1.0f)
            "speaking" -> newSpeaking = (newSpeaking + 0.05f).coerceAtMost(1.0f)
            "listening" -> newListening = (newListening + 0.05f).coerceAtMost(1.0f)
        }
        
        val totalProgress = (newVocab + newGrammar + newSpeaking + newListening) / 4.0f
        
        // Update charts history
        val newVocabHistory = currentProfile.vocabularyHistory.toMutableList()
        if (newVocabHistory.isNotEmpty()) {
            val lastIndex = newVocabHistory.lastIndex
            newVocabHistory[lastIndex] = newVocabHistory[lastIndex] + (xpEarned.toFloat() * 0.5f)
        }
        
        val newLessonHistory = currentProfile.lessonHistory.toMutableList()
        if (newLessonHistory.isNotEmpty()) {
            val lastIndex = newLessonHistory.lastIndex
            newLessonHistory[lastIndex] = newLessonHistory[lastIndex] + 1f
        }
        
        val updatedProfile = currentProfile.copy(
            totalXp = newXp, 
            dayStreak = newStreak,
            level = newLevel,
            vocabularyProgress = newVocab,
            grammarProgress = newGrammar,
            speakingProgress = newSpeaking,
            listeningProgress = newListening,
            certificationProgress = totalProgress,
            vocabularyHistory = newVocabHistory,
            lessonHistory = newLessonHistory
        )
        updateProfile(updatedProfile)
    }

    fun addXp(xpToAdd: Int) {
        val currentProfile = _userProfile.value
        val newXp = currentProfile.totalXp + xpToAdd
        val newLevel = when {
            newXp < 1000 -> "Beginner Level (A1)"
            newXp < 3000 -> "Elementary Level (A2)"
            newXp < 6000 -> "Intermediate Level (B1)"
            newXp < 10000 -> "Upper Intermediate (B2)"
            else -> "Advanced Level (C1)"
        }
        val updatedProfile = currentProfile.copy(totalXp = newXp, level = newLevel)
        updateProfile(updatedProfile)
    }

    val allWords: StateFlow<List<WordObject>> = repository.allWords.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allLessons: StateFlow<List<LessonEntity>> = repository.allLessons.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val weakWords: StateFlow<List<WordObject>> = repository.getWeakWords().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateWordMastery(word: String, quality: Int) {
        viewModelScope.launch {
            repository.updateWordMastery(word, quality)
            addXp(5) // Award XP for practicing weak words
        }
    }

    // Observe words from database and combine with search/filter
    val displayedWords: StateFlow<List<WordObject>> = combine(
        repository.allWords,
        _searchQuery,
        _selectedCategory,
        _selectedLetter
    ) { allWords, query, category, letter ->
        val lowercaseQuery = query.lowercase()
        allWords.filter { word ->
            val matchesQuery = word.word.lowercase().contains(lowercaseQuery) ||
                                word.definitions.any { it.meaning.lowercase().contains(lowercaseQuery) }
            val matchesCategory = category == null || word.category == category
            val matchesLetter = letter == null || word.word.startsWith(letter, ignoreCase = true)
            matchesQuery && matchesCategory && matchesLetter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun selectLetter(letter: String?) {
        _selectedLetter.value = letter
    }

    fun updateUserInterest(interest: String) {
        _userInterest.value = interest
    }

    fun generateSentenceForWord(word: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            _aiGeneratedSentence.value = null
            
            val result = repository.generateExampleSentence(word, _userInterest.value, _userProfile.value.targetLanguage)
            _aiGeneratedSentence.value = result.getOrElse { "Error: ${it.message}" }
            
            _isGenerating.value = false
        }
    }

    fun clearAiSentence() {
        _aiGeneratedSentence.value = null
    }

    fun addWord(word: WordObject) {
        viewModelScope.launch {
            repository.insertWord(word)
        }
    }
    
    fun uploadRecording(file: File, sentence: String, score: Int) {
        val user = auth.currentUser ?: return
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference.child("recordings/${user.uid}/${System.currentTimeMillis()}.3gp")
        
        ref.putFile(Uri.fromFile(file)).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val recordingData = mapOf(
                    "url" to uri.toString(),
                    "sentence" to sentence,
                    "score" to score,
                    "timestamp" to com.google.firebase.Timestamp.now()
                )
                db.collection("users").document(user.uid)
                  .collection("recordings").add(recordingData)
            }
        }
    }
    
    fun loadWord(wordString: String) {
        viewModelScope.launch {
            _selectedWord.value = repository.getWord(wordString)
        }
    }
}
