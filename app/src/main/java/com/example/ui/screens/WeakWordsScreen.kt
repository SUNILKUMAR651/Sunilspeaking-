package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeakWordsScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    val weakWords by viewModel.weakWords.collectAsState()
    val allWords by viewModel.allWords.collectAsState()
    
    var currentIndex by remember { mutableIntStateOf(0) }
    
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var currentOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    
    LaunchedEffect(currentIndex, weakWords, allWords) {
        if (currentIndex < weakWords.size && allWords.isNotEmpty()) {
            val currentWord = weakWords[currentIndex]
            val correctDefinition = currentWord.definitions.firstOrNull()?.meaning ?: "No meaning found."
            
            val otherWords = allWords.filter { it.word != currentWord.word && it.definitions.isNotEmpty() }.shuffled()
            val incorrectOptions = otherWords.take(3).map { it.definitions.first().meaning }
            
            val options = (incorrectOptions + correctDefinition).shuffled()
            currentOptions = options
            selectedOption = null
            isCorrect = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spaced Repetition Quiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1CB0F6),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF7F9FC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (weakWords.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF58CC02).copy(alpha = 0.2f),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = Color(0xFF58CC02), modifier = Modifier.padding(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "You're all caught up!",
                            color = Color(0xFF4B4B4B),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No weak words due for review right now.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else if (currentIndex < weakWords.size) {
                val word = weakWords[currentIndex]
                val correctDefinition = word.definitions.firstOrNull()?.meaning ?: ""
                
                // Progress Bar
                LinearProgressIndicator(
                    progress = { currentIndex.toFloat() / weakWords.size },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                    color = Color(0xFF58CC02),
                    trackColor = Color(0xFFE5E5E5),
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "What is the meaning of:",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4B4B4B)
                )
                
                if (word.phonetic.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = word.phonetic,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1CB0F6)
                    )
                }
                                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Options
                currentOptions.forEach { optionText ->
                    val isSelected = selectedOption == optionText
                    val isCorrectAnswer = optionText == correctDefinition
                    
                    val bgColor = when {
                        isSelected && isCorrect == true -> Color(0xFFD7FFB8)
                        isSelected && isCorrect == false -> Color(0xFFFFDFE0)
                        selectedOption != null && isCorrectAnswer -> Color(0xFFD7FFB8)
                        else -> Color.White
                    }
                    
                    val borderColor = when {
                        isSelected && isCorrect == true -> Color(0xFF58CC02)
                        isSelected && isCorrect == false -> Color(0xFFFF4B4B)
                        selectedOption != null && isCorrectAnswer -> Color(0xFF58CC02)
                        else -> Color(0xFFE5E5E5)
                    }
                    
                    val textColor = when {
                        isSelected && isCorrect == true -> Color(0xFF58CC02)
                        isSelected && isCorrect == false -> Color(0xFFFF4B4B)
                        selectedOption != null && isCorrectAnswer -> Color(0xFF58CC02)
                        else -> Color(0xFF4B4B4B)
                    }
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable(enabled = selectedOption == null) {
                                selectedOption = optionText
                                isCorrect = isCorrectAnswer
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = bgColor,
                        border = BorderStroke(2.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor,
                                fontWeight = if (selectedOption != null && (isSelected || isCorrectAnswer)) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected || (selectedOption != null && isCorrectAnswer)) {
                                Icon(
                                    imageVector = if (isCorrectAnswer) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = null,
                                    tint = textColor
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (selectedOption != null) {
                    Button(
                        onClick = {
                            if (isCorrect == true) {
                                viewModel.updateWordMastery(word.word, 5) // Easy
                            } else {
                                viewModel.updateWordMastery(word.word, 1) // Forgot
                            }
                            currentIndex++
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCorrect == true) Color(0xFF58CC02) else Color(0xFFFF4B4B)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF58CC02).copy(alpha = 0.2f),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF58CC02), modifier = Modifier.padding(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Challenge Complete!",
                            color = Color(0xFF4B4B4B),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "You reviewed ${weakWords.size} words.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onBack,
                            modifier = Modifier.height(56.dp).fillMaxWidth(0.8f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1CB0F6)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Back to Home", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
