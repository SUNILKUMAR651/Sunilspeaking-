package com.example.ui.screens

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
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.viewmodel.LexiViewModel
import com.example.data.WordObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

private fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
    val lhsLength = lhs.length
    val rhsLength = rhs.length
    var cost = IntArray(lhsLength + 1) { it }
    var newCost = IntArray(lhsLength + 1)
    for (i in 1..rhsLength) {
        newCost[0] = i
        for (j in 1..lhsLength) {
            val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
            val costReplace = cost[j - 1] + match
            val costInsert = cost[j] + 1
            val costDelete = newCost[j - 1] + 1
            newCost[j] = minOf(costInsert, costDelete, costReplace)
        }
        val swap = cost
        cost = newCost
        newCost = swap
    }
    return cost[lhsLength]
}

private fun calculateAccuracy(expected: String, actual: String): Float {
    if (expected.isEmpty()) return 0f
    val e = expected.lowercase().replace(Regex("[^a-z]"), "")
    val a = actual.lowercase().replace(Regex("[^a-z]"), "")
    if (e.isEmpty() || a.isEmpty()) return 0f
    val distance = levenshteinDistance(e, a)
    val maxLength = maxOf(e.length, a.length)
    val score = ((maxLength - distance).toFloat() / maxLength) * 100f
    return score.coerceIn(0f, 100f)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractivePracticeScreen(
    title: String,
    viewModel: LexiViewModel,
    onBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val allWords by viewModel.displayedWords.collectAsState()
    
    // Extract part number from title to select words
    val partNumber = title.substringAfterLast("Part ").toIntOrNull() ?: 1
    val itemsPerPart = 15
    val startIndex = ((partNumber - 1) * itemsPerPart).coerceAtLeast(0)
    
    // Fallback to allWords if indices are out of bounds, but try to get 15 words
    val practiceWords = if (allWords.isNotEmpty() && startIndex < allWords.size) {
        allWords.drop(startIndex).take(itemsPerPart)
    } else if (allWords.isNotEmpty()) {
        allWords.take(itemsPerPart)
    } else {
        emptyList()
    }
    
    var currentIndex by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var attemptCount by remember { mutableIntStateOf(0) }
    val accuracy = if (attemptCount > 0) (correctCount * 100) / attemptCount else 0
    
    val currentWord = practiceWords.getOrNull(currentIndex)
    var showDefinition by remember { mutableStateOf(false) }
    
    var isRecording by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var evaluationMessage by remember { mutableStateOf("") }
    var evaluationColor by remember { mutableStateOf(Color.Black) }
    
    val scope = rememberCoroutineScope()
    
    // Text to Speech
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        tts = textToSpeech
        onDispose { textToSpeech.shutdown() }
    }
    
    fun playWord() {
        currentWord?.let {
            tts?.speakWithVoice(it.word, userProfile.useFemaleVoice, null)
        }
    }
    
    val hasRecordPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        }
    }
    
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
        }
    }
    
    DisposableEffect(context) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isRecording = false }
            override fun onError(error: Int) {
                isRecording = false
                recognizedText = "Could not hear you. Please try again."
                evaluationMessage = "Error detecting speech"
                evaluationColor = Color(0xFFE53935)
            }
            override fun onResults(results: Bundle?) {
                isRecording = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty() && currentWord != null) {
                    val heard = matches[0]
                    recognizedText = heard
                    val expected = currentWord.word
                    val score = calculateAccuracy(expected, heard)
                    
                    attemptCount++
                    if (score >= 80f) {
                        correctCount++
                        evaluationMessage = "Excellent!"
                        evaluationColor = Color(0xFF43A047)
                        
                        // Auto-advance on success after a short delay
                        scope.launch {
                            delay(1000)
                            if (currentIndex < practiceWords.size - 1) {
                                currentIndex++
                                showDefinition = false
                                evaluationMessage = ""
                                recognizedText = ""
                            }
                        }
                    } else if (score >= 50f) {
                        evaluationMessage = "Close, but try again."
                        evaluationColor = Color(0xFFFDD835)
                    } else {
                        evaluationMessage = "Keep practicing!"
                        evaluationColor = Color(0xFFE53935)
                    }
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        onDispose { speechRecognizer.destroy() }
    }
    
    fun startListening() {
        if (!hasRecordPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        isRecording = true
        recognizedText = ""
        evaluationMessage = ""
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (practiceWords.isEmpty()) "Practice" else "Word ${currentIndex + 1} / ${practiceWords.size}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { playWord() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp, 
                                contentDescription = "Play",
                                tint = Color(0xFF43A047),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFAFAFA)
                )
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        if (practiceWords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No words available for practice yet.")
            }
            return@Scaffold
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress Bar
            LinearProgressIndicator(
                progress = { if (practiceWords.isEmpty()) 0f else (currentIndex + 1).toFloat() / practiceWords.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF673AB7),
                trackColor = Color(0xFFE0E0E0)
            )
            
            // Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(value = correctCount.toString(), label = "CORRECT", color = Color(0xFF673AB7))
                    StatItem(value = attemptCount.toString(), label = "ATTEMPTS", color = Color(0xFF673AB7))
                    StatItem(value = "$accuracy%", label = "ACCURACY", color = Color(0xFF673AB7))
                }
            }
            
            // Word Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .weight(1f, fill = false),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    currentWord?.let { word ->
                        // POS Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF26A69A),
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                            Text(
                                text = word.partOfSpeech.lowercase(),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Word
                        Text(
                            text = word.word,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Phonetic
                        Text(
                            text = word.phonetic.ifEmpty { "/phonetic/" },
                            fontSize = 18.sp,
                            color = Color(0xFF9C27B0)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Example
                        val exampleText = word.definitions.firstOrNull()?.example
                        val example = if (!exampleText.isNullOrBlank()) exampleText else "No example available."
                        Text(
                            text = example,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF64748B),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Show Definition toggle
                        Row(
                            modifier = Modifier.clickable { showDefinition = !showDefinition }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight, // Placeholder for question mark/info icon
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Show Definition",
                                color = Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                        }
                        
                        AnimatedVisibility(visible = showDefinition) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 16.dp)) {
                                Text(
                                    text = word.definitions.firstOrNull()?.meaning ?: "",
                                    fontSize = 16.sp,
                                    color = Color(0xFF334155),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                if (word.memoryHook.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFFF8E1)
                                    ) {
                                        Text(
                                            text = "💡 Hook: ${word.memoryHook}",
                                            fontSize = 14.sp,
                                            color = Color(0xFFF57F17),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.padding(12.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Feedback Text
            if (evaluationMessage.isNotEmpty()) {
                Text(
                    text = evaluationMessage,
                    color = evaluationColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (recognizedText.isNotEmpty()) {
                Text(
                    text = "You said: \"$recognizedText\"",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Text(
                    text = "Click the speaker to listen, then the mic to pronounce the word.",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp).padding(bottom = 16.dp)
                )
            }
            
            // Bottom Controls
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 16.dp).padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Listen Button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF6B4EE6),
                    modifier = Modifier.size(64.dp).clickable { playWord() },
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                // Mic Button
                Surface(
                    shape = CircleShape,
                    color = if (isRecording) Color(0xFFE53935) else Color(0xFF26A69A),
                    modifier = Modifier.size(80.dp).clickable { 
                        if (!isRecording) startListening() 
                        else {
                            speechRecognizer.stopListening()
                            isRecording = false
                        }
                    },
                    shadowElevation = 8.dp
                ) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = "Speak",
                        tint = Color.White,
                        modifier = Modifier.padding(20.dp)
                    )
                }
                
                // Next Button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFB39DDB),
                    modifier = Modifier.size(64.dp).clickable { 
                        if (currentIndex < practiceWords.size - 1) {
                            currentIndex++
                            showDefinition = false
                            evaluationMessage = ""
                            recognizedText = ""
                        } else {
                            onBack()
                        }
                    },
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
    }
}
