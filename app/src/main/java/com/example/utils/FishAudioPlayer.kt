package com.example.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object FishAudioPlayer {
    private val client = OkHttpClient()
    private val API_KEY = "c6fe65b595354573b51a572583381ada"
    private var mediaPlayer: MediaPlayer? = null

    suspend fun playAudio(
        context: Context,
        text: String,
        isFemale: Boolean,
        fallbackTts: TextToSpeech? = null,
        utteranceId: String? = null,
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null
    ) {
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("text", text)
                    put("format", "mp3")
                }
                
                val body = json.toString().toRequestBody("application/json".toMediaType())
                
                val request = Request.Builder()
                    .url("https://api.fish.audio/v1/tts")
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .post(body)
                    .build()
                
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful && response.body != null) {
                    val bytes = response.body!!.bytes()
                    if (bytes.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            playMp3Bytes(context, bytes, onStart, onDone)
                        }
                    } else {
                        fallback(text, isFemale, fallbackTts, utteranceId, onStart, onDone)
                    }
                } else {
                    val errorStr = response.body?.string() ?: ""
                    Log.e("FishAudioPlayer", "API Error: ${response.code} $errorStr")
                    fallback(text, isFemale, fallbackTts, utteranceId, onStart, onDone)
                }
            } catch (e: Exception) {
                Log.e("FishAudioPlayer", "Exception: ${e.message}")
                fallback(text, isFemale, fallbackTts, utteranceId, onStart, onDone)
            }
        }
    }

    private suspend fun fallback(text: String, isFemale: Boolean, tts: TextToSpeech?, utteranceId: String?, onStart: (() -> Unit)?, onDone: (() -> Unit)?) {
        withContext(Dispatchers.Main) {
            if (tts == null) {
                onDone?.invoke()
                return@withContext
            }
            onStart?.invoke()
            
            // Set up a temporary listener for this utterance if needed
            val listener = object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(id: String?) {}
                override fun onDone(id: String?) {
                    if (id == utteranceId) {
                        onDone?.invoke()
                    }
                }
                @Deprecated("Deprecated in Java")
                override fun onError(id: String?) {
                    if (id == utteranceId) {
                        onDone?.invoke()
                    }
                }
            }
            tts.setOnUtteranceProgressListener(listener)
            tts.speakWithVoice(text, isFemale, utteranceId)
        }
    }

    private fun playMp3Bytes(context: Context, bytes: ByteArray, onStart: (() -> Unit)?, onDone: (() -> Unit)?) {
        try {
            val tempFile = File.createTempFile("fish_audio", ".mp3", context.cacheDir)
            tempFile.deleteOnExit()
            FileOutputStream(tempFile).use { it.write(bytes) }

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(tempFile.absolutePath)
                prepare()
                onStart?.invoke()
                start()
                setOnCompletionListener { 
                    it.release() 
                    tempFile.delete()
                    onDone?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    onDone?.invoke()
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("FishAudioPlayer", "MediaPlayer Exception: ${e.message}")
            onDone?.invoke()
        }
    }
    
    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {}
    }
}
