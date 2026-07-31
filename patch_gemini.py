import re
path = "app/src/main/java/com/example/api/GeminiService.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("v1beta/models/gemini-2.0-flash:generateContent", "v1beta/models/gemini-1.5-flash:generateContent")

# Reduce retries so it doesn't hang too long if it truly fails
content = content.replace("val maxRetries = 3", "val maxRetries = 1")
content = content.replace("Thread.sleep(1000L * tryCount)", "Thread.sleep(500L)")

with open(path, "w") as f:
    f.write(content)
