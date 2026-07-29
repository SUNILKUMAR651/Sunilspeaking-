import re

path = "app/src/main/java/com/example/ui/screens/ActiveLessonScreen.kt"
with open(path, 'r') as f:
    content = f.read()

# Add import
if "import com.example.ui.components.ConfettiAnimation" not in content:
    content = content.replace("import androidx.compose.ui.Alignment", "import androidx.compose.ui.Alignment\nimport com.example.ui.components.ConfettiAnimation\nimport androidx.compose.foundation.layout.Box")

# Replace LessonCompleteView
old_view = """fun LessonCompleteView(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {"""

new_view = """fun LessonCompleteView(onFinish: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {"""

content = content.replace(old_view, new_view)

old_button = """        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("CONTINUE", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}"""

new_button = """        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("CONTINUE", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
    ConfettiAnimation()
    }
}"""

content = content.replace(old_button, new_button)

with open(path, 'w') as f:
    f.write(content)
