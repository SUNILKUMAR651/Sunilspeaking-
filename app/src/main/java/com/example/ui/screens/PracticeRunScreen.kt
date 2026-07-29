package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import com.example.utils.speakWithVoice


@Composable
fun PracticeRunScreen(lessonId: Int, viewModel: LexiViewModel, onBack: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val title = "${userProfile.name}'s practice run"
    
    val sentences = remember {
        List(50) { index -> 
            if (index == 0) "Hello, my name is ${userProfile.name}." 
            else if (index == 1) "I am happy to introduce myself."
            else if (index == 2) "I am learning English every day."
            else "This is practice sentence number ${index + 1}." 
        }
    }
    
    
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // ready
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
    
    var currentSentenceIndex by remember { mutableIntStateOf(0) }

    
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(40.dp).clickable(onClick = onBack)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF6B4EE6), modifier = Modifier.padding(8.dp))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF6B4EE6)
                ) {
                    Text(
                        text = title.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$currentSentenceIndex", fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6), fontSize = 18.sp)
                    Text("DONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("0%", fontWeight = FontWeight.Bold, color = Color(0xFFFF9600), fontSize = 18.sp)
                    Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9600))
                }
            }
        },
        containerColor = Color(0xFFFFF0F5)
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.padding(padding).fillMaxSize()) {
            val isWide = maxWidth > 800.dp
            
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(2f)) {
                        PracticeMainPanel(sentences[currentSentenceIndex], currentSentenceIndex + 1, sentences.size, tts, userProfile.useFemaleVoice)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PracticeListPanel(sentences, currentSentenceIndex)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PracticeMainPanel(sentences[currentSentenceIndex], currentSentenceIndex + 1, sentences.size, tts, userProfile.useFemaleVoice, modifier = Modifier.weight(1f))
                }
            }
            
            // Bottom Action Bar
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { 
                        if (currentSentenceIndex < sentences.size - 1) currentSentenceIndex++ 
                    }) {
                        Text("SKIP", color = Color(0xFF6B4EE6), fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4EE6)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(56.dp).widthIn(min = 200.dp)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = "Speak")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tap to speak", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFE5E5),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Retry", tint = Color(0xFFFF4B4B))
                            Text("RETRY", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF4B4B))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeMainPanel(sentence: String, currentIndex: Int, totalCount: Int, tts: TextToSpeech?, useFemaleVoice: Boolean, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF6B4EE6),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(currentIndex.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text("SENTENCE $currentIndex OF $totalCount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B4EE6))
                    Text("Ready?", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF6B4EE6))
                    Surface(color = Color(0xFF6B4EE6), shape = RoundedCornerShape(4.dp)) {
                        Text("Speak the sentence clearly and confidently.", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(4.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
                                    Text("🔊 Speak this line", color = Color(0xFF6B4EE6), fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                            tts?.speakWithVoice(sentence, useFemaleVoice, null)
                        })
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sentence chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sentence.split(" ").forEach { word ->
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
                        color = Color.White
                    ) {
                        Text(word, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4B4B4B))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // You Said / Score box
            Row(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
                    modifier = Modifier.weight(1f).height(100.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("YOU SAID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Waiting for your voice...", color = Color.Gray, fontSize = 16.sp)
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
                    modifier = Modifier.width(100.dp).height(100.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("0%", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1CB0F6))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Voice idle", color = Color.Gray, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
                ) {
                    Text("0:00", color = Color(0xFF6B4EE6), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun PracticeListPanel(sentences: List<String>, currentIndex: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp) // Leave space for bottom bar
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("All sentences", color = Color(0xFF1CB0F6), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Full 50-line run", color = Color.Gray, fontSize = 12.sp)
                }
                Row {
                    Text("🏆 0", fontWeight = FontWeight.Bold, color = Color(0xFFFF9600))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(sentences) { index, sentence ->
                    val isActive = index == currentIndex
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isActive) Color(0xFFF0F5FF) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isActive) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)
                                ) {
                                    Text(
                                        "${index + 1}",
                                        color = if (isActive) Color.White else Color.Gray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isActive) "ACTIVE" else "NEXT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(sentence, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4B4B4B))
                        }
                    }
                }
            }
        }
    }
}
