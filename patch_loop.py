import re
path = "app/src/main/java/com/example/ui/screens/AITeacherScreen.kt"
with open(path, "r") as f:
    content = f.read()

bad_block = """                var retryCount = 0
                var aiResponse = ""
                while(retryCount < 3) {
                    try {
                        val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                        aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm sorry, I couldn't process that."
                        break
                    } catch (e: Exception) {
                        retryCount++
                        if (retryCount >= 3) throw e
                        kotlinx.coroutines.delay(1000L * retryCount)
                    }
                }"""

good_block = """                val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm sorry, I couldn't process that.\""""

content = content.replace(bad_block, good_block)

with open(path, "w") as f:
    f.write(content)
