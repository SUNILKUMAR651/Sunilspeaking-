package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LexiViewModel,
    onNavigate: (String) -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Background Gradient for Fluent Dark Theme
    val bgBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D1017),
            Color(0xFF141923),
            Color(0xFF1A1F2C)
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(bgBrush)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onOpenDrawer) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null,
                                    modifier = Modifier.padding(10.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Welcome back,", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(userProfile.level, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    },
                    actions = {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(40.dp)
                                .clickable { onNavigate("leaderboard") }
                        ) {
                            Icon(
                                Icons.Filled.EmojiEvents,
                                contentDescription = "Leaderboard",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable { onNavigate("profile") }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔥", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${userProfile.dayStreak}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color(0xFF0D1017).copy(alpha = 0.9f)
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            containerColor = Color.Transparent, // Let the Box gradient show through
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Main Banner
                item {
                    HeroBanner(userProfile.totalXp) { onNavigate("learning_path") }
                }

                // Quick Actions
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            GlassmorphicActionCard(
                                title = "Roleplay",
                                subtitle = "AI Voice",
                                icon = Icons.Filled.SmartToy,
                                accentColor = Color(0xFFB388FF),
                                modifier = Modifier.weight(1f)
                            ) { onNavigate("ai_roleplay") }
                            
                            GlassmorphicActionCard(
                                title = "Practice",
                                subtitle = "Pronunciation",
                                icon = Icons.Filled.Mic,
                                accentColor = Color(0xFF00E5FF),
                                modifier = Modifier.weight(1f)
                            ) { onNavigate("speaking_practice") }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            GlassmorphicActionCard(
                                title = "Speaking Practice",
                                subtitle = "50+ scenarios",
                                icon = Icons.Filled.RecordVoiceOver,
                                accentColor = Color(0xFFFF9600),
                                modifier = Modifier.weight(1f)
                            ) { onNavigate("pro_practice") }
                            
                            GlassmorphicActionCard(
                                title = "Speaking Fast",
                                subtitle = "Job Interviews",
                                icon = Icons.Filled.TrendingUp,
                                accentColor = Color(0xFFFF3333),
                                modifier = Modifier.weight(1f)
                            ) { onNavigate("speaking_fast") }
                        }
                    }
                }

                // Games Section
                item {
                    SectionHeader(title = "Practice Games", onSeeAll = { onNavigate("practice") })
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        item {
                            GlassmorphicActionCard(
                                title = "Swipe Battle",
                                subtitle = "Which is Right?",
                                icon = Icons.Filled.Swipe,
                                accentColor = Color(0xFFE91E63),
                                modifier = Modifier.width(160.dp)
                            ) { onNavigate("swipe_battle") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Bubble Pop",
                                accentColor = Color(0xFFFF512F),
                                icon = Icons.Filled.Games
                            ) { onNavigate("bubble_pop") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Word Wheel",
                                accentColor = Color(0xFF56AB2F),
                                icon = Icons.Filled.DataExploration
                            ) { onNavigate("word_wheel") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Audio Dictation",
                                accentColor = Color(0xFF4776E6),
                                icon = Icons.Filled.Audiotrack
                            ) { onNavigate("audio_dictation") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Crossword",
                                accentColor = Color(0xFF00C6FF),
                                icon = Icons.Filled.Abc
                            ) { onNavigate("crossword_connect") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Weak Words",
                                accentColor = Color(0xFFE040FB),
                                icon = Icons.Filled.TrendingUp
                            ) { onNavigate("weak_words") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Grammar Challenge",
                                accentColor = Color(0xFF00E676),
                                icon = Icons.Filled.AutoAwesome
                            ) { onNavigate("grammar_challenge") }
                        }
                        item {
                            GlassmorphicGameCard(
                                title = "Flashcards",
                                accentColor = Color(0xFFFFC107),
                                icon = Icons.Filled.Flip
                            ) { onNavigate("smart_flashcards") }
                        }
                    }
                }
                
                // Categories
                item {
                    SectionHeader(title = "Vocabulary Topics", onSeeAll = { onNavigate("learn") })
                    Spacer(modifier = Modifier.height(16.dp))
                    val categories = listOf(
                        Triple("Basic Vocab", "150 words", Color(0xFFFF5252)),
                        Triple("A To Z Vocab", "1200 words", Color(0xFF69F0AE)),
                        Triple("Business ${userProfile.targetLanguage}", "400 words", Color(0xFF9C27B0)),
                        Triple("Job Interview", "200 words", Color(0xFF00BCD4)),
                        Triple("IT & Tech", "300 words", Color(0xFF3F51B5)),
                        Triple("News Vocab", "340 words", Color(0xFF448AFF)),
                        Triple("Travel & Tourism", "250 words", Color(0xFFFF9800)),
                        Triple("Medical ${userProfile.targetLanguage}", "180 words", Color(0xFFE91E63)),
                        Triple("Important Vocab", "500 words", Color(0xFFFFAB40)),
                        Triple("Daily Phrases", "600 phrases", Color(0xFF8BC34A)),
                        Triple("IELTS & TOEFL", "1500 words", Color(0xFFF44336)),
                        Triple("Slang & Idioms", "350 phrases", Color(0xFF9C27B0)),
                        Triple("Movie ${userProfile.targetLanguage}", "250 scenes", Color(0xFFE91E63)),
                        Triple("Phrasal Verbs", "400 verbs", Color(0xFF009688)),
                        Triple("Academic", "500 words", Color(0xFF795548)),
                        Triple("Email Writing", "150 templates", Color(0xFF607D8B))
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val premiumCategories = listOf("IELTS & TOEFL", "Business ${userProfile.targetLanguage}", "Medical ${userProfile.targetLanguage}", "Academic", "Movie ${userProfile.targetLanguage}", "IT & Tech", "Email Writing")
                        categories.chunked(2).forEach { rowCategories ->
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                rowCategories.forEach { category ->
                                    val isPremiumLocked = premiumCategories.contains(category.first) && !userProfile.isPremium
                                    val destination = if (isPremiumLocked) "premium" else if (category.first == "A To Z Vocab") "vocabulary_index" else "learn_category/${category.first}"
                                    
                                    Box(modifier = Modifier.weight(1f)) {
                                        GlassmorphicCategoryCard(
                                            title = category.first,
                                            subtitle = category.second,
                                            accentColor = category.third,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            onNavigate(destination)
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
                                if (rowCategories.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun HeroBanner(xp: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF2979FF), Color(0xFF1565C0))
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            // Glass reflection highlight
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                ))
            )

            // Decorative circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    radius = size.width * 0.4f,
                    center = Offset(size.width * 0.9f, size.height * 0.1f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = size.width * 0.3f,
                    center = Offset(size.width * 0.1f, size.height * 0.8f)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(
                            "LESSON 1",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Continue your learning path",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "$xp / 10000 XP to next level",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // Play Button
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(68.dp),
                    shadowElevation = 16.dp
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.padding(16.dp),
                        tint = Color(0xFF1565C0)
                    )
                }
            }
            
            // Progress Bar at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = (xp / 10000f).coerceIn(0f, 1f))
                        .background(Color(0xFF00E676))
                )
            }
        }
    }
}

@Composable
fun GlassmorphicActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = accentColor
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.clickable { onSeeAll() }
        ) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun GlassmorphicGameCard(
    title: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(150.dp)
            .height(170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        color = accentColor.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
        shadowElevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = size.width * 0.8f,
                    center = Offset(size.width * 0.8f, size.height * 0.2f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.padding(14.dp),
                        tint = accentColor
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun GlassmorphicCategoryCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    Icons.Filled.MenuBook,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}
