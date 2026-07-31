package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import java.util.Locale
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import com.example.utils.speakWithVoice


@Composable
fun PracticeRunScreen(lessonId: Int, viewModel: LexiViewModel, onBack: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val displayName = userProfile.name.ifBlank { "User" }.split(" ").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "User"
    val title = "${displayName}'s Practice Run"
    
    val sentences = remember {
        List(50) { index -> 
            if (index == 0) "Hello, my name is ${displayName}." 
            else if (index == 1) "I am happy to introduce myself."
            else if (index == 2) "I am learning every day."
            else "This is practice sentence number ${index + 1}." 
        }
    }
    
    
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val context = LocalContext.current

    var hasMicPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) }
    
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasMicPermission = isGranted
    }

    var isRecording by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    
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
    
    var currentSentenceIndex by remember { mutableIntStateOf(0) }

    
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(40.dp).clickable(onClick = onBack)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF6B4EE6), modifier = Modifier.padding(8.dp))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = title,
                    color = Color(0xFF4B4B4B),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$currentSentenceIndex", fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6), fontSize = 18.sp)
                    Text("DONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val targetW = sentences[currentSentenceIndex].replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                    val recW = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                    val currentScore = if (recognizedText.isEmpty() || targetW.isEmpty()) 0 else {
                        val matches = recW.count { targetW.contains(it) }
                        (matches.toFloat() / targetW.size * 100).toInt().coerceIn(0, 100)
                    }
                    val scoreColor = if (currentScore > 80) Color(0xFF58CC02) else if (currentScore > 40) Color(0xFFFF9600) else if (currentScore > 0) Color(0xFFFF4B4B) else Color(0xFF1CB0F6)
                    Text("${currentScore}%", fontWeight = FontWeight.Bold, color = scoreColor, fontSize = 18.sp)
                    Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = scoreColor)
                }
            }
        },
        containerColor = Color(0xFFFFF0F5)
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.padding(padding).fillMaxSize()) {
            val isWide = maxWidth > 800.dp
            
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(2f)) {
                        PracticeMainPanel(
                            sentence = sentences[currentSentenceIndex],
                            currentIndex = currentSentenceIndex + 1,
                            totalCount = sentences.size,
                            tts = tts,
                            useFemaleVoice = userProfile.useFemaleVoice,
                            isRecording = isRecording,
                            recognizedText = recognizedText,
                            onToggleRecord = {
                                if (isRecording) {
                                    speechRecognizer.stopListening()
                                    isRecording = false
                                } else {
                                    if (hasMicPermission) {
                                        recognizedText = ""
                                        speechRecognizer.startListening(speechRecognizerIntent)
                                        isRecording = true
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            onNext = {
                                if (currentSentenceIndex < sentences.size - 1) {
                                    currentSentenceIndex++
                                    recognizedText = ""
                                } else {
                                    onBack()
                                }
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PracticeListPanel(sentences, currentSentenceIndex)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PracticeMainPanel(
                        sentence = sentences[currentSentenceIndex],
                        currentIndex = currentSentenceIndex + 1,
                        totalCount = sentences.size,
                        tts = tts,
                        useFemaleVoice = userProfile.useFemaleVoice,
                        isRecording = isRecording,
                        recognizedText = recognizedText,
                        onToggleRecord = {
                            if (isRecording) {
                                speechRecognizer.stopListening()
                                isRecording = false
                            } else {
                                if (hasMicPermission) {
                                    recognizedText = ""
                                    speechRecognizer.startListening(speechRecognizerIntent)
                                    isRecording = true
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        onNext = {
                            if (currentSentenceIndex < sentences.size - 1) {
                                currentSentenceIndex++
                                recognizedText = ""
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Bottom Action Bar
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { 
                        if (currentSentenceIndex < sentences.size - 1) currentSentenceIndex++ 
                    }) {
                        Text("SKIP", color = Color(0xFF6B4EE6), fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Button(
                        onClick = {
                            if (isRecording) {
                                speechRecognizer.stopListening()
                                isRecording = false
                            } else {
                                if (hasMicPermission) {
                                    recognizedText = ""
                                    speechRecognizer.startListening(speechRecognizerIntent)
                                    isRecording = true
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) Color(0xFFFF4B4B) else Color(0xFF6B4EE6)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(56.dp).widthIn(min = 200.dp)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = "Speak")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isRecording) "Listening..." else "Tap to speak", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFE5E5),
                        modifier = Modifier.size(56.dp).clickable { recognizedText = "" }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Retry", tint = Color(0xFFFF4B4B))
                            Text("RETRY", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF4B4B))
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Simulation Button for Emulator Testing
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF0F5FF),
                        modifier = Modifier.size(56.dp).clickable {
                            // Simulate voice input (perfect, partial, wrong)
                            val target = sentences[currentSentenceIndex]
                            val random = (0..2).random()
                            recognizedText = when (random) {
                                0 -> target // Perfect
                                1 -> target.split(" ").shuffled().take(target.split(" ").size / 2 + 1).joinToString(" ") + " incorrect word" // Partial
                                else -> "I am saying something completely wrong" // Wrong
                            }
                        }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Filled.Star, contentDescription = "Simulate", tint = Color(0xFF1CB0F6))
                            Text("SIM", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1CB0F6))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeMainPanel(sentence: String, currentIndex: Int, totalCount: Int, tts: TextToSpeech?, useFemaleVoice: Boolean, isRecording: Boolean, recognizedText: String, onToggleRecord: () -> Unit, onNext: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF6B4EE6),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(currentIndex.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text("SENTENCE $currentIndex OF $totalCount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6))
                    Text("Ready?", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF6B4EE6))
                    Surface(color = Color(0xFF6B4EE6), shape = RoundedCornerShape(4.dp)) {
                        Text("Speak the sentence clearly and confidently.", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(4.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
                                    Text("🔊 Speak this line", color = Color(0xFF6B4EE6), fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                            tts?.speakWithVoice(sentence, useFemaleVoice, null)
                        })
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sentence chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val recognizedWordsListChips = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                sentence.split(" ").forEach { word ->
                    val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                    val isSpoken = recognizedWordsListChips.contains(cleanWord)
                    val isWrong = !isSpoken && !isRecording && recognizedText.isNotEmpty()
                    
                    val borderColor = if (isSpoken) Color(0xFF58CC02) else if (isWrong) Color(0xFFFF4B4B) else Color(0xFFE5E5E5)
                    val bgColor = if (isSpoken) Color(0xFFD7FFB8) else if (isWrong) Color(0xFFFFE5E5) else Color.White
                    val textColor = if (isSpoken) Color(0xFF58CC02) else if (isWrong) Color(0xFFFF4B4B) else Color(0xFF4B4B4B)
                    
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                        color = bgColor
                    ) {
                        Text(word, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // You Said / Score box
            Row(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C323A)),
                    color = Color(0xFF181C20),
                    modifier = Modifier.weight(1f).height(100.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("YOU SAID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val targetWords = sentence.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        
                        if (recognizedText.isEmpty()) {
                            Text("Waiting for your voice...", color = Color.DarkGray, fontSize = 16.sp)
                        } else {
                            val annotatedString = buildAnnotatedString {
                                recognizedText.split(" ").forEach { word ->
                                    val cleanWord = word.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                                    val isCorrect = targetWords.contains(cleanWord)
                                    withStyle(style = SpanStyle(color = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B))) {
                                        append("$word ")
                                    }
                                }
                            }
                            Text(text = annotatedString, fontSize = 16.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C323A)),
                    color = Color(0xFF181C20),
                    modifier = Modifier.width(100.dp).height(100.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        val targetWords = sentence.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        val recognizedWordsList = recognizedText.replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase().split(" ").filter { it.isNotBlank() }
                        val score = if (recognizedText.isEmpty() || targetWords.isEmpty()) 0 else {
                            val matches = recognizedWordsList.count { targetWords.contains(it) }
                            (matches.toFloat() / targetWords.size * 100).toInt().coerceIn(0, 100)
                        }
                        val scoreColor = if (score > 80) Color(0xFF58CC02) else if (score > 40) Color(0xFFFF9600) else if (score > 0) Color(0xFFFF4B4B) else Color(0xFF1CB0F6)
                        Text("${score}%", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = scoreColor)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Voice idle", color = Color.Gray, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
                ) {
                    Text("0:00", color = Color(0xFF6B4EE6), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun PracticeListPanel(sentences: List<String>, currentIndex: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp) // Leave space for bottom bar
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("All sentences", color = Color(0xFF1CB0F6), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Full 50-line run", color = Color.Gray, fontSize = 12.sp)
                }
                Row {
                    Text("🏆 0", fontWeight = FontWeight.Bold, color = Color(0xFFFF9600))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(sentences) { index, sentence ->
                    val isActive = index == currentIndex
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isActive) Color(0xFFF0F5FF) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isActive) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)
                                ) {
                                    Text(
                                        "${index + 1}",
                                        color = if (isActive) Color.White else Color.Gray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isActive) "ACTIVE" else "NEXT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(sentence, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4B4B4B))
                        }
                    }
                }
            }
        }
    }
}
