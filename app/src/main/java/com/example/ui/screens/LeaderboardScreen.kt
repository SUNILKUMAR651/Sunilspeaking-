package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.viewmodel.LexiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LexiViewModel,
    onBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val currentUserProfile by viewModel.userProfile.collectAsState()
    var leaderboard by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        leaderboard = viewModel.getLeaderboard()
        isLoading = false
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Leaderboard", fontWeight = FontWeight.Bold) },
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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00E676))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Top 3 Podium
                PodiumSection(leaderboard.take(3))
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Rest of the leaderboard
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(leaderboard.drop(3)) { index, profile ->
                        LeaderboardItem(
                            rank = index + 4,
                            profile = profile,
                            isCurrentUser = profile.id == currentUserProfile.id
                        )
                    }
                    
                    // Add current user if not in top 10
                    if (leaderboard.none { it.id == currentUserProfile.id }) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Your Rank", 
                                color = Color.Gray, 
                                modifier = Modifier.padding(horizontal = 16.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LeaderboardItem(
                                rank = 99, // Dummy rank for now
                                profile = currentUserProfile,
                                isCurrentUser = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumSection(top3: List<UserProfile>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp)
            .height(220.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        if (top3.size > 1) PodiumPlace(profile = top3[1], rank = 2, height = 140.dp, color = Color(0xFFC0C0C0))
        if (top3.isNotEmpty()) PodiumPlace(profile = top3[0], rank = 1, height = 180.dp, color = Color(0xFFFFD700))
        if (top3.size > 2) PodiumPlace(profile = top3[2], rank = 3, height = 110.dp, color = Color(0xFFCD7F32))
    }
}

@Composable
fun PodiumPlace(profile: UserProfile, rank: Int, height: androidx.compose.ui.unit.Dp, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.width(100.dp)
    ) {
        // Avatar
        Box(contentAlignment = Alignment.TopEnd) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                border = BorderStroke(2.dp, color),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(profile.initials, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                }
            }
            if (rank == 1) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = "Crown",
                    tint = color,
                    modifier = Modifier.size(24.dp).offset(x = 6.dp, y = (-6).dp)
                )
            } else {
                Surface(
                    shape = CircleShape,
                    color = color,
                    modifier = Modifier.size(24.dp).offset(x = 4.dp, y = 4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("$rank", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(profile.name.split(" ").firstOrNull() ?: "", color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
        Text("${profile.totalXp} XP", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Podium block
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = Color.White.copy(alpha = 0.05f),
            border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(color.copy(alpha = 0.2f), Color.Transparent))
                ),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    "$rank", 
                    color = color.copy(alpha = 0.5f), 
                    fontSize = 48.sp, 
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, profile: UserProfile, isCurrentUser: Boolean) {
    val backgroundColor = if (isCurrentUser) Color(0xFF00E676).copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f)
    val borderColor = if (isCurrentUser) Color(0xFF00E676).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f)
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$rank",
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(profile.initials, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isCurrentUser) "You" else profile.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Text(
                text = "${profile.totalXp} XP",
                color = if (isCurrentUser) Color(0xFF00E676) else Color(0xFF2979FF),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
