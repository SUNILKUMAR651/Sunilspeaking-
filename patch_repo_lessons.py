import re

path = "app/src/main/java/com/example/data/repository/LexiRepository.kt"
with open(path, "r") as f:
    content = f.read()

import_str = """import com.example.data.database.WordDao"""
new_import_str = """import com.example.data.database.WordDao
import com.example.data.database.LessonDao
import com.example.data.database.LessonEntity"""
content = content.replace(import_str, new_import_str)

class_decl = """class LexiRepository(
    private val wordDao: WordDao,"""
new_class_decl = """class LexiRepository(
    private val wordDao: WordDao,
    private val lessonDao: LessonDao,"""
content = content.replace(class_decl, new_class_decl)

all_words = """    val allWords: Flow<List<WordObject>> = wordDao.getAllWords()"""
new_all_words = """    val allWords: Flow<List<WordObject>> = wordDao.getAllWords()
    val allLessons: Flow<List<LessonEntity>> = lessonDao.getAllLessons()"""
content = content.replace(all_words, new_all_words)

with open(path, "w") as f:
    f.write(content)
