import re
path = "app/src/main/java/com/example/api/GeminiService.kt"
with open(path, "r") as f:
    content = f.read()

bad = 'okhttp3.MediaType.parse("application/json")'
good = '"application/json".toMediaType()'
content = content.replace(bad, good)

with open(path, "w") as f:
    f.write(content)
