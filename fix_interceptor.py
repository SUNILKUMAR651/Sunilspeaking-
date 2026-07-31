import re
with open("app/src/main/java/com/example/api/GeminiService.kt", "r") as f:
    content = f.read()

bad_interceptor = """                val finalResponse = response
                if (finalResponse == null || !finalResponse.isSuccessful) {
                    val mockResponses = listOf(
                        "That's very interesting! Can you tell me more?",
                        "I see! How does that make you feel?",
                        "That is a great point. I agree with you.",
                        "Could you elaborate on that?",
                        "I hear you! You said something great.",
                        "Awesome! Let's keep practicing.",
                        "Very good! Your pronunciation is getting better."
                    )
                    val fallbackMsg = mockResponses.random()
                    
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
                    if (finalResponse != null) {
                        return@addInterceptor finalResponse.newBuilder()
                            .code(200)
                            .message("OK")
                            .body(okhttp3.ResponseBody.create("application/json".toMediaType(), mockResponseJson))
                            .build()
                    } else {
                        return@addInterceptor okhttp3.Response.Builder()
                            .request(request)
                            .protocol(okhttp3.Protocol.HTTP_1_1)
                            .code(200)
                            .message("OK")
                            .body(okhttp3.ResponseBody.create("application/json".toMediaType(), mockResponseJson))
                            .build()
                    }
                }"""

good_interceptor = """                val finalResponse = response
                if (finalResponse == null || !finalResponse.isSuccessful) {
                    val fallbackMsg = if (finalResponse?.code == 429) {
                        "Gemini API Quota Exceeded (429). Please check your billing/quota."
                    } else if (finalResponse?.code == 404) {
                        "Gemini API Model Not Found (404). Check the API key permissions."
                    } else if (finalResponse?.code == 403 || finalResponse?.code == 401) {
                        "Gemini API Invalid Key (401/403). Please check your API key."
                    } else {
                        "Gemini API Error: ${finalResponse?.code} ${finalResponse?.message}"
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
                    if (finalResponse != null) {
                        return@addInterceptor finalResponse.newBuilder()
                            .code(200)
                            .message("OK")
                            .body(okhttp3.ResponseBody.create("application/json".toMediaType(), mockResponseJson))
                            .build()
                    } else {
                        return@addInterceptor okhttp3.Response.Builder()
                            .request(request)
                            .protocol(okhttp3.Protocol.HTTP_1_1)
                            .code(200)
                            .message("OK")
                            .body(okhttp3.ResponseBody.create("application/json".toMediaType(), mockResponseJson))
                            .build()
                    }
                }"""

if bad_interceptor in content:
    content = content.replace(bad_interceptor, good_interceptor)
else:
    print("Not found")

with open("app/src/main/java/com/example/api/GeminiService.kt", "w") as f:
    f.write(content)
