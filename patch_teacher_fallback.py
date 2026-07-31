import re
import os

files = [
    "app/src/main/java/com/example/ui/screens/AITeacherScreen.kt",
    "app/src/main/java/com/example/ui/screens/AIRoleplayScreen.kt",
    "app/src/main/java/com/example/ui/screens/AICallScreen.kt"
]

good_fallback = """val mockResponses = listOf(
                    "That's very interesting! Can you tell me more?",
                    "I see! How does that make you feel?",
                    "That is a great point. I agree with you.",
                    "Could you elaborate on that?",
                    "Awesome! Let's keep practicing.",
                    "Very good! Your pronunciation is getting better."
                )
                val fallbackResponse = mockResponses.random()"""

for path in files:
    if os.path.exists(path):
        with open(path, "r") as f:
            content = f.read()
        
        # for AITeacherScreen
        content = re.sub(
            r'val fallbackResponse = "I seem to be having a little trouble connecting to my knowledge base right now\..*?"\s*',
            good_fallback + "\n                ",
            content
        )
        
        # for AIRoleplayScreen
        content = re.sub(
            r'val fallbackText = "I seem to be having a little trouble connecting to my knowledge base right now\..*?"\s*',
            good_fallback.replace("fallbackResponse", "fallbackText") + "\n                ",
            content
        )

        with open(path, "w") as f:
            f.write(content)
