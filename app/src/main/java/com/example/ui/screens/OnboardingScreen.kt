package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@Composable
fun OnboardingScreen(viewModel: LexiViewModel, onFinish: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    var step by remember { mutableIntStateOf(0) }
    var selectedLanguage by remember { mutableStateOf(userProfile.nativeLanguage) }
    var selectedTarget by remember { mutableStateOf(userProfile.targetLanguage) }
    var selectedLevel by remember { mutableStateOf(userProfile.level) }

    val languages = listOf("English", "Hindi", "Spanish", "French", "German", "Mandarin", "Arabic", "Portuguese", "Russian", "Japanese", "Korean", "Italian", "Turkish", "Vietnamese", "Polish", "Dutch", "Thai", "Indonesian", "Malay", "Bengali")
    val levels = listOf(
        "Beginner (A1)" to "Just starting to learn",
        "Elementary (A2)" to "Can understand basic phrases",
        "Intermediate (B1)" to "Can hold a basic conversation",
        "Upper Int. (B2)" to "Can speak fluently on most topics",
        "Advanced (C1)" to "Can express complex ideas easily",
        "Master (C2)" to "Native-like proficiency"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Progress Bar
        LinearProgressIndicator(
            progress = { (step + 1) / 3f },
            modifier = Modifier.fillMaxWidth().height(8.dp).border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(4.dp)),
            color = Color(0xFF58CC02),
            trackColor = Color(0xFFF0F0F0)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (step == 0) {
            Text(
                text = "What language do you want to learn?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Choose your target language.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                languages.forEach { lang ->
                    val isSelected = selectedTarget == lang
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { selectedTarget = lang },
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, if (isSelected) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)),
                        color = if (isSelected) Color(0xFFDDF4FF) else Color.White
                    ) {
                        Text(
                            text = lang,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF1CB0F6) else Color(0xFF4B4B4B),
                            modifier = Modifier.padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else if (step == 1) {
            Text(
                text = "What is your native language?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "This helps us translate explanations for you.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                languages.forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { selectedLanguage = lang },
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, if (isSelected) Color(0xFF1CB0F6) else Color(0xFFE5E5E5)),
                        color = if (isSelected) Color(0xFFDDF4FF) else Color.White
                    ) {
                        Text(
                            text = lang,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF1CB0F6) else Color(0xFF4B4B4B),
                            modifier = Modifier.padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            Text(
                text = "What is your $selectedTarget level?", 
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                levels.forEach { (level, desc) ->
                    val isSelected = selectedLevel == level
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { selectedLevel = level },
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, if (isSelected) Color(0xFF58CC02) else Color(0xFFE5E5E5)),
                        color = if (isSelected) Color(0xFFD7FFB8) else Color.White
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = level,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF58CC02) else Color(0xFF4B4B4B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                fontSize = 14.sp,
                                color = if (isSelected) Color(0xFF4CA600) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = {
                if (step < 2) {
                    step++
                } else {
                    viewModel.updateProfile(userProfile.copy(
                        targetLanguage = selectedTarget,
                        nativeLanguage = selectedLanguage,
                        level = selectedLevel,
                        isOnboardingCompleted = true
                    ))
                    onFinish()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (step < 2) Color(0xFF1CB0F6) else Color(0xFF58CC02))
        ) {
            Text(if (step < 2) "CONTINUE" else "START LEARNING", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
