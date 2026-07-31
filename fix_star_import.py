with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "r") as f:
    content = f.read()

if "import androidx.compose.material.icons.filled.Star" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Refresh", "import androidx.compose.material.icons.filled.Refresh\nimport androidx.compose.material.icons.filled.Star")

with open("app/src/main/java/com/example/ui/screens/PracticeRunScreen.kt", "w") as f:
    f.write(content)
