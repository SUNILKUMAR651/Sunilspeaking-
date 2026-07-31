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
    @POST("v1beta/models/gemini-3.1-flash-lite:generateContent")
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
                val mockResponses = listOf(
                    "That's very interesting! Can you tell me more?",
                    "I see! How does that make you feel?",
                    "That is a great point. I agree with you.",
                    "Could you elaborate on that?",
                    "I'm currently in offline mock mode, but I hear you clearly! You said something great.",
                    "Awesome! Let's keep practicing.",
                    "Very good! Your pronunciation is getting better."
                )
                val randomResponse = mockResponses.random()
                val mockResponseJson = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "${randomResponse}"
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
                val finalResponse = response
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
                    val mockResponseJson = """
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
                    """.trimIndent()
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
                }
                return@addInterceptor finalResponse!!
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
