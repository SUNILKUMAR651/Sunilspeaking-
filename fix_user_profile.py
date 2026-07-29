with open('app/src/main/java/com/example/data/UserProfile.kt', 'r') as f:
    content = f.read()

content = content.replace("var lessonHistory: List<Float> = listOf(5f, 12f, 10f, 25f, 22f, 30f)", "var lessonHistory: List<Float> = listOf(5f, 12f, 10f, 25f, 22f, 30f),\n    var useFemaleVoice: Boolean = true")

with open('app/src/main/java/com/example/data/UserProfile.kt', 'w') as f:
    f.write(content)
