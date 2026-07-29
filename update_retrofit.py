path = "app/src/main/java/com/example/api/GeminiService.kt"
with open(path, 'r') as f:
    content = f.read()

import re

old_client = """    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()"""

new_client = """    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            val url = request.url
            val key = url.queryParameter("key")
            
            if (key == null || key == "MY_GEMINI_API_KEY" || key.isEmpty()) {
                val mockResponseJson = \"\"\"
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "This is an automatic mock response because no API key was provided. To get real AI responses, please configure your Gemini API Key in the settings or Secrets panel."
                          }
                        ]
                      }
                    }
                  ]
                }
                \"\"\".trimIndent()
                
                okhttp3.Response.Builder()
                    .request(request)
                    .protocol(okhttp3.Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(okhttp3.ResponseBody.create("application/json".toMediaType(), mockResponseJson))
                    .build()
            } else {
                chain.proceed(request)
            }
        }
        .build()"""

content = content.replace(old_client, new_client)

with open(path, 'w') as f:
    f.write(content)

