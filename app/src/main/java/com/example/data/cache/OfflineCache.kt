package com.example.data.cache

import android.content.Context
import com.example.data.WordObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class OfflineCache(private val context: Context) {
    private val cacheFile = File(context.filesDir, "words_cache.json")
    
    private val _words = MutableStateFlow<List<WordObject>>(emptyList())
    val words: StateFlow<List<WordObject>> = _words.asStateFlow()
    
    init {
        loadFromDisk()
    }
    
    fun loadFromDisk() {
        if (cacheFile.exists()) {
            try {
                val json = cacheFile.readText()
                val jsonConfig = Json { ignoreUnknownKeys = true; encodeDefaults = true; coerceInputValues = true }
                val parsed = jsonConfig.decodeFromString<List<WordObject>>(json)
                _words.value = parsed
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun saveToDisk(words: List<WordObject>) {
        _words.value = words
        try {
            val jsonConfig = Json { ignoreUnknownKeys = true; encodeDefaults = true; coerceInputValues = true }
            val json = jsonConfig.encodeToString(words)
            cacheFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun getWord(wordString: String): WordObject? {
        return _words.value.find { it.word.equals(wordString, ignoreCase = true) }
    }
    
    fun getWordsByCategory(category: String): List<WordObject> {
        return _words.value.filter { it.category == category }
    }
}
