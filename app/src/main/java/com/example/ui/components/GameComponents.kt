package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalLetterWheel(
    letters: List<String>,
    modifier: Modifier = Modifier,
    onLetterSelected: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val itemHeight = 60.dp
    
    // Add empty strings at the beginning and end so the first and last letters can snap to center
    val displayLetters = listOf("") + letters + listOf("")
    
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val centerIndex = listState.firstVisibleItemIndex + 1
        if (centerIndex in 1..letters.size) {
            onLetterSelected(letters[centerIndex - 1])
        }
    }
    
    Box(
        modifier = modifier
            .width(60.dp)
            .height(itemHeight * 3)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(2.dp, Color(0xFFE5E5E5), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier.fillMaxSize()
        ) {
            items(displayLetters.size) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayLetters[index],
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B)
                    )
                }
            }
        }
        // Center selection highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(Color(0xFF1CB0F6).copy(alpha = 0.1f))
                .border(2.dp, Color(0xFF1CB0F6), RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun WordWheelGameLayout(
    sentenceWithBlank: String,
    targetCorrectWord: String,
    rolls: List<List<String>>,
    onCorrectSubmit: () -> Unit
) {
    val selectedLetters = remember { mutableStateListOf<String>().apply { 
        rolls.forEach { add(it.first()) } 
    } }
    
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = sentenceWithBlank,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = Color(0xFF4B4B4B)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            rolls.forEachIndexed { index, letters ->
                VerticalLetterWheel(
                    letters = letters,
                    modifier = Modifier.weight(1f),
                    onLetterSelected = { letter ->
                        selectedLetters[index] = letter
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        val currentWord = selectedLetters.joinToString("")
        Button(
            onClick = {
                if (currentWord.equals(targetCorrectWord, ignoreCase = true)) {
                    onCorrectSubmit()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentWord.equals(targetCorrectWord, ignoreCase = true)) Color(0xFF58CC02) else Color(0xFF1CB0F6)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("SUBMIT: $currentWord", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun CrosswordConnectGameLayout(
    letterBank: List<String>,
    validWords: List<String>,
    onAllWordsFound: () -> Unit
) {
    val foundWords = remember { mutableStateListOf<String>() }
    var currentSelection by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top: Crossword Grid (simplified as a list of boxes)
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                validWords.forEach { word ->
                    val isFound = foundWords.contains(word)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        word.forEach { char ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(if (isFound) Color(0xFF58CC02) else Color.White, RoundedCornerShape(8.dp))
                                    .border(2.dp, if (isFound) Color(0xFF4CA600) else Color(0xFFE5E5E5), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isFound) {
                                    Text(char.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Middle: Current Selection Display
        Text(
            text = currentSelection.ifEmpty { " " },
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1CB0F6),
            modifier = Modifier.padding(16.dp).height(40.dp)
        )
        
        // Bottom: Circular Letter Bank
        CircularLetterBank(
            letters = letterBank,
            onWordSubmit = { word ->
                if (validWords.contains(word) && !foundWords.contains(word)) {
                    foundWords.add(word)
                    if (foundWords.size == validWords.size) {
                        onAllWordsFound()
                    }
                }
                currentSelection = ""
            },
            onSelectionChange = { selection ->
                currentSelection = selection
            }
        )
    }
}

@Composable
fun CircularLetterBank(
    letters: List<String>,
    onWordSubmit: (String) -> Unit,
    onSelectionChange: (String) -> Unit
) {
    Box(
        modifier = Modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFF7F9FC),
            border = BorderStroke(2.dp, Color(0xFFE5E5E5)),
            modifier = Modifier.fillMaxSize()
        ) {}
        
        val selectedIndices = remember { mutableStateListOf<Int>() }
        
        letters.forEachIndexed { index, letter ->
            val angle = 2 * Math.PI * index / letters.size - Math.PI / 2
            val offsetX = (Math.cos(angle) * 85).toFloat()
            val offsetY = (Math.sin(angle) * 85).toFloat()
            
            val isSelected = selectedIndices.contains(index)
            
            Box(
                modifier = Modifier
                    .offset(x = offsetX.dp, y = offsetY.dp)
                    .size(56.dp)
                    .background(if (isSelected) Color(0xFF1CB0F6) else Color.White, CircleShape)
                    .border(2.dp, if (isSelected) Color(0xFF1899D6) else Color(0xFFE5E5E5), CircleShape)
                    .clickable {
                        if (!isSelected) {
                            selectedIndices.add(index)
                            val word = selectedIndices.map { letters[it] }.joinToString("")
                            onSelectionChange(word)
                        } else if (selectedIndices.last() == index) {
                            // Allow deselecting the last selected letter
                            selectedIndices.removeLast()
                            val word = selectedIndices.map { letters[it] }.joinToString("")
                            onSelectionChange(word)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color(0xFF4B4B4B)
                )
            }
        }
        
        // Submit action trigger
        Button(
            onClick = {
                val word = selectedIndices.map { letters[it] }.joinToString("")
                onWordSubmit(word)
                selectedIndices.clear()
            },
            modifier = Modifier.align(Alignment.Center).size(64.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("GO", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}
