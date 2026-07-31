import re

content = """package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.viewmodel.LexiViewModel
import com.example.api.RetrofitClient
import com.example.api.GenerateContentRequest
import com.example.api.Content
import com.example.api.Part
import com.example.BuildConfig
import kotlinx.coroutines.launch
import java.util.Locale
import com.example.utils.FishAudioPlayer
import com.example.utils.speakWithVoice

@Composable
fun AICallScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isMuted by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var isFemaleVoice by remember { mutableStateOf(true) }
    
    var aiMessage by remember { mutableStateOf("Connecting...") }
    
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    
    var conversationHistory by remember { mutableStateOf(listOf<Content>()) }
    
    var hasRecordPermission by remember { 
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) 
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasRecordPermission = granted }
    )
    
    fun startListening() {
        if (!hasRecordPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        if (isMuted || isSpeaking) return
        
        coroutineScope.launch {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
            }
            try {
                speechRecognizer.startListening(intent)
                isListening = true
                aiMessage = "Listening..."
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
    
    val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            isListening = false
        }
        override fun onError(error: Int) {
            isListening = false
            if (!isMuted && !isSpeaking) {
                coroutineScope.launch {
                    kotlinx.coroutines.delay(1000)
                    startListening()
                }
            }
        }
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val userText = matches[0]
                aiMessage = "..."
                isSpeaking = true
                coroutineScope.launch {
                    try {
                        val systemPrompt = "You are a friendly AI English teacher in a voice call. Keep responses conversational, concise, and helpful. Correct mistakes gently."
                        val newHistory = conversationHistory + Content(listOf(Part(userText)), role = "user")
                        val request = GenerateContentRequest(
                            contents = newHistory,
                            systemInstruction = Content(listOf(Part(systemPrompt)))
                        )
                        val responseObj = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                        val cleanResponse = responseObj.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Sorry, I missed that."
                        
                        conversationHistory = newHistory + Content(listOf(Part(cleanResponse)), role = "model")
                        aiMessage = cleanResponse
                        
                        FishAudioPlayer.playAudio(
                            context = context,
                            text = cleanResponse,
                            isFemale = isFemaleVoice,
                            fallbackTts = tts.value,
                            utteranceId = "response_${System.currentTimeMillis()}",
                            onStart = {
                                isSpeaking = true
                            },
                            onDone = {
                                isSpeaking = false
                                if (!isMuted) startListening()
                            }
                        )
                    } catch (e: Exception) {
                        isSpeaking = false
                        aiMessage = "Error connecting"
                        if (!isMuted) startListening()
                    }
                }
            } else {
                if (!isMuted && !isSpeaking) startListening()
            }
        }
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
    
    DisposableEffect(Unit) {
        speechRecognizer.setRecognitionListener(listener)
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale.US
            }
        }
        tts.value = ttsInstance
        
        startListening()
        
        onDispose {
            speechRecognizer.destroy()
            tts.value?.stop()
            tts.value?.shutdown()
            FishAudioPlayer.stop()
        }
    }
    
    // Animation for avatar pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking || isListening) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnimation"
    )

    Scaffold(
        containerColor = Color(0xFF0F1218)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "AI English Teacher",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isSpeaking) "Speaking..." else if (isListening) "Listening..." else "Connected",
                    color = Color(0xFF00E5FF),
                    fontSize = 16.sp
                )
            }
            
            // Avatar
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Color(0xFF1E222A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = if (isFemaleVoice) Color(0xFFFF4081) else Color(0xFF29B6F6)
                )
            }
            
            // Transcript / Status
            Text(
                text = aiMessage,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                minLines = 3
            )
            
            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle Voice
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { isFemaleVoice = !isFemaleVoice },
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF2A2E38), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SwapHoriz,
                            contentDescription = "Toggle Voice",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isFemaleVoice) "Female" else "Male",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
                
                // Mute Mic
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { 
                            isMuted = !isMuted 
                            if (isMuted) {
                                speechRecognizer.stopListening()
                                isListening = false
                            } else {
                                startListening()
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .background(if (isMuted) Color.White else Color(0xFF2A2E38), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                            contentDescription = "Mute",
                            tint = if (isMuted) Color.Black else Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isMuted) "Muted" else "Mute",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
                
                // End Call
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.Red, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "End",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/AICallScreen.kt", "w") as f:
    f.write(content)

