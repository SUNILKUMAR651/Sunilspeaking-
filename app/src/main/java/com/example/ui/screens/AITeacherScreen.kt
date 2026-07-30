package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.viewmodel.LexiViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

import androidx.compose.material.icons.filled.Call

data class TeacherMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITeacherScreen(viewModel: LexiViewModel, onNavigateToCall: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var messages by remember { mutableStateOf(listOf<TeacherMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isLoadingHistory by remember { mutableStateOf(true) }
    var isRecording by remember { mutableStateOf(false) }
    var isThinking by remember { mutableStateOf(false) }
    
    val userId = viewModel.userProfile.collectAsState().value.id
    val db = remember { 
        try { 
            FirebaseFirestore.getInstance() 
        } catch (e: Exception) { 
            null 
        } 
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(Unit) {
        if (db != null) {
            try {
                val doc = db.collection("users").document(userId).collection("teacher").document("history").get().await()
                if (doc.exists()) {
                    val history = doc.get("messages") as? List<Map<String, Any>>
                    if (history != null) {
                        messages = history.map { 
                            TeacherMessage(it["text"] as String, it["isUser"] as Boolean) 
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore error, maybe offline or not configured
            }
        }
        
        if (messages.isEmpty()) {
            messages = listOf(TeacherMessage("Hello! I am your AI ${userProfile.targetLanguage} Teacher. How can I help you practice today? Feel free to ask me questions, or let me know what you want to learn.", false))
        }
        isLoadingHistory = false
    }

    fun saveMessagesToFirestore(updatedMessages: List<TeacherMessage>) {
        if (db != null) {
            coroutineScope.launch {
                try {
                    val messagesList = updatedMessages.map { mapOf("text" to it.text, "isUser" to it.isUser) }
                    db.collection("users").document(userId).collection("teacher").document("history")
                        .set(mapOf("messages" to messagesList), SetOptions.merge())
                } catch (e: Exception) {
                    // Ignore error
                }
            }
        }
    }

    fun sendMessageToAI(userText: String) {
        if (userText.isBlank()) return
        
        val newMessages = messages + TeacherMessage(userText, true)
        messages = newMessages
        inputText = ""
        isThinking = true
        saveMessagesToFirestore(newMessages)
        
        coroutineScope.launch {
            try {
                val systemPrompt = "You are an expert ${userProfile.targetLanguage} language teacher. The user's native language is ${userProfile.nativeLanguage}. Help the user improve their grammar, vocabulary, and speaking skills. Correct mistakes gently, provide explanations when asked, and be encouraging. If the user makes a grammar mistake, provide a small '💡 Tip:' at the end of your response."
                val historyParts = newMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }
                
                val request = GenerateContentRequest(
                    contents = historyParts,
                    systemInstruction = Content(listOf(Part(systemPrompt)))
                )
                
                var retryCount = 0
                var aiResponse = ""
                while(retryCount < 3) {
                    try {
                        val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                        aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm sorry, I couldn't process that."
                        break
                    } catch (e: Exception) {
                        retryCount++
                        if (retryCount >= 3) throw e
                        kotlinx.coroutines.delay(1000L * retryCount)
                    }
                }
                
                val cleanResponse = aiResponse
                
                val finalMessages = newMessages + TeacherMessage(cleanResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
                viewModel.recordLessonCompletion(5, "vocabulary")
            } catch (e: Exception) {
                // Fallback smooth message instead of an ugly error
                val fallbackResponse = "I seem to be having a little trouble connecting to my knowledge base right now. Let's keep practicing our ${userProfile.targetLanguage}! (Error: ${e.message})" 
                val finalMessages = newMessages + TeacherMessage(fallbackResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
            } finally {
                isThinking = false
            }
        }
    }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
        }
    }
    
    DisposableEffect(context) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isRecording = false }
            override fun onError(error: Int) { 
                isRecording = false 
            }
            override fun onResults(results: Bundle?) {
                isRecording = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val resultText = matches?.firstOrNull() ?: ""
                if (resultText.isNotBlank()) {
                    sendMessageToAI(resultText)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val resultText = matches?.firstOrNull() ?: ""
                if (resultText.isNotBlank()) {
                    inputText = resultText
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        onDispose { speechRecognizer.destroy() }
    }

    var hasRecordPermission by remember { 
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) 
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasRecordPermission = granted }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Teacher", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1218),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onNavigateToCall) {
                        Icon(Icons.Filled.Call, contentDescription = "Call AI Teacher", tint = Color(0xFF00E5FF))
                    }
                }
            )
        },
        containerColor = Color(0xFF0F1218)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            if (isLoadingHistory) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00E5FF))
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                    reverseLayout = true
                ) {
                    if (isThinking) {
                        item {
                            TeacherChatBubble("Typing...", isUser = false, isTyping = true)
                        }
                    }
                    items(messages.reversed()) { msg ->
                        TeacherChatBubble(text = msg.text, isUser = msg.isUser)
                    }
                }
                
                Surface(
                    color = Color(0xFF1E222A), // Solid dark color
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .imePadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isRecording) Color.Red.copy(alpha = 0.2f) else Color(0xFF00E5FF).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, if (isRecording) Color.Red else Color(0xFF00E5FF).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .size(48.dp)
                        ) {
                            IconButton(onClick = {
                                if (!hasRecordPermission) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    if (isRecording) {
                                        speechRecognizer.stopListening()
                                    } else {
                                        isRecording = true
                                        inputText = ""
                                        speechRecognizer.startListening(speechRecognizerIntent)
                                    }
                                }
                            }) {
                                Icon(
                                    if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                                    contentDescription = if (isRecording) "Stop" else "Speak",
                                    tint = if (isRecording) Color.Red else Color(0xFF00E5FF)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ask a question...", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFF00E5FF)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 3
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Surface(
                            shape = CircleShape,
                            color = if (inputText.isNotBlank()) Color(0xFF00E5FF) else Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            IconButton(
                                onClick = { sendMessageToAI(inputText) },
                                enabled = inputText.isNotBlank() && !isThinking
                            ) {
                                Icon(Icons.Filled.Send, contentDescription = "Send", tint = if (inputText.isNotBlank()) Color.Black else Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherChatBubble(text: String, isUser: Boolean, isTyping: Boolean = false) {
    val backgroundColor = if (isUser) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
    val borderColor = if (isUser) Color(0xFF00E5FF).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f)
    
    // Parse for tip
    val tipRegex = Regex("💡 Tip:(.*?)(?=\\n|$)")
    val matchResult = tipRegex.find(text)
    val tipText = matchResult?.groups?.get(1)?.value?.trim()
    val cleanText = text.replace(tipRegex, "").trim()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            modifier = Modifier.weight(0.85f, fill = false),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                ),
                color = backgroundColor,
                border = BorderStroke(1.dp, borderColor)
            ) {
                if (isTyping) {
                    val infiniteTransition = rememberInfiniteTransition(label = "dots")
                    val alpha1 by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 0), repeatMode = RepeatMode.Reverse), label = "d1")
                    val alpha2 by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 200), repeatMode = RepeatMode.Reverse), label = "d2")
                    val alpha3 by infiniteTransition.animateFloat(initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 400), repeatMode = RepeatMode.Reverse), label = "d3")
                    
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).background(Color.White.copy(alpha = alpha1), CircleShape))
                        Box(modifier = Modifier.size(8.dp).background(Color.White.copy(alpha = alpha2), CircleShape))
                        Box(modifier = Modifier.size(8.dp).background(Color.White.copy(alpha = alpha3), CircleShape))
                    }
                } else {
                    Text(
                        text = cleanText,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
            
            if (!isUser && tipText != null && tipText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFD700).copy(alpha = 0.15f), // Gold
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f)),
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = "💡 $tipText",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Color(0xFFFFE082),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        if (isUser) Spacer(modifier = Modifier.width(8.dp))
    }
}
