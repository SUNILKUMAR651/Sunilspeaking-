import re

path = "app/src/main/java/com/example/viewmodel/LexiViewModel.kt"
with open(path, "r") as f:
    content = f.read()

import_str = """import com.example.data.cache.OfflineCache"""
new_import_str = """import com.example.data.database.LexiDatabase"""
content = content.replace(import_str, new_import_str)

init_str = """    init {
        val offlineCache = OfflineCache(application)
        repository = LexiRepository(offlineCache)"""
new_init_str = """    init {
        val database = LexiDatabase.getDatabase(application)
        repository = LexiRepository(database.wordDao())"""
content = content.replace(init_str, new_init_str)

with open(path, "w") as f:
    f.write(content)
