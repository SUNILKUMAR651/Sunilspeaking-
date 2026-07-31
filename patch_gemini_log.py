import re
with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "r") as f:
    content = f.read()

content = content.replace("catch (e: Exception) {", 'catch (e: Exception) {\n                android.util.Log.e("GeminiError", "AITeacher Gemini API error", e)')
with open("app/src/main/java/com/example/ui/screens/AITeacherScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/AICallScreen.kt", "r") as f:
    content = f.read()

content = content.replace("catch (e: Exception) {", 'catch (e: Exception) {\n                android.util.Log.e("GeminiError", "AICall Gemini API error", e)')
with open("app/src/main/java/com/example/ui/screens/AICallScreen.kt", "w") as f:
    f.write(content)
