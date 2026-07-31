import re
with open("app/src/main/java/com/example/api/GeminiService.kt", "r") as f:
    content = f.read()

content = content.replace("gemini-1.5-flash", "gemini-3.5-flash")

with open("app/src/main/java/com/example/api/GeminiService.kt", "w") as f:
    f.write(content)
