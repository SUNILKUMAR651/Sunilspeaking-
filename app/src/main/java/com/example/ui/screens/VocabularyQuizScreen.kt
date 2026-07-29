package com.example.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import com.example.utils.speakWithVoice
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.WordObject
import com.example.viewmodel.LexiViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyQuizScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val words by viewModel.allWords.collectAsState()
    val context = LocalContext.current
    
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    
    // Timer state
    var timeLeft by remember { mutableIntStateOf(15) }
    
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
    DisposableEffect(Unit) {
        onDispose { toneGen.release() }
    }
    
    // Text to Speech
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val ttsContext = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            context.createAttributionContext("audio_playback")
        } else {
            context
        }
        val textToSpeech = TextToSpeech(ttsContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        tts = textToSpeech
        onDispose { textToSpeech.shutdown() }
    }
    
    // Create some dummy questions based on words
    val questions = remember(words) {
        if (words.size >= 4) {
            words.shuffled().take(5).map { word ->
                val correctAnswer = word.definitions.firstOrNull()?.meaning ?: "No meaning"
                val wrongOptions = words.filter { it.word != word.word }.shuffled().take(3).map { it.definitions.firstOrNull()?.meaning ?: "No meaning" }
                val options = (wrongOptions + correctAnswer).shuffled()
                QuizQuestion(
                    word = word.word,
                    correctAnswer = correctAnswer,
                    options = options
                )
            }
        } else {
            emptyList()
        }
    }
    
    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    
    // Timer Logic
    LaunchedEffect(currentQuestionIndex, showResult) {
        if (!showResult && questions.isNotEmpty() && currentQuestionIndex < questions.size) {
            timeLeft = 15
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            // Time is up
            showResult = true
            toneGen.startTone(ToneGenerator.TONE_SUP_ERROR, 200)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vocabulary Quiz", fontWeight = FontWeight.Bold) },
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
        ) {
            if (questions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Not enough words for a quiz. Please add more words.", color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { 
                                com.example.data.LexiconDatabase.words.take(5).forEach {
                                    viewModel.addWord(it)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Add Demo Words to Firebase", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            } else if (currentQuestionIndex < questions.size) {
                val question = questions[currentQuestionIndex]
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Timer display
                    val timerColor = if (timeLeft <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    Text(
                        text = "⏱ $timeLeft s",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = timerColor
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress Bar
                LinearProgressIndicator(
                    progress = { timeLeft / 15f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = if (timeLeft <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("What is the meaning of:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = question.word,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        tts?.speakWithVoice(question.word, userProfile.useFemaleVoice, null)
                                    }
                            ) {
                                Icon(
                                    Icons.Filled.VolumeUp,
                                    contentDescription = "Pronounce",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(question.options) { option ->
                        val isSelected = selectedAnswer == option
                        val isCorrect = option == question.correctAnswer
                        
                        val containerColor = when {
                            !showResult && isSelected -> MaterialTheme.colorScheme.primaryContainer
                            !showResult -> MaterialTheme.colorScheme.surface
                            showResult && isCorrect -> MaterialTheme.colorScheme.primary
                            showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                            showResult && !isCorrect && option == question.correctAnswer -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) // highlight correct if missed
                            else -> MaterialTheme.colorScheme.surface
                        }
                        
                        val contentColor = when {
                            !showResult && isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            !showResult -> MaterialTheme.colorScheme.onSurface
                            showResult && isCorrect -> MaterialTheme.colorScheme.onPrimary
                            showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
                            showResult && !isCorrect && option == question.correctAnswer -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                        
                        Card(
                            onClick = { 
                                if (!showResult) {
                                    selectedAnswer = option
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = containerColor)
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(16.dp),
                                color = contentColor,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = {
                        if (showResult) {
                            currentQuestionIndex++
                            selectedAnswer = null
                            showResult = false
                        } else {
                            if (selectedAnswer == question.correctAnswer) {
                                score++
                                toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
                            } else {
                                toneGen.startTone(ToneGenerator.TONE_SUP_ERROR, 300)
                            }
                            showResult = true
                        }
                    },
                    enabled = selectedAnswer != null || showResult,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (showResult) "Next Question" else "Check Answer")
                }
            } else {
                // Result Screen
                LaunchedEffect(Unit) {
                    if (score > 0) {
                        viewModel.recordLessonCompletion(score * 5, "vocabulary")
                    }
                }
                
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Quiz Complete!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your score: $score / ${questions.size}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = {
                        // Reset quiz
                        currentQuestionIndex = 0
                        score = 0
                        selectedAnswer = null
                        showResult = false
                    }) {
                        Text("Retake Quiz")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = onBack) {
                        Text("Back to Practice")
                    }
                }
            }
        }
    }
}

data class QuizQuestion(
    val word: String,
    val correctAnswer: String,
    val options: List<String>
)
