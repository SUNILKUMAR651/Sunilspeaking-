package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.example.utils.retryWithBackoff
import android.speech.tts.TextToSpeech
import java.util.Locale
import com.example.utils.AudioPlayer
import com.example.utils.speakWithVoice

data class RecordingItem(
    val id: String,
    val sentence: String,
    val score: Int,
    val url: String,
    val timestamp: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationHistoryScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var recordings by remember { mutableStateOf<List<RecordingItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    val userProfile by viewModel.userProfile.collectAsState()
    
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }
    
    val audioPlayer = remember { AudioPlayer() }
    DisposableEffect(Unit) {
        onDispose { audioPlayer.stop() }
    }
    
    var playingUrl by remember { mutableStateOf<String?>(null) }
    var playingNativeSentence by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                val db = FirebaseFirestore.getInstance()
                val snapshot = db.collection("users").document(user.uid)
                    .collection("recordings")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                recordings = snapshot.documents.mapNotNull { doc ->
                    val sentence = doc.getString("sentence") ?: return@mapNotNull null
                    val score = doc.getLong("score")?.toInt() ?: 0
                    val url = doc.getString("url") ?: return@mapNotNull null
                    val ts = doc.getTimestamp("timestamp")?.seconds ?: 0L
                    RecordingItem(doc.id, sentence, score, url, ts)
                }
            } catch (e: Exception) {
                // Ignore error
            }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pronunciation History", fontWeight = FontWeight.Bold) },
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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1CB0F6))
            }
        } else if (recordings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No recordings yet. Practice speaking to save history!", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recordings) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Text(item.sentence, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                                val scoreColor = if (item.score >= 70) Color(0xFF58CC02) else Color(0xFFFF4B4B)
                                Text("${item.score}%", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = scoreColor)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                // Play User Audio
                                val isUserPlaying = playingUrl == item.url
                                Button(
                                    onClick = {
                                        if (isUserPlaying) {
                                            audioPlayer.stop()
                                            playingUrl = null
                                        } else {
                                            playingUrl = item.url
                                            playingNativeSentence = null
                                            audioPlayer.play(item.url) {
                                                playingUrl = null
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isUserPlaying) Color(0xFFFF4B4B) else Color(0xFF1CB0F6)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play Yours", tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isUserPlaying) "Stop" else "Your Attempt", color = Color.White)
                                }
                                
                                // Play Native Audio (TTS)
                                val isNativePlaying = playingNativeSentence == item.sentence
                                Button(
                                    onClick = {
                                        if (isNativePlaying) {
                                            tts?.stop()
                                            playingNativeSentence = null
                                        } else {
                                            audioPlayer.stop()
                                            playingUrl = null
                                            playingNativeSentence = item.sentence
                                            tts?.setOnUtteranceProgressListener(object: android.speech.tts.UtteranceProgressListener() {
                                                override fun onStart(utteranceId: String?) {}
                                                override fun onDone(utteranceId: String?) { playingNativeSentence = null }
                                                override fun onError(utteranceId: String?) { playingNativeSentence = null }
                                            })
                                            tts?.speakWithVoice(item.sentence, userProfile.useFemaleVoice, "native_voice")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isNativePlaying) Color(0xFFFF4B4B) else Color(0xFF58CC02)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play Native", tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isNativePlaying) "Stop" else "Native Voice", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
