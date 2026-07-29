import re

path = "app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt"
with open(path, 'r') as f:
    content = f.read()

# Make sure we import ConfettiAnimation
if "import com.example.ui.components.ConfettiAnimation" not in content:
    content = content.replace("import androidx.compose.ui.Alignment", "import androidx.compose.ui.Alignment\nimport com.example.ui.components.ConfettiAnimation")

# Inject inside LessonCompleteView
content = content.replace("fun LessonCompleteView(onFinish: () -> Unit) {\n    Column(", "fun LessonCompleteView(onFinish: () -> Unit) {\n    Box(modifier = Modifier.fillMaxSize()) {\n    Column(")
content = content.replace("            fontSize = 20.sp,\n            color = Color(0xFF6B4EE6)\n        )\n        Spacer(modifier = Modifier.height(32.dp))\n        Button(\n            onClick = onFinish,", "            fontSize = 20.sp,\n            color = Color(0xFF6B4EE6)\n        )\n        Spacer(modifier = Modifier.height(32.dp))\n        Button(\n            onClick = onFinish,")

# Oh wait, need to properly close the Box.
# Let's find LessonCompleteView and replace the whole block.
