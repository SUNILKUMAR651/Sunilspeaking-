import re

with open('app/src/main/java/com/example/ui/screens/LearningPathScreen.kt', 'r') as f:
    content = f.read()

old_lessons = '''    val lessons = remember {
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
            "Time & Days" to "TIME",
            "At Home" to "HOUSEHOLD",
            "At School" to "EDUCATION",
            "Travel" to "VACATION",
            "Work" to "OFFICE",
            "Weather" to "NATURE",
            "Hobbies" to "LEISURE",
            "Emotions" to "FEELINGS"
        )
        for (i in 6..100) {
            val topic = topics[(i - 6) % topics.size]
            list.add(PathLesson(i, topic.first, topic.second, i > 10))
        }
        list
    }'''

new_lessons = '''    val lessons = remember {
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
            "Time & Days" to "TIME",
            "At Home" to "HOUSEHOLD",
            "At School" to "EDUCATION",
            "Travel" to "VACATION",
            "Work" to "OFFICE",
            "Weather" to "NATURE",
            "Hobbies" to "LEISURE",
            "Emotions" to "FEELINGS",
            "Business" to "CORPORATE",
            "Science" to "DISCOVERY",
            "Technology" to "INNOVATION",
            "Culture" to "TRADITIONS",
            "Art" to "CREATIVITY"
        )
        for (i in 6..500) {
            val topic = topics[(i - 6) % topics.size]
            val difficultyLevel = (i / 50) + 1
            val difficultyLabel = when(difficultyLevel) {
                1 -> "BEGINNER"
                2 -> "INTERMEDIATE"
                3 -> "UPPER INTERMEDIATE"
                4 -> "ADVANCED"
                5 -> "PROFICIENT"
                6 -> "MASTER"
                7 -> "EXPERT"
                8 -> "ELITE"
                9 -> "LEGENDARY"
                else -> "GOD TIER"
            }
            list.add(PathLesson(i, "${topic.first} - Lv $difficultyLevel", "$difficultyLabel • ${topic.second}", i > 3))
        }
        list
    }'''

content = content.replace(old_lessons, new_lessons)

with open('app/src/main/java/com/example/ui/screens/LearningPathScreen.kt', 'w') as f:
    f.write(content)
