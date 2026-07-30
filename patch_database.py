import re

path = "app/src/main/java/com/example/data/database/LexiDatabase.kt"
with open(path, "r") as f:
    content = f.read()

db_annotation = """@Database(entities = [WordObject::class], version = 2, exportSchema = false)"""
new_db_annotation = """@Database(entities = [WordObject::class, LessonEntity::class], version = 3, exportSchema = false)"""
content = content.replace(db_annotation, new_db_annotation)

dao_decl = """    abstract fun wordDao(): WordDao"""
new_dao_decl = """    abstract fun wordDao(): WordDao
    abstract fun lessonDao(): LessonDao"""
content = content.replace(dao_decl, new_dao_decl)

populate_db = """                    // Prepopulate with LexiconDatabase words
                    dao.insertWords(LexiconDatabase.words)"""
new_populate_db = """                    // Prepopulate with LexiconDatabase words
                    dao.insertWords(LexiconDatabase.words)
                    
                    val lessonDao = database.lessonDao()
                    lessonDao.insertLessons(listOf(
                        LessonEntity(1, "Introductions", "MEETING NEW PEOPLE", false),
                        LessonEntity(2, "Colors", "BASIC COLORS", false),
                        LessonEntity(3, "Numbers", "COUNTING NUMBERS", false),
                        LessonEntity(4, "Family Members", "FAMILY MEMBERS", true),
                        LessonEntity(5, "Animals", "COMMON ANIMALS", true)
                    ))"""
content = content.replace(populate_db, new_populate_db)

with open(path, "w") as f:
    f.write(content)
