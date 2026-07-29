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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.ui.components.ConfettiAnimation
import androidx.compose.foundation.layout.Box
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
import java.util.Locale

sealed class LessonExercise {
    data class NewWord(val word: String, val sentence: String) : LessonExercise()
    data class Listening(val audioText: String, val options: List<String>, val correctOption: String) : LessonExercise()
    data class SpeakingSentence(val sentence: String) : LessonExercise()
    data class Translation(val prompt: String, val options: List<String>, val correctOption: String) : LessonExercise()
    data class ArrangeWords(val prompt: String, val correctSentence: String, val shuffledWords: List<String>) : LessonExercise()
    data class FillInTheBlanks(val prompt: String, val sentenceParts: List<String>, val correctWord: String, val options: List<String>) : LessonExercise()
    data class MatchPairs(val prompt: String, val pairs: Map<String, String>) : LessonExercise()
    data class PhraseNarration(val title: String, val phrase: String, val explanation: String) : LessonExercise()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ActiveLessonScreen(
    lessonId: Int,
    viewModel: LexiViewModel,
    onFinish: () -> Unit,
    onClose: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    // Generate exercises based on lessonId
    val exercises = remember {
        val difficulty = (lessonId / 50) + 1
        val isProLevel = lessonId > 10
        val generated = mutableListOf<LessonExercise>()
        
        // Dynamic content based on level
        val words = if (difficulty < 3) listOf("Hello", "Name", "Friend", "Good") else listOf("Challenge", "Opportunity", "Perseverance", "Achievement")
        val advancedWords = listOf("Incomprehensible", "Magnificent", "Exquisite", "Flawless")
        
        // Increase number of exercises based on difficulty (10 to 20)
        val exerciseCount = 10 + (difficulty * 2).coerceAtMost(10)
        
        for (i in 0 until exerciseCount) {
            val typeRand = (0..100).random()
            
            // Gradually introduce harder exercises based on difficulty
            if (i == 0) {
                generated.add(LessonExercise.PhraseNarration(
                    title = "Grammar & Tense Focus",
                    phrase = if (difficulty > 3) "Past Perfect Continuous" else "Present Simple",
                    explanation = if (difficulty > 3) "Used to describe an action that started in the past and continued up until another time in the past." else "Used for facts, habits, and general truths."
                ))
            } else if (typeRand < 15) {
                val word = if (difficulty > 5) advancedWords.random() else words.random()
                generated.add(LessonExercise.NewWord(word, "Let's learn: $word"))
            } else if (typeRand < 30) {
                val pairs = if (difficulty > 4) mapOf("Big" to "Huge", "Fast" to "Quick", "Smart" to "Clever", "Hard" to "Difficult") else mapOf("Dog" to "Perro", "Cat" to "Gato", "Sun" to "Sol", "Moon" to "Luna")
                generated.add(LessonExercise.MatchPairs("Match the correct pairs", pairs))
            } else if (typeRand < 45) {
                val sentence = if (difficulty > 4) "The meticulous attention to detail is truly impressive." else "My name is Anna and I am happy."
                generated.add(LessonExercise.ArrangeWords("Arrange to form the correct sentence", sentence.lowercase().replace(".", ""), sentence.split(" ").shuffled()))
            } else if (typeRand < 60) {
                generated.add(LessonExercise.FillInTheBlanks("Complete the tense", listOf("I ", " to the store yesterday."), "went", listOf("go", "went", "going", "gone")))
            } else if (typeRand < 75) {
                val word = if (difficulty > 5) advancedWords.random() else words.random()
                generated.add(LessonExercise.Translation("Translate '$word'", listOf(word, "Random", "Other", "Wrong").shuffled(), word))
            } else if (typeRand < 90) {
                val word = if (difficulty > 5) advancedWords.random() else words.random()
                generated.add(LessonExercise.Listening("Listen carefully", listOf(word, "Something", "Nothing", "Everything").shuffled(), word))
            } else {
                val sentence = if (difficulty > 4) "It is imperative that we proceed with caution." else "I am learning a new language."
                generated.add(LessonExercise.SpeakingSentence(sentence))
            }
        }

        generated
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val progress = (currentIndex.toFloat() / exercises.size).coerceIn(0f, 1f)

    if (currentIndex >= exercises.size) {
        // Lesson completed
        LessonCompleteView(onFinish = onFinish)
        return
    }

    val currentExercise = exercises[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
    ) {
        // Top Progress Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
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
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { 
                viewModel.updateProfile(userProfile.copy(audioEnabled = !userProfile.audioEnabled)) 
            }) {
                Icon(
                    imageVector = if (userProfile.audioEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                    contentDescription = if (userProfile.audioEnabled) "Mute Audio" else "Unmute Audio",
                    tint = Color.Gray
                )
            }
        }

        // Animated content for exercises
        AnimatedContent(
            targetState = currentExercise,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
            },
            label = "ExerciseTransition",
            modifier = Modifier.weight(1f)
        ) { exercise ->
            when (exercise) {
                is LessonExercise.NewWord -> {
                    NewWordView(exercise, useFemaleVoice = userProfile.useFemaleVoice, audioEnabled = userProfile.audioEnabled) { currentIndex++ }
                }
                is LessonExercise.Listening -> {
                    ListeningView(exercise, useFemaleVoice = userProfile.useFemaleVoice, audioEnabled = userProfile.audioEnabled) { currentIndex++ }
                }
                is LessonExercise.SpeakingSentence -> {
                    SpeakingSentenceView(exercise, useFemaleVoice = userProfile.useFemaleVoice, audioEnabled = userProfile.audioEnabled) { currentIndex++ }
                }
                is LessonExercise.Translation -> {
                    TranslationView(exercise, useFemaleVoice = userProfile.useFemaleVoice, audioEnabled = userProfile.audioEnabled) { currentIndex++ }
                }
                is LessonExercise.ArrangeWords -> {
                    ArrangeWordsView(exercise, useFemaleVoice = userProfile.useFemaleVoice, audioEnabled = userProfile.audioEnabled) { currentIndex++ }
                }
                is LessonExercise.FillInTheBlanks -> {
                    FillInTheBlanksView(exercise, useFemaleVoice = userProfile.useFemaleVoice, audioEnabled = userProfile.audioEnabled) { currentIndex++ }
                }
                is LessonExercise.MatchPairs -> {
                    MatchPairsView(exercise, useFemaleVoice = userProfile.useFemaleVoice, audioEnabled = userProfile.audioEnabled) { currentIndex++ }
                }
                is LessonExercise.PhraseNarration -> {
                    PhraseNarrationView(exercise, useFemaleVoice = userProfile.useFemaleVoice, audioEnabled = userProfile.audioEnabled) { currentIndex++ }
                }
            }
        }
    }
}

