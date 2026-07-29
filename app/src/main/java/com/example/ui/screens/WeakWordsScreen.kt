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
    var currentIndex by remember { mutableIntStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice Weak Words", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1218),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0F1218)
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
                            color = Color(0xFF00E676).copy(alpha = 0.2f),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.padding(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "You're all caught up!",
                            color = Color.White,
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

                Text(
                    "Word ${currentIndex + 1} of ${weakWords.size}",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable { showAnswer = !showAnswer },
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = word.word,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = word.phonetic,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF2979FF)
                            )
                            
                            AnimatedVisibility(
                                visible = showAnswer,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Spacer(modifier = Modifier.height(32.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    val definition = word.definitions.firstOrNull()
                                    if (definition != null) {
                                        Text(
                                            text = definition.meaning,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "\"${definition.example}\"",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (!showAnswer) {
                    Button(
                        onClick = { showAnswer = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Show Answer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("How well did you remember this?", color = Color.White, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RatingButton("Forgot", Color(0xFFFF5252)) {
                            viewModel.updateWordMastery(word.word, 1)
                            showAnswer = false
                            currentIndex++
                        }
                        RatingButton("Hard", Color(0xFFFF9800)) {
                            viewModel.updateWordMastery(word.word, 3)
                            showAnswer = false
                            currentIndex++
                        }
                        RatingButton("Easy", Color(0xFF00E676)) {
                            viewModel.updateWordMastery(word.word, 5)
                            showAnswer = false
                            currentIndex++
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF2979FF).copy(alpha = 0.2f),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = Color(0xFF2979FF), modifier = Modifier.padding(16.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Session Complete!",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "You practiced ${weakWords.size} words.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onBack,
                            modifier = Modifier.height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Back to Home", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.RatingButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .padding(horizontal = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold)
    }
}
