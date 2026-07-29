package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.viewmodel.LexiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(viewModel: LexiViewModel, onNavigate: (String) -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val premiumModules = listOf("AI Debate", "Accent Lab", "Tone & Politeness", "Scenarios", "Conversation")

    val modules = listOf(
        LearnModule("Vocabulary", Icons.Filled.Spellcheck, "vocabulary_index"),
        LearnModule("Grammar", Icons.Filled.Rule, "grammar_learn"),
        LearnModule("Speaking", Icons.Filled.RecordVoiceOver, "speaking_practice"),
        LearnModule("Reading", Icons.Filled.MenuBook, "reading_learn"),
        LearnModule("Listening", Icons.Filled.Headphones, "listening_learn"),
        LearnModule("Writing", Icons.Filled.Edit, "writing_learn"),
        LearnModule("Conversation", Icons.Filled.Forum, "conversation_learn"),
        LearnModule("Dictionary", Icons.Filled.Translate, "list/Dictionary"),
        LearnModule("AI Debate", Icons.Filled.Psychology, "ai_debate"),
        LearnModule("Accent Lab", Icons.Filled.GraphicEq, "accent_lab"),
        LearnModule("Tone & Politeness", Icons.Filled.SelfImprovement, "tone_learn"),
        LearnModule("Scenarios", Icons.Filled.TravelExplore, "scenarios_learn")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learn Modules", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(modules) { module ->
                val isPremiumLocked = premiumModules.contains(module.title) && !userProfile.isPremium
                val dest = if (isPremiumLocked) "premium" else module.route

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onNavigate(dest) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                module.icon, 
                                contentDescription = module.title,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = module.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (isPremiumLocked) {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = "Premium",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class LearnModule(val title: String, val icon: ImageVector, val route: String)
