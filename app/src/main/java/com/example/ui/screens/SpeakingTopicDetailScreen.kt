package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SpeakingTask(val id: Int, val title: String, val subtitle: String, val difficulty: String, val time: String)
data class SpeakingTaskSection(val title: String, val tasks: List<SpeakingTask>)

val sampleJobTasks = listOf(
    SpeakingTaskSection("GENERAL", listOf(
        SpeakingTask(1, "Tell me about yourself", "Practice the classic interview introduction.", "EASY", "~60s"),
        SpeakingTask(2, "Your Greatest Strength", "Discuss what you excel at.", "EASY", "~45s"),
        SpeakingTask(3, "Your Greatest Weakness", "Handle the weakness question professionally.", "MEDIUM", "~45s"),
        SpeakingTask(4, "Why this company?", "Explain your specific interest in the role.", "MEDIUM", "~45s"),
        SpeakingTask(5, "Where do you see yourself in 5 years?", "Discuss your long-term career goals.", "EASY", "~45s")
    )),
    SpeakingTaskSection("BEHAVIORAL", listOf(
        SpeakingTask(6, "Overcoming a Challenge", "Use the STAR method to describe a challenge.", "MEDIUM", "~60s"),
        SpeakingTask(7, "Conflict with a Coworker", "Explain how you handle workplace conflict.", "HARD", "~60s"),
        SpeakingTask(8, "Leadership Experience", "Describe a time you showed leadership.", "HARD", "~60s")
    ))
)

@Composable
fun SpeakingTopicDetailScreen(categoryId: String, onBack: () -> Unit, onNavigateToTask: (Int) -> Unit) {
    val title = speakingCategories.find { it.id == categoryId }?.title ?: "Job Interview Practice"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(40.dp).clickable(onClick = onBack)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text("50 Topics • intermediate Level", color = Color(0xFF64748B), fontSize = 14.sp)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            sampleJobTasks.forEach { section ->
                item {
                    Text(
                        text = "${section.title} • ${section.tasks.size} TASKS",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
                    )
                }
                
                itemsIndexed(section.tasks) { index, task ->
                    SpeakingTaskRow(task = task, index = index + 1) {
                        onNavigateToTask(task.id)
                    }
                    if (index < section.tasks.size - 1) {
                        HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(start = 72.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SpeakingTaskRow(task: SpeakingTask, index: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF3366FF),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(index.toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            Text(task.subtitle, fontSize = 14.sp, color = Color(0xFF64748B))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            val difficultyColor = when (task.difficulty) {
                "EASY" -> Color(0xFF00C48C)
                "MEDIUM" -> Color(0xFFFF9800)
                else -> Color(0xFFFF3333)
            }
            Text(task.difficulty, color = difficultyColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text(task.time, color = Color(0xFF64748B), fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Surface(
            shape = CircleShape,
            color = Color(0xFF3366FF),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Filled.Mic, contentDescription = "Practice", tint = Color.White, modifier = Modifier.padding(10.dp))
        }
    }
}
