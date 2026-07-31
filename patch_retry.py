import re
path = "app/src/main/java/com/example/api/GeminiService.kt"
with open(path, "r") as f:
    content = f.read()

old_block = """            } else {
                chain.proceed(request)
            }"""

new_block = """            } else {
                var response: okhttp3.Response? = null
                var tryCount = 0
                val maxRetries = 3
                var error: Throwable? = null
                
                while (tryCount < maxRetries) {
                    try {
                        response?.close()
                        response = chain.proceed(request)
                        val code = response.code
                        if (response.isSuccessful || (code != 429 && code !in 500..599)) {
                            return@addInterceptor response
                        }
                    } catch (e: Exception) {
                        error = e
                        response = null
                    }
                    tryCount++
                    if (tryCount < maxRetries) {
                        Thread.sleep(1000L * tryCount)
                    }
                }
                response ?: throw error ?: java.io.IOException("Network request failed after $maxRetries retries")
            }"""

content = content.replace(old_block, new_block)

with open(path, "w") as f:
    f.write(content)
