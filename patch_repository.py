import re

path = "app/src/main/java/com/example/data/repository/LexiRepository.kt"
with open(path, "r") as f:
    content = f.read()

import_str = """import com.example.data.cache.OfflineCache"""
new_import_str = """import com.example.data.database.WordDao"""
content = content.replace(import_str, new_import_str)

class_decl = """class LexiRepository(
    private val offlineCache: OfflineCache,"""
new_class_decl = """class LexiRepository(
    private val wordDao: WordDao,"""
content = content.replace(class_decl, new_class_decl)

all_words = """    val allWords: Flow<List<WordObject>> = offlineCache.words"""
new_all_words = """    val allWords: Flow<List<WordObject>> = wordDao.getAllWords()"""
content = content.replace(all_words, new_all_words)

get_word = """    suspend fun getWord(word: String): WordObject? {
        return offlineCache.getWord(word)
    }"""
new_get_word = """    suspend fun getWord(word: String): WordObject? {
        return wordDao.getWord(word)
    }"""
content = content.replace(get_word, new_get_word)

insert_word = """    suspend fun insertWord(word: WordObject) {
        val updated = offlineCache.words.value + word
        offlineCache.saveToDisk(updated)"""
new_insert_word = """    suspend fun insertWord(word: WordObject) {
        wordDao.insertWord(word)"""
content = content.replace(insert_word, new_insert_word)

sync_firebase = """                        if (remoteWords.isNotEmpty()) {
                            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                offlineCache.saveToDisk(remoteWords)
                            }
                        }"""
new_sync_firebase = """                        if (remoteWords.isNotEmpty()) {
                            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                wordDao.insertWords(remoteWords)
                            }
                        }"""
content = content.replace(sync_firebase, new_sync_firebase)

with open(path, "w") as f:
    f.write(content)
