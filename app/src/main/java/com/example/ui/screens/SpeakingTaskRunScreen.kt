package com.example.ui.screens
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.example.utils.speakWithVoice
import java.util.Locale

val mockTexts = mapOf(
    1 to "I am a dedicated professional with over five years of experience in project management. In my previous role, I successfully led a team of ten people and delivered three major product launches ahead of schedule. I am passionate about optimizing workflows and I am looking for a position where I can continue to drive operational efficiency.",
    2 to "My greatest strength is my ability to communicate complex technical concepts to non technical stakeholders. I pride myself on bridging the gap between the engineering team and the marketing department, ensuring everyone is aligned on the product vision."
)

@Composable
fun SpeakingTaskRunScreen(taskId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    var hasMicPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) }
    
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasMicPermission = isGranted
    }
    
    val targetText = mockTexts[taskId] ?: mockTexts[1]!!
    val targetWords = targetText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ")

    var isRecording by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    val recognizedWords = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ")

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isRecording = false }
            override fun onError(error: Int) { isRecording = false }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                }
                isRecording = false
            }
            override fun onPartialResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        onDispose {
            speechRecognizer.stopListening()
            speechRecognizer.destroy()
        }
    }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // ready
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFE2E8F0),
                modifier = Modifier.size(40.dp).clickable(onClick = onBack)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.padding(8.dp), tint = Color(0xFF1E293B))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Filled.BusinessCenter, contentDescription = null, tint = Color(0xFF64748B))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (taskId == 1) "Tell me about yourself" else "Your Greatest Strength",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text("General • easy • ~60s", color = Color(0xFF64748B), fontSize = 12.sp)
            }
        }

        HorizontalDivider(color = Color(0xFFE2E8F0))

        Column(modifier = Modifier.padding(24.dp).weight(1f).verticalScroll(rememberScrollState())) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Mic, contentDescription = null, tint = Color(0xFF6C63FF), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("READ THIS ALOUD", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                }
                Surface(
                    color = Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.clickable { tts?.speakWithVoice(targetText, false) }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color(0xFF3366FF), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Listen", color = Color(0xFF3366FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            val annotatedString = buildAnnotatedString {
                val words = targetText.split(" ")
                var currentMatchIndex = 0
                
                words.forEachIndexed { index, word ->
                    val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                    
                    if (recognizedText.isEmpty()) {
                        withStyle(style = SpanStyle(color = Color(0xFF1E293B))) { append("$word ") }
                    } else {
                        // Check if this word was spoken
                        // We will just do a simple check: if the word exists in the spoken text, mark green.
                        // If we have spoken N words, and this is the Nth word and it doesn't match, mark red.
                        
                        val isMatched = recognizedWords.contains(cleanWord)
                        
                        if (isMatched) {
                            withStyle(style = SpanStyle(color = Color(0xFF00C48C), fontWeight = FontWeight.Bold)) { append("$word ") }
                        } else {
                            // If it's not matched, but it's within the count of words spoken so far, mark it red as a mistake
                            // We use a heuristic: if recognizedWords has X words, we assume they tried to read up to word X
                            if (index < recognizedWords.size) {
                                withStyle(style = SpanStyle(color = Color(0xFFFF3333), textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) { append("$word ") }
                            } else {
                                withStyle(style = SpanStyle(color = Color(0xFF1E293B))) { append("$word ") }
                            }
                        }
                    }
                }
            }

            Text(
                text = annotatedString,
                fontSize = 24.sp,
                lineHeight = 36.sp,
                color = Color(0xFF1E293B)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF00C48C))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("MICROPHONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Text(if (hasMicPermission) "Ready" else "Permission needed", fontSize = 14.sp, color = Color(0xFF1E293B))
                        }
                    }
                }
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Wifi, contentDescription = null, tint = Color(0xFF3366FF))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("CONNECTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            Text("Ready to connect", fontSize = 14.sp, color = Color(0xFF1E293B))
                        }
                    }
                }
            }
        }
        
        Surface(
            color = Color(0xFFEEF2FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color(0xFFFF3333) else Color(0xFF6C63FF)
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRecording) "Stop" else "Tap to speak", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("We will check mic permission, then connect and start", color = Color(0xFF64748B), fontSize = 12.sp)
            }
        }
    }
}
