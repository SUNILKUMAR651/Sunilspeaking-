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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel

data class PracticeScenario(val id: Int, val title: String, val description: String, val color: Color)

val practiceScenarios = listOf(
    PracticeScenario(1, "Introduce Yourself", "Talk about yourself, your name, where you're from.", Color(0xFF8E9EFA)),
    PracticeScenario(2, "Daily Routine", "Describe your daily activities and routines in detail.", Color(0xFF4B93FF)),
    PracticeScenario(3, "Family", "Talk about your family members and your relationships.", Color(0xFFFF89B3)),
    PracticeScenario(4, "Food & Drinks", "Speak about your favorite foods, drinks, and meals.", Color(0xFFFF9600)),
    PracticeScenario(5, "Hobbies", "Discuss what you like to do in your free time.", Color(0xFF58CC02)),
    PracticeScenario(6, "Travel", "Talk about places you have visited or want to go.", Color(0xFF1CB0F6)),
    PracticeScenario(7, "Work & Study", "Describe your job, studies, and career goals.", Color(0xFF00C4B4)),
    PracticeScenario(8, "Technology", "Talk about gadgets, apps, and how you use them.", Color(0xFF8E9EFA)),
    PracticeScenario(9, "Shopping", "Practice useful phrases for stores, prices, and buying items.", Color(0xFF00C4B4)),
    PracticeScenario(10, "Health & Wellness", "Talk about healthy habits, routines, and feeling better.", Color(0xFF1CB0F6)),
    PracticeScenario(11, "At the Pharmacy", "Ask for medicine and explain symptoms clearly.", Color(0xFF1CB0F6)),
    PracticeScenario(12, "Public Speaking", "Build confidence speaking to a group and presenting.", Color(0xFF8E9EFA)),
    PracticeScenario(13, "Problem Solving", "Discuss problems and propose solutions.", Color(0xFF4B93FF)),
    PracticeScenario(14, "Customer Support", "Handle complaints and help customers politely.", Color(0xFF00C4B4)),
    PracticeScenario(15, "Airport & Immigration", "Answer questions at the airport and customs.", Color(0xFF4B93FF)),
    PracticeScenario(16, "Neighborhood", "Describe your area and talk to neighbors.", Color(0xFF58CC02))
) + (17..50).map { PracticeScenario(it, "Scenario $it", "Practice speaking about scenario $it.", Color(0xFF1CB0F6)) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProPracticeScreen(viewModel: LexiViewModel, onBack: () -> Unit, onNavigateToRun: (Int) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice", fontWeight = FontWeight.Bold, fontSize = 28.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F9FC))
            )
        },
        containerColor = Color(0xFFF7F9FC)
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(280.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            items(practiceScenarios) { scenario ->
                PracticeCard(scenario, onClick = { onNavigateToRun(scenario.id) })
            }
        }
    }
}

@Composable
fun PracticeCard(scenario: PracticeScenario, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Image Placeholder 
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(scenario.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.RecordVoiceOver, 
                    contentDescription = null, 
                    tint = scenario.color,
                    modifier = Modifier.size(64.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = scenario.color,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(scenario.id.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = scenario.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B4B4B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = scenario.description,
                fontSize = 14.sp,
                color = Color(0xFFAFAFAF),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF15182B),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Start",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
