import re

path = "app/src/main/java/com/example/ui/screens/LearningPathScreen.kt"
with open(path, "r") as f:
    content = f.read()

data_class = """data class PathLesson(
    val id: Int,
    val title: String,
    val subtitle: String,
    val isPro: Boolean
)"""
new_data_class = """import com.example.data.database.LessonEntity

data class PathLesson(
    val id: Int,
    val title: String,
    val subtitle: String,
    val isPro: Boolean
)"""
if "import com.example.data.database.LessonEntity" not in content:
    content = content.replace(data_class, new_data_class + "\n" + data_class)

lessons_gen = """    val lessons = remember {
        val list = mutableListOf(
            PathLesson(1, "Introductions", "MEETING NEW PEOPLE", false),
            PathLesson(2, "Colors", "BASIC COLORS", false),
            PathLesson(3, "Numbers", "COUNTING NUMBERS", false),
            PathLesson(4, "Family Members", "FAMILY MEMBERS", true),
            PathLesson(5, "Animals", "COMMON ANIMALS", true)
        )
        val topics = listOf(
            "Fruits & Veggies" to "FOOD",
            "Daily Routines" to "ROUTINES",
            "Travel" to "TRANSPORT",
            "Emotions" to "FEELINGS",
            "Shopping" to "CLOTHES",
            "Weather" to "SEASONS"
        )
        
        for (i in 6..20) {
            val topic = topics[(i - 6) % topics.size]
            val difficultyLevel = ((i - 6) / topics.size) + 2 // Increase level each wrap-around
            
            val difficultyLabel = when {
                difficultyLevel <= 2 -> "BEGINNER II"
                difficultyLevel == 3 -> "INTERMEDIATE"
                difficultyLevel == 4 -> "UPPER INTERMEDIATE"
                else -> "ADVANCED"
            }
            
            list.add(PathLesson(i, "${topic.first} - Lv $difficultyLevel", "$difficultyLabel • ${topic.second}", i > 3))
        }
        
        list
    }"""
    
new_lessons_gen = """    val cachedLessons by viewModel.allLessons.collectAsState()
    
    val lessons = remember(cachedLessons) {
        val list = mutableListOf<PathLesson>()
        
        if (cachedLessons.isNotEmpty()) {
            cachedLessons.forEach { dbLesson ->
                list.add(PathLesson(dbLesson.id, dbLesson.title, dbLesson.description, dbLesson.isLocked))
            }
        } else {
            list.add(PathLesson(1, "Introductions", "MEETING NEW PEOPLE", false))
            list.add(PathLesson(2, "Colors", "BASIC COLORS", false))
            list.add(PathLesson(3, "Numbers", "COUNTING NUMBERS", false))
            list.add(PathLesson(4, "Family Members", "FAMILY MEMBERS", true))
            list.add(PathLesson(5, "Animals", "COMMON ANIMALS", true))
        }
        
        val topics = listOf(
            "Fruits & Veggies" to "FOOD",
            "Daily Routines" to "ROUTINES",
            "Travel" to "TRANSPORT",
            "Emotions" to "FEELINGS",
            "Shopping" to "CLOTHES",
            "Weather" to "SEASONS"
        )
        
        for (i in (list.size + 1)..20) {
            val topic = topics[(i - 6) % topics.size]
            val difficultyLevel = ((i - 6) / topics.size) + 2
            
            val difficultyLabel = when {
                difficultyLevel <= 2 -> "BEGINNER II"
                difficultyLevel == 3 -> "INTERMEDIATE"
                difficultyLevel == 4 -> "UPPER INTERMEDIATE"
                else -> "ADVANCED"
            }
            
            list.add(PathLesson(i, "${topic.first} - Lv $difficultyLevel", "$difficultyLabel • ${topic.second}", i > 3))
        }
        
        list
    }"""
content = content.replace(lessons_gen, new_lessons_gen)

with open(path, "w") as f:
    f.write(content)
