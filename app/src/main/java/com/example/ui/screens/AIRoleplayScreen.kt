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
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

data class RoleplayScenario(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val systemPrompt: String,
    val initialMessage: String,
    val color: Color
)

val scenarios = listOf(
    RoleplayScenario(
        "job_interview",
        "Job Interview",
        "Practice answering common interview questions.",
        Icons.Filled.WorkOutline,
        "You are an HR manager conducting a job interview for a software developer position. Ask questions one by one. Highlight any grammar or phrasing mistakes gently by starting your response with '[Feedback: ...]' before continuing the conversation.",
        "Hello! Welcome to the interview. Can you start by telling me a little bit about yourself?",
        Color(0xFF2979FF)
    ),
    RoleplayScenario(
        "restaurant_order",
        "Ordering at a Restaurant",
        "Practice ordering food and interacting with a waiter.",
        Icons.Filled.RestaurantMenu,
        "You are a friendly waiter at an Italian restaurant. Ask for their order, suggest specials, and keep the conversation natural. Highlight any grammar or phrasing mistakes gently by starting your response with '[Feedback: ...]' before continuing.",
        "Welcome! Here is our menu. Can I get you started with anything to drink?",
        Color(0xFFFF9800)
    ),
    RoleplayScenario(
        "airport_checkin",
        "At the Airport",
        "Practice checking in for a flight and going through security.",
        Icons.Filled.Flight,
        "You are an airline check-in agent. Ask for their passport, where they are flying, and if they have any bags to check. Highlight any grammar or phrasing mistakes gently by starting your response with '[Feedback: ...]' before continuing.",
        "Good morning! Passport and ticket, please.",
        Color(0xFF00E676)
    ),
    RoleplayScenario(
        "talking_to_stranger",
        "Talking to a Stranger",
        "Practice casual small talk.",
        Icons.Filled.PersonOutline,
        "You are a friendly stranger waiting at a bus stop. Make casual small talk about the weather or local events. Highlight any grammar or phrasing mistakes gently by starting your response with '[Feedback: ...]' before continuing.",
        "Beautiful weather we're having today, isn't it?",
        Color(0xFFE040FB)
    )
)

