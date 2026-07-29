package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: LexiViewModel, onBack: () -> Unit, onNavigateToPremium: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = Color(0xFF4B4B4B)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF4B4B4B))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFFF7F9FC)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ACCOUNT
            SettingsSection(title = "ACCOUNT") {
                SettingsItem(
                    icon = Icons.Filled.Person,
                    iconBgColor = Color(0xFF8E9EFA),
                    title = userProfile.name.ifEmpty { "User" },
                    subtitle = "user@example.com", // Replace with real email if available
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    iconBgColor = Color(0xFFFF8989),
                    title = "Log Out",
                    subtitle = "Sign out from your account",
                    titleColor = Color(0xFFFF4B4B),
                    subtitleColor = Color(0xFFFF8989),
                    onClick = { viewModel.signOut() }
                )
            }

            // SUBSCRIPTION
            SettingsSection(title = "SUBSCRIPTION") {
                SettingsItem(
                    icon = Icons.Filled.CreditCard,
                    iconBgColor = Color(0xFFFFC800),
                    title = "Pro Monthly",
                    badge = "ON HOLD",
                    subtitle = "Payment method needs updating",
                    onClick = onNavigateToPremium
                )
            }

            // PREFERENCES
            SettingsSection(title = "PREFERENCES") {
                SettingsItem(
                    icon = Icons.Filled.Language,
                    iconBgColor = Color(0xFF58CC02),
                    title = "Native Language",
                    subtitle = "Your mother tongue",
                    trailingText = "Hindi",
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsItem(
                    icon = Icons.Filled.VerifiedUser,
                    iconBgColor = Color(0xFF8E9EFA),
                    title = "English Level",
                    subtitle = "Tap to choose your current level",
                    trailingText = "Advanced",
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsItem(
                    icon = Icons.Filled.Schedule,
                    iconBgColor = Color(0xFFFF9600),
                    title = "Daily Goal",
                    subtitle = "Minutes per day",
                    trailingText = "${userProfile.dailyGoalMinutes} min",
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsItem(
                    icon = Icons.Filled.Description,
                    iconBgColor = Color(0xFFFF89B3),
                    title = "Learning Goal",
                    subtitle = "Tap to choose your learning objective",
                    trailingText = "Pass Exams",
                    onClick = { }
                )
            }

            // SUPPORT & LEGAL
            SettingsSection(title = "SUPPORT & LEGAL") {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    iconBgColor = Color(0xFF1CB0F6),
                    title = "Help Center",
                    subtitle = "Get help and find answers",
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsItem(
                    icon = Icons.Filled.ChatBubbleOutline,
                    iconBgColor = Color(0xFFFF9600),
                    title = "Contact Support",
                    subtitle = "Talk to our support team",
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsItem(
                    icon = Icons.Filled.Lock,
                    iconBgColor = Color(0xFFB1ADF6),
                    title = "Privacy Policy",
                    subtitle = "Read how we protect your data",
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsItem(
                    icon = Icons.Filled.Description,
                    iconBgColor = Color(0xFF4B93FF),
                    title = "Terms of Service",
                    subtitle = "Read terms and conditions",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B),
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E5E5)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    iconBgColor: Color,
    title: String,
    subtitle: String,
    badge: String? = null,
    trailingText: String? = null,
    titleColor: Color = Color(0xFF4B4B4B),
    subtitleColor: Color = Color(0xFFAFAFAF),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = iconBgColor,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = titleColor
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFFFF0E5),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFF9600),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = subtitle, 
                fontSize = 14.sp, 
                color = subtitleColor
            )
        }
        
        if (trailingText != null) {
            Text(
                text = trailingText,
                fontSize = 16.sp,
                color = Color(0xFFAFAFAF),
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFFAFAFAF),
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFAFAFAF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
