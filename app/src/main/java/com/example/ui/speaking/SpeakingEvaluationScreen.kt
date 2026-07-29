package com.example.ui.speaking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import com.example.utils.speakWithVoice
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.viewmodel.LexiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SpeakingEvaluationScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // UI State
    var progress by remember { mutableFloatStateOf(0.2f) }
    var isRecording by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    
    // Fake lessons for gamified experience
    val sentences = listOf(
        "Hello, my name is Anna.",
        "I would like to order a coffee.",
        "Where is the nearest train station?",
        "Nice to meet you!"
    )
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentSentence = sentences[currentIndex]
    
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }
    
    var hasMicPermission by remember { 
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) 
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasMicPermission = isGranted
    }

    DisposableEffect(speechRecognizer) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isRecording = false }
            override fun onError(error: Int) { isRecording = false }
            override fun onResults(results: Bundle?) {
                isRecording = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                    showResult = true
                    // simple check for demo
                    isSuccess = recognizedText.lowercase().contains("hello") || recognizedText.length > 5
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        onDispose { speechRecognizer.destroy() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)) // Light grayish blue background
    ) {
        // Top Progress Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(0xFF58CC02),
                trackColor = Color(0xFFE5E5E5),
                strokeCap = StrokeCap.Round
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = "Now say the sentence!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Character & Bubble Container
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Character Placeholder (Ideally an Image, using Box for now)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("👩‍🏫", fontSize = 64.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Speech Bubble Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().border(2.dp, Color(0xFFE5E5E5), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1CB0F6),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                tts?.speakWithVoice(currentSentence, userProfile.useFemaleVoice, null)
                            }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp, 
                            contentDescription = "Listen", 
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "\"$currentSentence\"",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Recognition Result or Warning
        if (!hasMicPermission) {
            Surface(
                color = Color(0xFFFFE5E5),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp).fillMaxWidth().border(1.dp, Color(0xFFFF4B4B), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.WarningAmber, contentDescription = "Warning", tint = Color(0xFFFF4B4B))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "No microphone permission on this device.",
                        color = Color(0xFFFF4B4B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else if (showResult) {
            Text(
                text = recognizedText,
                fontSize = 18.sp,
                color = if (isSuccess) Color(0xFF58CC02) else Color(0xFFFF4B4B),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp).align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
        }
        
        // Bottom Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            if (showResult) {
                Button(
                    onClick = {
                        showResult = false
                        recognizedText = ""
                        if (isSuccess) {
                            progress = minOf(1f, progress + 0.25f)
                            currentIndex = (currentIndex + 1) % sentences.size
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSuccess) Color(0xFF58CC02) else Color(0xFFFF4B4B)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(if (isSuccess) "CONTINUE" else "TRY AGAIN", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else {
                Button(
                    onClick = {
                        if (!hasMicPermission) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            return@Button
                        }
                        if (isRecording) {
                            speechRecognizer.stopListening()
                            isRecording = false
                        } else {
                            recognizedText = ""
                            speechRecognizer.startListening(speechRecognizerIntent)
                            isRecording = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color(0xFFFF4B4B) else Color(0xFF1CB0F6)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Mic, contentDescription = "Mic", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRecording) "RECORDING..." else "TAP TO SPEAK", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
