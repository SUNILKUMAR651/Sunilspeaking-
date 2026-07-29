package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel

data class PracticeModule(
    val title: String, 
    val description: String, 
    val icon: ImageVector, 
    val route: String,
    val color1: Color,
    val color2: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(viewModel: LexiViewModel, onNavigate: (String) -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val premiumModules = listOf("AI Voice Roleplay", "Multiplayer Word Battle", "Mock Interview")
    
    val practiceModules = listOf(
        PracticeModule("AI Roleplay", "Real-life scenarios", Icons.Filled.SupportAgent, "ai_roleplay", Color(0xFFE91E63), Color(0xFF9C27B0)),
        PracticeModule("Mock Interview", "AI HR simulation", Icons.Filled.Headset, "mock_interview", Color(0xFF00C6FF), Color(0xFF0072FF)),
        PracticeModule("Speaking Coach", "Pronunciation & fluency", Icons.Filled.RecordVoiceOver, "speaking_practice", Color(0xFFFF512F), Color(0xFFDD2476)),
        PracticeModule("Smart Flashcards", "Spaced repetition", Icons.Filled.Style, "smart_flashcards", Color(0xFF4776E6), Color(0xFF8E54E9)),
        PracticeModule("Word Battle", "Compete globally", Icons.Filled.SportsEsports, "word_battle", Color(0xFFF12711), Color(0xFFF5AF19)),
        PracticeModule("Vocab Quiz", "Test your memory", Icons.Filled.Spellcheck, "vocabulary_quiz", Color(0xFF56AB2F), Color(0xFFA8E063)),
        PracticeModule("Grammar Rule", "Sentence mastery", Icons.AutoMirrored.Filled.Rule, "grammar_challenge", Color(0xFF11998E), Color(0xFF38EF7D))
    )

    val bgBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D1017), Color(0xFF141923), Color(0xFF1A1F2C))
    )

    Box(modifier = Modifier.fillMaxSize().background(bgBrush)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Header Section
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Pro Practice Lab", 
                    style = MaterialTheme.typography.headlineMedium, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = Color.White
                )
                Text(
                    text = "Unlock your fluency with AI-powered interactive modules.", 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )
            }
            
            // Grid of Pro Modules
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(practiceModules) { module ->
                    val isPremium = premiumModules.contains(module.title)
                    val isLocked = isPremium && !userProfile.isPremium
                    
                    PracticeModuleCard(
                        module = module,
                        isLocked = isLocked,
                        onClick = {
                            if (isLocked) {
                                onNavigate("premium")
                            } else {
                                onNavigate(module.route)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PracticeModuleCard(
    module: PracticeModule,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val cardBrush = Brush.linearGradient(
        colors = if (isLocked) listOf(Color(0xFF2C3240), Color(0xFF1A1F2C)) 
                 else listOf(module.color1, module.color2)
    )
    
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .background(cardBrush)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon Header
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = module.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    if (isLocked) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Premium",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Text Footer
                Column {
                    Text(
                        text = module.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = module.description,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
