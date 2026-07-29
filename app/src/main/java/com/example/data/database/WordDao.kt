package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.WordObject
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAllWords(): Flow<List<WordObject>>

    @Query("SELECT * FROM words WHERE category = :category ORDER BY word ASC")
    fun getWordsByCategory(category: String): Flow<List<WordObject>>

    @Query("SELECT * FROM words WHERE word LIKE :letter || '%' ORDER BY word ASC")
    fun getWordsByStartingLetter(letter: String): Flow<List<WordObject>>
    
    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getWord(word: String): WordObject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordObject)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordObject>)

    @Query("DELETE FROM words WHERE word = :word")
    suspend fun deleteWord(word: String)
    
    @Query("SELECT COUNT(*) FROM words")
    fun getWordCount(): Flow<Int>

    @Query("SELECT * FROM words WHERE nextReviewDate > 0 AND nextReviewDate <= :currentTime ORDER BY easinessFactor ASC LIMIT :limit")
    suspend fun getDueWeakWords(currentTime: Long, limit: Int): List<WordObject>
}
