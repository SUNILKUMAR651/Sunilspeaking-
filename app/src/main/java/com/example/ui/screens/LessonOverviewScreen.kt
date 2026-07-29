package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel

@Composable
fun LessonOverviewScreen(
    lessonId: Int,
    viewModel: LexiViewModel,
    onStartLearning: () -> Unit,
    onBack: () -> Unit
) {
    val title = if (lessonId == 1) "Introductions" else "Lesson $lessonId"
    val subtitle = if (lessonId == 1) "Meeting New People" else "Continue learning"
    
    val words = listOf("Hello", "Name", "Meet", "Friend", "+1 more")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White, RoundedCornerShape(32.dp))
                .border(2.dp, Color(0xFFE5E5E5), RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("👋", fontSize = 64.sp)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B)
        )
        Text(
            text = subtitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFAFAFAF)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Stats
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(modifier = Modifier.weight(1f), icon = "⭐", value = "15", label = "XP REWARD", iconColor = Color(0xFFFFC800))
            StatCard(modifier = Modifier.weight(1f), icon = "📖", value = "5", label = "WORDS", iconColor = Color(0xFF1CB0F6))
            StatCard(modifier = Modifier.weight(1f), icon = "⏱️", value = "5m", label = "DURATION", iconColor = Color(0xFFFF4B4B))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Core Vocabulary
        Text(
            text = "◎ CORE VOCABULARY",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFAFAFAF),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            WordChip("👋 Hello")
            Spacer(modifier = Modifier.width(8.dp))
            WordChip("🏷️ Name")
            Spacer(modifier = Modifier.width(8.dp))
            WordChip("🤝 Meet")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            WordChip("😊 Friend")
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = Color(0xFFEEEEEE),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = "+1 more",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color(0xFFAFAFAF),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onStartLearning,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B46FF)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("START LEARNING →", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "← BACK TO MAP",
            color = Color(0xFFAFAFAF),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onBack() }.padding(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StatCard(modifier: Modifier, icon: String, value: String, label: String, iconColor: Color) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4B4B4B))
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFAFAFAF))
        }
    }
}

@Composable
fun WordChip(text: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E5E5)),
        shadowElevation = 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color(0xFF4B4B4B),
            fontWeight = FontWeight.Bold
        )
    }
}
