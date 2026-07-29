path = "app/src/main/java/com/example/data/repository/LexiRepository.kt"
with open(path, 'r') as f:
    content = f.read()

import re
old_check = """        if (apiKey == "MY_GEMINI_API_KEY" || apiKey.isEmpty()) {
            return@withContext Result.failure(Exception("Security Warning: API Key missing. Please configure your API key in AI Studio Secrets panel."))
        }"""

content = content.replace(old_check, "")

with open(path, 'w') as f:
    f.write(content)