data class RoleplayMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIRoleplayScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    var selectedScenario by remember { mutableStateOf<RoleplayScenario?>(null) }

    if (selectedScenario == null) {
        ScenarioSelectionScreen(onBack = onBack, onSelect = { selectedScenario = it })
    } else {
        ActiveRoleplayScreen(
            scenario = selectedScenario!!,
            viewModel = viewModel,
            onBack = { selectedScenario = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioSelectionScreen(onBack: () -> Unit, onSelect: (RoleplayScenario) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Voice Roleplay", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1218),
                    titleContentColor = Color.White
                ),
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = Color(0xFF0F1218)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Choose a scenario to practice:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(scenarios) { scenario ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(scenario) },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = scenario.color.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, scenario.color.copy(alpha = 0.3f)),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = scenario.icon,
                                contentDescription = null,
                                tint = scenario.color,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(scenario.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(scenario.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRoleplayScreen(scenario: RoleplayScenario, viewModel: LexiViewModel, onBack: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var messages by remember { mutableStateOf(listOf<RoleplayMessage>()) }
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

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                if (messages.size == 1 && !messages.first().isUser) {
                    tts?.speakWithVoice(messages.first().text, userProfile.useFemaleVoice, null)
                }
            }
        }
        tts = textToSpeech
        onDispose { textToSpeech.shutdown() }
    }

    LaunchedEffect(scenario) {
        if (db != null) {
            try {
                val doc = db.collection("users").document(userId).collection("roleplays").document(scenario.id).get().await()
                if (doc.exists()) {
                    val history = doc.get("messages") as? List<Map<String, Any>>
                    if (history != null) {
                        messages = history.map { 
                            RoleplayMessage(it["text"] as String, it["isUser"] as Boolean) 
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore error, maybe offline or not configured
            }
        }
        
        if (messages.isEmpty()) {
            messages = listOf(RoleplayMessage(scenario.initialMessage, false))
            tts?.speakWithVoice(scenario.initialMessage, userProfile.useFemaleVoice, null)
        }
        isLoadingHistory = false
    }

    fun saveMessagesToFirestore(updatedMessages: List<RoleplayMessage>) {
        if (db != null) {
            coroutineScope.launch {
                try {
                    val messagesList = updatedMessages.map { mapOf("text" to it.text, "isUser" to it.isUser) }
                    db.collection("users").document(userId).collection("roleplays").document(scenario.id)
                        .set(mapOf("messages" to messagesList), SetOptions.merge())
                } catch (e: Exception) {
                    // Ignore error
                }
            }
        }
    }

    fun sendMessageToAI(userText: String) {
        val newMessages = messages + RoleplayMessage(userText, true)
        messages = newMessages
        isThinking = true
        saveMessagesToFirestore(newMessages)
        
        coroutineScope.launch {
            try {
                val historyParts = newMessages.map { Content(listOf(Part(it.text)), role = if(it.isUser) "user" else "model") }
                
                val request = GenerateContentRequest(
                    contents = historyParts,
                    systemInstruction = Content(listOf(Part(scenario.systemPrompt)))
                )
                
                val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
                val aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I didn't quite catch that."
                val cleanResponse = aiResponse
                
                val finalMessages = newMessages + RoleplayMessage(cleanResponse, false)
                messages = finalMessages
                saveMessagesToFirestore(finalMessages)
                
                // Remove the feedback text from the spoken response
                val spokenResponse = cleanResponse.replace(Regex("\\[Feedback:.*?\\]\\s*"), "")
                tts?.speakWithVoice(spokenResponse, userProfile.useFemaleVoice, null)
                viewModel.recordLessonCompletion(15, "speaking")
            } catch (e: Exception) {
                messages = messages + RoleplayMessage("Sorry, I had an error connecting. Please try again.", false)
                Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show()
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
            override fun onPartialResults(partialResults: Bundle?) {}
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
                title = { Text(scenario.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        tts?.stop()
                        onBack()
                    }) {
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
        ) {
            if (isLoadingHistory) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00E676))
                }
            } else {
                val listState = rememberLazyListState()
                
                LaunchedEffect(messages.size, isThinking) {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(0)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
                    reverseLayout = true
                ) {
                    if (isThinking) {
                        item {
                            GlassmorphicChatBubble("Thinking...", isUser = false, scenarioColor = scenario.color)
                        }
                    }
                    items(messages.reversed()) { msg ->
                        GlassmorphicChatBubble(text = msg.text, isUser = msg.isUser, scenarioColor = scenario.color)
                    }
                }
                
                Surface(
                    color = Color(0xFF1E222A),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (isRecording) "Listening..." else "Tap to speak", 
                            style = MaterialTheme.typography.bodyMedium, 
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = if (isRecording) 1.2f else 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulse_scale"
                        )
                        
                        Surface(
                            shape = CircleShape,
                            color = if (isRecording) Color.Red.copy(alpha = 0.2f) else scenario.color.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, if (isRecording) Color.Red else scenario.color.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .size(80.dp)
                                .clickable {
                                    if (!hasRecordPermission) {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    } else {
                                        if (isRecording) {
                                            speechRecognizer.stopListening()
                                        } else {
                                            isRecording = true
                                            tts?.stop()
                                            speechRecognizer.startListening(speechRecognizerIntent)
                                        }
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                                    contentDescription = if (isRecording) "Stop" else "Speak",
                                    tint = if (isRecording) Color.Red else scenario.color,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassmorphicChatBubble(text: String, isUser: Boolean, scenarioColor: Color) {
    val backgroundColor = if (isUser) scenarioColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
    val borderColor = if (isUser) scenarioColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f)
    
    // Parse for feedback
    val feedbackRegex = Regex("\\[Feedback:(.*?)\\]")
    val matchResult = feedbackRegex.find(text)
    val feedbackText = matchResult?.groups?.get(1)?.value?.trim()
    val cleanText = text.replace(feedbackRegex, "").trim()

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
                Text(
                    text = cleanText,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            
            if (!isUser && feedbackText != null && feedbackText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFF9800).copy(alpha = 0.1f), // Warning orange
                    border = BorderStroke(1.dp, Color(0xFFFF9800).copy(alpha = 0.3f)),
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = "💡 $feedbackText",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Color(0xFFFFCC80),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        if (isUser) Spacer(modifier = Modifier.width(8.dp))
    }
}