@Composable
fun NewWordView(exercise: LessonExercise.NewWord, useFemaleVoice: Boolean, audioEnabled: Boolean = true, onContinue: () -> Unit) {
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US
        }
        onDispose { tts?.stop(); tts?.shutdown() }
    }
    
    // Auto play audio on show
    LaunchedEffect(exercise) {
        delay(300)
        tts?.speakWithVoice(exercise.word, useFemaleVoice, null, audioEnabled)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "New Word!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, Color(0xFFE5E5E5), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Placeholder for Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEEEEEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🖼️", fontSize = 64.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = exercise.word,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = exercise.word,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4B4B4B)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1CB0F6),
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { tts?.speakWithVoice(exercise.word, useFemaleVoice, null, audioEnabled) }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen",
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "\"${exercise.sentence}\"",
                    fontSize = 18.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color(0xFFAFAFAF),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("CONTINUE", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun ListeningView(exercise: LessonExercise.Listening, useFemaleVoice: Boolean, audioEnabled: Boolean = true, onContinue: () -> Unit) {
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US
        }
        onDispose { tts?.stop(); tts?.shutdown() }
    }
    
    LaunchedEffect(exercise) {
        delay(300)
        tts?.speakWithVoice(exercise.audioText, useFemaleVoice, null, audioEnabled)
    }

    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "What did you hear?",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1CB0F6),
                modifier = Modifier
                    .size(80.dp)
                    .clickable { tts?.speakWithVoice(exercise.audioText, useFemaleVoice, null, audioEnabled) }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen",
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E5E5)),
                color = Color.White
            ) {
                Text(
                    text = "Tap to listen!",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B4B4B)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Options
        exercise.options.forEach { option ->
            val isSelected = selectedOption == option
            val bgColor = if (isSelected) Color(0xFFDDF4FF) else Color.White
            val borderColor = if (isSelected) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)
            
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(enabled = !showResult) { selectedOption = option }
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF1CB0F6) else Color(0xFF4B4B4B),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (showResult) {
            Surface(
                color = if (isCorrect) Color(0xFFD7FFB8) else Color(0xFFFFE5E5),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isCorrect) "Excellent!" else "Correct answer:",
                        color = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    if (!isCorrect) {
                        Text(
                            text = exercise.correctOption,
                            color = Color(0xFFFF4B4B),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
        
        Button(
            onClick = {
                if (showResult) {
                    if (isCorrect) onContinue() else {
                        // In a real app, maybe repeat the question. For now, continue.
                        onContinue()
                    }
                } else if (selectedOption != null) {
                    isCorrect = selectedOption == exercise.correctOption
                    showResult = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showResult) {
                    if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B)
                } else if (selectedOption != null) Color(0xFF58CC02) else Color(0xFFE5E5E5)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = selectedOption != null || showResult
        ) {
            Text(if (showResult) "CONTINUE" else "CHECK", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (selectedOption != null || showResult) Color.White else Color(0xFFAFAFAF))
        }
    }
}

@Composable
fun TranslationView(exercise: LessonExercise.Translation, useFemaleVoice: Boolean, audioEnabled: Boolean = true, onContinue: () -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = exercise.prompt,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Options
        exercise.options.forEach { option ->
            val isSelected = selectedOption == option
            val bgColor = if (isSelected) Color(0xFFDDF4FF) else Color.White
            val borderColor = if (isSelected) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)
            
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(enabled = !showResult) { selectedOption = option }
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF1CB0F6) else Color(0xFF4B4B4B),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (showResult) {
            Surface(
                color = if (isCorrect) Color(0xFFD7FFB8) else Color(0xFFFFE5E5),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isCorrect) "Excellent!" else "Correct answer:",
                        color = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    if (!isCorrect) {
                        Text(
                            text = exercise.correctOption,
                            color = Color(0xFFFF4B4B),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
        
        Button(
            onClick = {
                if (showResult) {
                    onContinue()
                } else if (selectedOption != null) {
                    isCorrect = selectedOption == exercise.correctOption
                    showResult = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showResult) {
                    if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B)
                } else if (selectedOption != null) Color(0xFF58CC02) else Color(0xFFE5E5E5)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = selectedOption != null || showResult
        ) {
            Text(if (showResult) "CONTINUE" else "CHECK", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (selectedOption != null || showResult) Color.White else Color(0xFFAFAFAF))
        }
    }
}

@Composable
fun SpeakingSentenceView(exercise: LessonExercise.SpeakingSentence, useFemaleVoice: Boolean, audioEnabled: Boolean = true, onContinue: () -> Unit) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US
        }
        onDispose { tts?.stop(); tts?.shutdown() }
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
                    val expected = exercise.sentence.lowercase().replace(Regex("[^a-z]"), "")
                    val actual = recognizedText.lowercase().replace(Regex("[^a-z]"), "")
                    // Since it's a demo, we will be lenient
                    isSuccess = actual.contains(expected) || expected.contains(actual) || actual.length > 3
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
            .padding(24.dp)
    ) {
        Text(
            text = "Now say the sentence!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("👩‍🏫", fontSize = 64.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1CB0F6),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { tts?.speakWithVoice(exercise.sentence, useFemaleVoice, null, audioEnabled) }
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
                        text = "\"${exercise.sentence}\"",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (!hasMicPermission) {
            Surface(
                color = Color(0xFFFFE5E5),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth().border(1.dp, Color(0xFFFF4B4B), RoundedCornerShape(12.dp))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
        }
        
        if (showResult) {
            Button(
                onClick = {
                    if (isSuccess) onContinue() else { showResult = false; recognizedText = "" }
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

@Composable
fun LessonCompleteView(onFinish: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉", fontSize = 100.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Lesson Complete!",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You earned 15 XP!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFC800)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("CONTINUE", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
    ConfettiAnimation()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArrangeWordsView(exercise: LessonExercise.ArrangeWords, useFemaleVoice: Boolean, audioEnabled: Boolean = true, onContinue: () -> Unit) {
    var selectedWords by remember { mutableStateOf(listOf<String>()) }
    var availableWords by remember { mutableStateOf(exercise.shuffledWords) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = exercise.prompt,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Target drop area
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E5E5)),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp)
                .padding(bottom = 24.dp)
        ) {
            FlowRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedWords.forEach { word ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E5E5)),
                        modifier = Modifier.clickable {
                            if(!showResult) {
                                selectedWords = selectedWords - word
                                availableWords = availableWords + word
                            }
                        }
                    ) {
                        Text(
                            text = word,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B4B4B)
                        )
                    }
                }
            }
        }
        
        // Available words
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableWords.forEach { word ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E5E5)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.clickable {
                        if(!showResult) {
                            availableWords = availableWords - word
                            selectedWords = selectedWords + word
                        }
                    }
                ) {
                    Text(
                        text = word,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (showResult) {
            Surface(
                color = if (isCorrect) Color(0xFFD7FFB8) else Color(0xFFFFE5E5),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isCorrect) "Excellent!" else "Correct answer:",
                        color = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    if (!isCorrect) {
                        Text(
                            text = exercise.correctSentence,
                            color = Color(0xFFFF4B4B),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
        
        Button(
            onClick = {
                if (showResult) {
                    onContinue()
                } else if (availableWords.isEmpty() || selectedWords.isNotEmpty()) {
                    val formedSentence = selectedWords.joinToString(" ").lowercase().replace(Regex("[^a-z0-9 ]"), "")
                    val expected = exercise.correctSentence.lowercase().replace(Regex("[^a-z0-9 ]"), "")
                    isCorrect = formedSentence == expected
                    showResult = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showResult) {
                    if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B)
                } else if (selectedWords.isNotEmpty()) Color(0xFF58CC02) else Color(0xFFE5E5E5)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = selectedWords.isNotEmpty() || showResult
        ) {
            Text(if (showResult) "CONTINUE" else "CHECK", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (selectedWords.isNotEmpty() || showResult) Color.White else Color(0xFFAFAFAF))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillInTheBlanksView(exercise: LessonExercise.FillInTheBlanks, useFemaleVoice: Boolean, audioEnabled: Boolean = true, onContinue: () -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = exercise.prompt,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise.sentenceParts.getOrNull(0) ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B)
            )
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selectedOption != null) Color.White else Color(0xFFF7F9FC),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (selectedOption != null) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = selectedOption ?: "          ",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedOption != null) Color(0xFF1CB0F6) else Color.Transparent
                )
            }
            
            Text(
                text = exercise.sentenceParts.getOrNull(1) ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Options
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exercise.options.forEach { option ->
                val isSelected = selectedOption == option
                val bgColor = if (isSelected) Color(0xFFDDF4FF) else Color.White
                val borderColor = if (isSelected) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = bgColor,
                    border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                    modifier = Modifier.clickable(enabled = !showResult) { selectedOption = option }
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF1CB0F6) else Color(0xFF4B4B4B),
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (showResult) {
            Surface(
                color = if (isCorrect) Color(0xFFD7FFB8) else Color(0xFFFFE5E5),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isCorrect) "Excellent!" else "Correct answer:",
                        color = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    if (!isCorrect) {
                        Text(
                            text = exercise.correctWord,
                            color = Color(0xFFFF4B4B),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
        
        Button(
            onClick = {
                if (showResult) {
                    onContinue()
                } else if (selectedOption != null) {
                    isCorrect = selectedOption == exercise.correctWord
                    showResult = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showResult) {
                    if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B)
                } else if (selectedOption != null) Color(0xFF58CC02) else Color(0xFFE5E5E5)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = selectedOption != null || showResult
        ) {
            Text(if (showResult) "CONTINUE" else "CHECK", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (selectedOption != null || showResult) Color.White else Color(0xFFAFAFAF))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MatchPairsView(exercise: LessonExercise.MatchPairs, useFemaleVoice: Boolean, audioEnabled: Boolean = true, onContinue: () -> Unit) {
    val items = remember(exercise) { 
        (exercise.pairs.keys.toList() + exercise.pairs.values.toList()).shuffled() 
    }
    
    var selectedItem1 by remember { mutableStateOf<String?>(null) }
    var matchedPairs by remember { mutableStateOf(setOf<String>()) }
    var showResult by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = exercise.prompt,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items.forEach { item ->
                val isSelected = selectedItem1 == item
                val isMatched = matchedPairs.contains(item)
                val bgColor = when {
                    isMatched -> Color(0xFFE5E5E5)
                    isSelected -> Color(0xFFDDF4FF)
                    else -> Color.White
                }
                val borderColor = when {
                    isMatched -> Color(0xFFE5E5E5)
                    isSelected -> Color(0xFF1CB0F6)
                    else -> Color(0xFFE5E5E5)
                }
                val textColor = if (isMatched) Color.Transparent else if (isSelected) Color(0xFF1CB0F6) else Color(0xFF4B4B4B)
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = bgColor,
                    border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                    modifier = Modifier
                        .clickable(enabled = !isMatched && !showResult) {
                            if (selectedItem1 == null) {
                                selectedItem1 = item
                            } else if (selectedItem1 == item) {
                                selectedItem1 = null
                            } else {
                                // Check match
                                val match1 = exercise.pairs[selectedItem1] == item
                                val match2 = exercise.pairs[item] == selectedItem1
                                if (match1 || match2) {
                                    matchedPairs = matchedPairs + setOf(selectedItem1!!, item)
                                    selectedItem1 = null
                                    if (matchedPairs.size == items.size) {
                                        showResult = true
                                    }
                                } else {
                                    // Wrong match
                                    selectedItem1 = null
                                }
                            }
                        }
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (showResult) {
            Surface(
                color = Color(0xFFD7FFB8),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Excellent!",
                        color = Color(0xFF58CC02),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }
        
        Button(
            onClick = {
                if (showResult) {
                    onContinue()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showResult) Color(0xFF58CC02) else Color(0xFFE5E5E5)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = showResult
        ) {
            Text("CONTINUE", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (showResult) Color.White else Color(0xFFAFAFAF))
        }
    }
}

@Composable
fun PhraseNarrationView(exercise: LessonExercise.PhraseNarration, useFemaleVoice: Boolean, audioEnabled: Boolean = true, onContinue: () -> Unit) {
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US
        }
        onDispose { tts?.stop(); tts?.shutdown() }
    }
    
    LaunchedEffect(exercise) {
        delay(300)
        tts?.speakWithVoice(exercise.phrase, useFemaleVoice, null, audioEnabled)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exercise.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, Color(0xFFE5E5E5), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1CB0F6),
                    modifier = Modifier
                        .size(80.dp)
                        .clickable { tts?.speakWithVoice(exercise.phrase, useFemaleVoice, null, audioEnabled) }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = Color.White,
                        modifier = Modifier.padding(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = exercise.phrase,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(
                    color = Color(0xFFF7F9FC),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = exercise.explanation,
                        fontSize = 18.sp,
                        color = Color(0xFF4B4B4B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("GOT IT", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
