package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.LexiViewModel
import com.example.ui.components.CrosswordConnectGameLayout
import com.example.ui.components.WordWheelGameLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleGameScreenPlaceholder(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("The $title game engine will be fully integrated here. Currently using the structured JSON API output as per AI engine parameters.", modifier = Modifier.padding(32.dp))
        }
    }
}

@Composable
fun BubblePopScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    SimpleGameScreenPlaceholder("Bubble Pop", onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordConnectScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    var isComplete by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crossword Connect", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isComplete) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Level Complete!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Finish")
                    }
                }
            } else {
                CrosswordConnectGameLayout(
                    letterBank = listOf("A", "E", "S", "R", "C", "H"),
                    validWords = listOf("SEARCH", "CHASE", "RACE", "CARE", "EACH"),
                    onAllWordsFound = {
                        isComplete = true
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordWheelScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    var isComplete by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word Wheel", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isComplete) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Word Found!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Finish")
                    }
                }
            } else {
                WordWheelGameLayout(
                    sentenceWithBlank = "The company will [BLANK] all new employees tomorrow.",
                    targetCorrectWord = "WELCOME",
                    rolls = listOf(
                        listOf("W", "M", "V", "N"),
                        listOf("E", "A", "O", "I"),
                        listOf("L", "I", "T", "D"),
                        listOf("C", "K", "S", "G"),
                        listOf("O", "U", "E", "A"),
                        listOf("M", "N", "W", "B"),
                        listOf("E", "I", "A", "Y")
                    ),
                    onCorrectSubmit = {
                        isComplete = true
                    }
                )
            }
        }
    }
}

@Composable
fun SwipeBattleScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    SimpleGameScreenPlaceholder("Swipe Battle", onBack)
}

@Composable
fun AudioDictationScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    SimpleGameScreenPlaceholder("Audio Dictation", onBack)
}
