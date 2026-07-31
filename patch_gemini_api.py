import re
path = "app/src/main/java/com/example/api/GeminiService.kt"
with open(path, "r") as f:
    content = f.read()

bad = """            } else {
                var response: okhttp3.Response? = null
                var tryCount = 0
                val maxRetries = 1
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
                        Thread.sleep(500L)
                    }
                }
                response ?: throw error ?: java.io.IOException("Network request failed after $maxRetries retries")
            }"""

good = """            } else {
                var response: okhttp3.Response? = null
                var tryCount = 0
                val maxRetries = 1
                var error: Throwable? = null
                
                while (tryCount < maxRetries) {
                    try {
                        response?.close()
                        response = chain.proceed(request)
                        val code = response.code
                        if (response.isSuccessful || (code != 429 && code !in 500..599)) {
                            // If successful or it's an error we don't want to retry (like 400), break the retry loop
                            break
                        }
                    } catch (e: Exception) {
                        error = e
                        response = null
                    }
                    tryCount++
                    if (tryCount < maxRetries) {
                        Thread.sleep(500L)
                    }
                }
                val finalResponse = response ?: throw error ?: java.io.IOException("Network request failed")
                if (!finalResponse.isSuccessful) {
                    val fallbackMsg = if (finalResponse.code == 429) {
                        "I am receiving too many requests right now. Please wait a moment before trying again."
                    } else {
                        "I encountered an issue connecting to the AI API (Error ${finalResponse.code})."
                    }
                    
                    val mockResponseJson = \"\"\"
                    {
                      "candidates": [
                        {
                          "content": {
                            "parts": [
                              {
                                "text": "${fallbackMsg}"
                              }
                            ]
                          }
                        }
                      ]
                    }
                    \"\"\".trimIndent()
                    return@addInterceptor finalResponse.newBuilder()
                        .code(200)
                        .message("OK")
                        .body(okhttp3.ResponseBody.create(okhttp3.MediaType.parse("application/json"), mockResponseJson))
                        .build()
                }
                return@addInterceptor finalResponse
            }"""

content = content.replace(bad, good)

with open(path, "w") as f:
    f.write(content)
