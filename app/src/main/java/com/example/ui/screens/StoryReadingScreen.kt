package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.viewmodel.LexiViewModel
import java.util.Locale

data class StoryLevel(val id: Int, val title: String, val content: String)

val storyLevels = listOf(
    StoryLevel(1, "Lesson 1: Introductions", "Hello, my name is John. I am a student. I like to learn new languages."),
    StoryLevel(2, "Lesson 2: At the Cafe", "I would like to order a cup of coffee and a piece of chocolate cake, please."),
    StoryLevel(3, "Lesson 3: The Weather", "Today the weather is very beautiful. The sun is shining brightly in the clear blue sky."),
    StoryLevel(4, "Lesson 4: Travel Plans", "Next week, I am traveling to Paris. I am very excited to see the Eiffel Tower and eat croissants."),
    StoryLevel(5, "Lesson 5: Daily Routine", "Every morning, I wake up at seven o'clock, brush my teeth, and have a healthy breakfast.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryReadingScreen(
    viewModel: LexiViewModel,
    lessonId: Int,
    onBack: () -> Unit,
    onNextLesson: () -> Unit
) {
    val context = LocalContext.current
    val story = storyLevels.find { it.id == lessonId } ?: storyLevels.first()
    
    var isRecording by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var evaluationComplete by remember { mutableStateOf(false) }
    
    var wordScores by remember { mutableStateOf<List<Boolean>>(emptyList()) }
    var accuracyScore by remember { mutableStateOf(0f) }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
        }
    }
    
    fun evaluateReading(expected: String, actual: String) {
        val expectedWords = expected.lowercase().replace(Regex("[^a-z ]"), "").split(Regex("\\s+")).filter { it.isNotBlank() }
        val actualWords = actual.lowercase().replace(Regex("[^a-z ]"), "").split(Regex("\\s+")).filter { it.isNotBlank() }
        
        val scores = expectedWords.map { expectedWord ->
            actualWords.any { actualWord ->
                // Check if they are similar enough (Levenshtein distance)
                val e = expectedWord
                val a = actualWord
                val distance = levenshteinDistance(e, a)
                val maxLength = maxOf(e.length, a.length)
                val score = if (maxLength > 0) ((maxLength - distance).toFloat() / maxLength) * 100f else 0f
                score > 70f // Threshold for correctness
            }
        }
        wordScores = scores
        val correctCount = scores.count { it }
        accuracyScore = if (expectedWords.isNotEmpty()) (correctCount.toFloat() / expectedWords.size) * 100f else 0f
        evaluationComplete = true
        
        if (accuracyScore >= 80f) {
            viewModel.recordLessonCompletion(20, "reading") // Give XP for good reading
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
            }
            override fun onResults(results: Bundle?) {
                isRecording = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val resultText = matches?.firstOrNull() ?: ""
                recognizedText = resultText
                if (resultText.isNotBlank()) {
                    evaluateReading(story.content, resultText)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        onDispose { speechRecognizer.destroy() }
    }

    var hasRecordPermission by remember { 
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) 
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasRecordPermission = granted }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(story.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text(
                "Read the following text out loud:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.padding(24.dp)) {
                    if (evaluationComplete && wordScores.isNotEmpty()) {
                        // Display annotated text
                        val words = story.content.split(Regex("(?<=\\s)|(?=\\s)"))
                        var wordIndex = 0
                        val annotatedText = buildAnnotatedString {
                            words.forEach { token ->
                                if (token.isNotBlank()) {
                                    val isCorrect = wordScores.getOrNull(wordIndex) ?: false
                                    val color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                                    withStyle(style = SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
                                        append(token)
                                    }
                                    wordIndex++
                                } else {
                                    append(token) // spaces
                                }
                            }
                        }
                        Text(
                            text = annotatedText,
                            style = MaterialTheme.typography.headlineSmall,
                            lineHeight = 36.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Default text
                        Text(
                            text = story.content,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 36.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (evaluationComplete) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScoreIndicator("Accuracy", accuracyScore)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            "You said: \"$recognizedText\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        if (accuracyScore >= 70f) {
                            Button(
                                onClick = onNextLesson,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Great Job! Next Lesson")
                            }
                        } else {
                            Button(
                                onClick = {
                                    evaluationComplete = false
                                    recognizedText = ""
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(if (isRecording) "Listening..." else "Tap the mic and start reading", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { 
                                if (!hasRecordPermission) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    if (isRecording) {
                                        speechRecognizer.stopListening()
                                    } else {
                                        isRecording = true
                                        evaluationComplete = false
                                        recognizedText = ""
                                        speechRecognizer.startListening(speechRecognizerIntent)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                                contentDescription = if (isRecording) "Stop" else "Speak",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
    val len0 = lhs.length + 1
    val len1 = rhs.length + 1
    var cost = IntArray(len0)
    var newcost = IntArray(len0)
    for (i in 0 until len0) cost[i] = i
    for (j in 1 until len1) {
        newcost[0] = j
        for (i in 1 until len0) {
            val match = if (lhs[i - 1] == rhs[j - 1]) 0 else 1
            val cost_replace = cost[i - 1] + match
            val cost_insert = cost[i] + 1
            val cost_delete = newcost[i - 1] + 1
            newcost[i] = minOf(minOf(cost_insert, cost_delete), cost_replace)
        }
        val swap = cost; cost = newcost; newcost = swap
    }
    return cost[len0 - 1]
}

@Composable
fun ScoreIndicator(label: String, score: Float) {
    androidx.compose.material3.Text("$label: ${score.toInt()}%", color = androidx.compose.ui.graphics.Color.Black)
}
