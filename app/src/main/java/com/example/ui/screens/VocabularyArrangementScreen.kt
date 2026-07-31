package com.example.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel
import kotlinx.coroutines.delay

import android.speech.tts.TextToSpeech
import com.example.utils.speakWithVoice
import java.util.Locale
import com.airbnb.lottie.compose.*
import com.example.R
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyArrangementScreen(
    viewModel: LexiViewModel,
    onBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
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
    
    val allWords by viewModel.allWords.collectAsState()
    val practiceWords = remember(allWords) {
        allWords.filter { !it.word.contains(" ") && !it.word.contains("-") }.shuffled()
    }
    
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    
    var soundEnabled by remember { mutableStateOf(true) }
    var isDarkMode by remember { mutableStateOf(false) }
    
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
    
    var showSuccessAnimation by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.heart))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = showSuccessAnimation,
        restartOnPlay = false
    )
    
    LaunchedEffect(progress) {
        if (progress == 1f) {
            showSuccessAnimation = false
        }
    }
    DisposableEffect(Unit) {
        onDispose { toneGen.release() }
    }
    
    val currentWord = practiceWords.getOrNull(currentIndex)
    
    // The letters of the current word, shuffled
    var scrambledLetters by remember { mutableStateOf(listOf<Char>()) }
    var selectedIndices by remember { mutableStateOf(listOf<Int>()) }
    var isWrong by remember { mutableStateOf(false) }
    var showMeaning by remember { mutableStateOf(false) }
    
    // Timer
    LaunchedEffect(currentIndex, timeLeft) {
        if (timeLeft > 0 && practiceWords.isNotEmpty()) {
            delay(1000L)
            timeLeft--
        } else if (timeLeft == 0) {
            // Time up, play error sound and move to next
            if (soundEnabled) toneGen.startTone(ToneGenerator.TONE_SUP_ERROR, 200)
            delay(1000L)
            if (currentIndex < practiceWords.size - 1) {
                currentIndex++
                timeLeft = 30
            }
        }
    }
    
    // Initialize scrambled letters when word changes
    LaunchedEffect(currentWord) {
        currentWord?.let {
            val chars = it.word.uppercase().toList()
            scrambledLetters = chars.shuffled()
            // Make sure it's actually shuffled differently from the correct word if possible
            if (scrambledLetters == chars && chars.size > 1) {
                scrambledLetters = chars.reversed()
            }
            selectedIndices = emptyList()
            isWrong = false
            showMeaning = false
        }
    }
    
    // Check if word is complete
    LaunchedEffect(selectedIndices) {
        if (currentWord != null && selectedIndices.size == scrambledLetters.size && scrambledLetters.isNotEmpty()) {
            val formedWord = selectedIndices.map { scrambledLetters[it] }.joinToString("")
            if (formedWord == currentWord.word.uppercase()) {
                // Correct
                if (soundEnabled) toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
                showSuccessAnimation = true
                score += 10
                delay(1000L)
                if (currentIndex < practiceWords.size - 1) {
                    currentIndex++
                    timeLeft = 30
                }
            } else {
                // Wrong
                isWrong = true
                if (soundEnabled) toneGen.startTone(ToneGenerator.TONE_SUP_ERROR, 300)
                delay(500L)
                isWrong = false
                selectedIndices = emptyList()
            }
        }
    }

    val bgColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFFAFAFA)
    val textColor = if (isDarkMode) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                actions = {
                    // Volume toggle
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF6B4EE6),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(44.dp)
                            .clickable { soundEnabled = !soundEnabled }
                    ) {
                        Icon(
                            if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                            contentDescription = "Toggle Sound",
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    
                    // Dark mode toggle
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF6B4EE6),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(44.dp)
                            .clickable { isDarkMode = !isDarkMode }
                    ) {
                        Icon(
                            if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = "Toggle Theme",
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )
        },
        containerColor = bgColor
    ) { padding ->
        if (practiceWords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No suitable words available for this practice.", color = textColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            com.example.data.LexiconDatabase.words.take(5).forEach {
                                viewModel.addWord(it)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4EE6))
                    ) {
                        Text("Add Demo Words to Firebase", color = Color.White)
                    }
                }
            }
            return@Scaffold
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Score Badge
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = "Score", tint = Color(0xFFFFB300), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Score:", color = textColor, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF6B4EE6),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = score.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Timer Circle
            Surface(
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFF6B4EE6)),
                color = Color.Transparent,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = timeLeft.toString(),
                        color = Color(0xFF6B4EE6),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Selected Letters Area
            val boxBgColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .drawBehind {
                        val stroke = Stroke(
                            width = 6f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                        )
                        drawRoundRect(
                            color = if (isWrong) Color.Red else Color(0xFFE0E0E0),
                            style = stroke,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx())
                        )
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedIndices.isEmpty()) {
                    Text(
                        text = "Tap the letters below\nto arrange a word.",
                        color = Color(0xFF9E9E9E),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(selectedIndices) { index ->
                            SelectedLetterTile(
                                letter = scrambledLetters[index],
                                isWrong = isWrong
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Show Meaning and Pronounce Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Show Meaning Toggle
                Row(
                    modifier = Modifier
                        .clickable { showMeaning = !showMeaning }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFFBC02D),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showMeaning) "Hide Meaning" else "Show Meaning",
                        color = Color(0xFF6B4EE6),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Pronounce Button
                Row(
                    modifier = Modifier
                        .clickable { 
                            currentWord?.word?.let { word ->
                                tts?.speakWithVoice(word, userProfile.useFemaleVoice, null)
                            }
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.VolumeUp,
                        contentDescription = "Pronounce Word",
                        tint = Color(0xFF4DB6AC),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Listen",
                        color = Color(0xFF4DB6AC),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            
            AnimatedVisibility(visible = showMeaning) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDarkMode) Color(0xFF333333) else Color(0xFFF3E5F5)
                ) {
                    val meaning = currentWord?.definitions?.firstOrNull()?.meaning ?: "No meaning available."
                    Text(
                        text = meaning,
                        color = textColor,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scrambled Letters Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 64.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(scrambledLetters) { index, letter ->
                    val isSelected = selectedIndices.contains(index)
                    LetterTile(
                        letter = letter,
                        isSelected = isSelected,
                        onClick = {
                            if (!isSelected && selectedIndices.size < scrambledLetters.size) {
                                selectedIndices = selectedIndices + index
                            }
                        }
                    )
                }
            }
            
            // Bottom Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Undo Last Button
                Button(
                    onClick = {
                        if (selectedIndices.isNotEmpty()) {
                            selectedIndices = selectedIndices.dropLast(1)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Undo Last", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                // Next Word Button
                Button(
                    onClick = {
                        if (currentIndex < practiceWords.size - 1) {
                            currentIndex++
                            timeLeft = 30
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6AC)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("Next Word", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Filled.ArrowForward, contentDescription = "Next", tint = Color.White)
                }
            }
        }
    }
    
    if (showSuccessAnimation) {
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(250.dp)
            )
        }
    }
    }
}

@Composable
fun SelectedLetterTile(letter: Char, isWrong: Boolean) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(if (isWrong) Color(0xFFEF5350) else Color(0xFF6B4EE6), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LetterTile(letter: Char, isSelected: Boolean, onClick: () -> Unit) {
    if (isSelected) {
        // Invisible placeholder to keep grid structure
        Box(modifier = Modifier.size(72.dp))
    } else {
        // 3D Button effect
        Box(
            modifier = Modifier
                .size(72.dp)
                .clickable { onClick() }
        ) {
            // Shadow / Bottom border
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 6.dp)
                    .background(Color(0xFFF06292), RoundedCornerShape(16.dp)) // Pink bottom
            )
            // Top face
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 0.dp)
                    .background(Color(0xFF6B4EE6), RoundedCornerShape(16.dp)), // Purple top
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.toString(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
