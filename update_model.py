import re
with open("app/src/main/java/com/example/api/GeminiService.kt", "r") as f:
    content = f.read()
    
content = content.replace("gemini-2.5-flash", "gemini-3.1-flash-lite")

with open("app/src/main/java/com/example/api/GeminiService.kt", "w") as f:
    f.write(content)
