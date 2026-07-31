package com.example.api

import com.example.BuildConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>
)

@Serializable
data class Candidate(
    val content: Content
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            val url = request.url
            val key = url.queryParameter("key")
            
            if (key == null || key == "MY_GEMINI_API_KEY" || key.isEmpty()) {
                val mockResponseJson = """
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
                """.trimIndent()
                
                okhttp3.Response.Builder()
                    .request(request)
                    .protocol(okhttp3.Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(okhttp3.ResponseBody.create("application/json".toMediaType(), mockResponseJson))
                    .build()
            } else {
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
            }
        }
        .build()

    val service: GeminiApiService by lazy {
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}
