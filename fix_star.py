with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "r") as f:
    content = f.read()

content = content.replace("androidx.compose.material.icons.filled.Star", "Icons.Filled.Star")

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "w") as f:
    f.write(content)
