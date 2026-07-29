package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarChallengeScreen(onBack: () -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    
    val questions = listOf(
        GrammarQuestion(
            sentence = "By this time next year, I ___ my degree.",
            correctAnswer = "will have finished",
            options = listOf("will finish", "will be finishing", "will have finished", "finished")
        ),
        GrammarQuestion(
            sentence = "If I ___ you, I would study harder.",
            correctAnswer = "were",
            options = listOf("am", "was", "were", "been")
        ),
        GrammarQuestion(
            sentence = "She is used to ___ early.",
            correctAnswer = "waking up",
            options = listOf("wake up", "woke up", "waking up", "waken up")
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grammar Challenge", fontWeight = FontWeight.Bold) },
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
            if (currentQuestionIndex < questions.size) {
                val question = questions[currentQuestionIndex]
                
                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
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
                        Text("Fill in the blank:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = question.sentence,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
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
                            else -> MaterialTheme.colorScheme.surface
                        }
                        
                        val contentColor = when {
                            !showResult && isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            !showResult -> MaterialTheme.colorScheme.onSurface
                            showResult && isCorrect -> MaterialTheme.colorScheme.onPrimary
                            showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
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
                            }
                            showResult = true
                        }
                    },
                    enabled = selectedAnswer != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (showResult) "Next Question" else "Check Answer")
                }
            } else {
                // Result Screen
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Challenge Complete!",
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
                    Button(onClick = onBack) {
                        Text("Back to Practice")
                    }
                }
            }
        }
    }
}

data class GrammarQuestion(
    val sentence: String,
    val correctAnswer: String,
    val options: List<String>
)
