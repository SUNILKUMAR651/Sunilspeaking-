import re
path = "app/src/main/java/com/example/api/GeminiService.kt"
with open(path, "r") as f:
    content = f.read()

bad = """                val finalResponse = response ?: throw error ?: java.io.IOException("Network request failed")
                if (!finalResponse.isSuccessful) {"""

good = """                val finalResponse = response
                if (finalResponse == null || !finalResponse.isSuccessful) {"""

content = content.replace(bad, good)

bad2 = """                    return@addInterceptor finalResponse.newBuilder()
                        .code(200)
                        .message("OK")
                        .body(okhttp3.ResponseBody.create("application/json".toMediaType(), mockResponseJson))
                        .build()
                }
                return@addInterceptor finalResponse"""

good2 = """                    if (finalResponse != null) {
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
                }
                return@addInterceptor finalResponse!!"""

content = content.replace(bad2, good2)

with open(path, "w") as f:
    f.write(content)
