import re

content = """package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel
import kotlin.math.sin

data class PathLesson(
    val id: Int,
    val title: String,
    val subtitle: String,
    val isPro: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningPathScreen(
    viewModel: LexiViewModel,
    onNavigateToLesson: (Int) -> Unit,
    onBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    
    val lessons = listOf(
        PathLesson(1, "Introductions", "MEETING NEW PEOPLE", false),
        PathLesson(2, "Colors", "BASIC COLORS", false),
        PathLesson(3, "Numbers", "COUNTING NUMBERS", false),
        PathLesson(4, "Family Members", "FAMILY MEMBERS", true),
        PathLesson(5, "Animals", "COMMON ANIMALS", true),
        PathLesson(6, "Fruits & Veggies", "FOOD", true),
        PathLesson(7, "Daily Routines", "ROUTINES", true),
        PathLesson(8, "Time & Days", "TIME", true),
        PathLesson(9, "At Home", "HOUSEHOLD", true),
        PathLesson(10, "At School", "EDUCATION", true)
    )

    val currentUnlockedLevel = when {
        userProfile.totalXp >= 8530 -> 5
        userProfile.totalXp >= 8510 -> 4
        userProfile.totalXp >= 8490 -> 3
        userProfile.totalXp >= 8470 -> 2
        else -> 1
    }

    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF8E9EFA),
            Color(0xFFB1ADF6),
            Color(0xFFDEB5E5),
            Color(0xFFFFDFB9)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning Path", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(skyGradient)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(lessons) { index, lesson ->
                    // Calculate horizontal offset for a winding path
                    val offset = sin(index.toDouble() * 0.8).toFloat() * 60f
                    
                    val isUnlocked = lesson.id <= currentUnlockedLevel
                    val isCompleted = lesson.id < currentUnlockedLevel

                    PathNode(
                        lesson = lesson,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        offsetX = offset,
                        isNextNodeUnlocked = lesson.id < currentUnlockedLevel, // for path line
                        onClick = {
                            if (isUnlocked) {
                                if (lesson.isPro && !userProfile.isPremium) {
                                    // Should show premium dialog, but for now we just navigate if unlocked
                                }
                                onNavigateToLesson(lesson.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PathNode(
    lesson: PathLesson,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    offsetX: Float,
    isNextNodeUnlocked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw the dashed path line to the next node
        Canvas(modifier = Modifier.fillMaxSize()) {
            val startX = size.width / 2 + offsetX.dp.toPx()
            val startY = size.height / 2
            
            // Assume the next node offset (just an approximation for visual connection)
            // Real path would require global coordinates, but this works for vertical list
            drawLine(
                color = Color(0xFF6B4EE6).copy(alpha = 0.5f),
                start = Offset(startX, startY),
                end = Offset(size.width / 2, size.height + 100f),
                strokeWidth = 20f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f),
                cap = StrokeCap.Round
            )
        }

        Column(
            modifier = Modifier
                .offset(x = offsetX.dp)
                .width(160.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // The Bubble Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) Color(0xFFFFD700)
                        else if (isUnlocked) Color(0xFF4CAF50)
                        else Color(0xFFE0E0E0)
                    )
                    .clickable(enabled = isUnlocked, onClick = onClick)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(Icons.Filled.Star, contentDescription = "Completed", tint = Color(0xFFFFD700), modifier = Modifier.size(40.dp))
                    } else if (isUnlocked) {
                        Icon(Icons.Filled.Star, contentDescription = "Current", tint = Color(0xFF4CAF50).copy(alpha = 0.5f), modifier = Modifier.size(40.dp))
                    } else {
                        Icon(Icons.Filled.Lock, contentDescription = "Locked", tint = Color.Gray, modifier = Modifier.size(40.dp))
                    }
                }
                
                if (lesson.isPro) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD700),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .offset(x = 4.dp, y = (-4).dp)
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = "Pro", tint = Color.White, modifier = Modifier.padding(4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The Lesson Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${lesson.id}. ${lesson.title}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = lesson.subtitle,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B4EE6),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    
                    Button(
                        onClick = onClick,
                        enabled = isUnlocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lesson.isPro) Color(0xFFE68A00) else Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            if (lesson.isPro) Icons.Filled.Star else Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (lesson.isPro) "PRO LESSON" else "FREE LESSON",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/LearningPathScreen.kt', 'w') as f:
    f.write(content)

