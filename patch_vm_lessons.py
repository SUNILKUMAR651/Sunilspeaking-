import re

path = "app/src/main/java/com/example/viewmodel/LexiViewModel.kt"
with open(path, "r") as f:
    content = f.read()

import_str = """import com.example.data.database.LexiDatabase"""
new_import_str = """import com.example.data.database.LexiDatabase
import com.example.data.database.LessonEntity"""
if "LessonEntity" not in content:
    content = content.replace(import_str, new_import_str)

init_repo = """        val database = LexiDatabase.getDatabase(application)
        repository = LexiRepository(database.wordDao())"""
new_init_repo = """        val database = LexiDatabase.getDatabase(application)
        repository = LexiRepository(database.wordDao(), database.lessonDao())"""
content = content.replace(init_repo, new_init_repo)

flow_lessons = """    val weakWords: StateFlow<List<WordObject>> = repository.getWeakWords().stateIn("""
new_flow_lessons = """    val allLessons: StateFlow<List<LessonEntity>> = repository.allLessons.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val weakWords: StateFlow<List<WordObject>> = repository.getWeakWords().stateIn("""
if "allLessons: StateFlow" not in content:
    content = content.replace(flow_lessons, new_flow_lessons)

with open(path, "w") as f:
    f.write(content)
