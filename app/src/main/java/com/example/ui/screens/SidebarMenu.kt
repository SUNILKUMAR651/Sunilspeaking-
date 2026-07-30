package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LexiViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SidebarMenu(
    viewModel: LexiViewModel,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFFF7F9FC),
        modifier = Modifier.width(340.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Header / ACCOUNT
            SidebarSection(title = "ACCOUNT") {
                SidebarItem(
                    icon = Icons.Filled.Person,
                    iconBgColor = Color(0xFF8E9EFA),
                    title = userProfile.name.ifEmpty { "Learner" },
                    subtitle = "user@example.com",
                    onClick = { 
                        onNavigate("profile")
                        onCloseDrawer()
                    }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SidebarItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    iconBgColor = Color(0xFFFF8989),
                    title = "Log Out",
                    subtitle = "Sign out from your account",
                    titleColor = Color(0xFFFF4B4B),
                    subtitleColor = Color(0xFFFF8989),
                    onClick = { 
                        viewModel.signOut()
                        onCloseDrawer()
                    }
                )
            }

            // SUBSCRIPTION
            SidebarSection(title = "SUBSCRIPTION") {
                SidebarItem(
                    icon = Icons.Filled.CreditCard,
                    iconBgColor = Color(0xFFFFC800),
                    title = if (userProfile.isPremium) "Pro Monthly" else "Upgrade to Pro",
                    badge = if (userProfile.isPremium) "ACTIVE" else "FREE",
                    subtitle = if (userProfile.isPremium) "Manage your subscription" else "Unlock all features",
                    onClick = {
                        onNavigate("premium")
                        onCloseDrawer()
                    }
                )
            }
            
            // LEARNING FEATURES
            SidebarSection(title = "FEATURES") {
                SidebarItem(
                    icon = Icons.Filled.SmartToy,
                    iconBgColor = Color(0xFF00E5FF),
                    title = "AI Roleplay Scenarios",
                    subtitle = "Practice real-life conversations",
                    onClick = {
                        onNavigate("ai_roleplay")
                        onCloseDrawer()
                    }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SidebarItem(
                    icon = Icons.Filled.Whatshot,
                    iconBgColor = Color(0xFFFF5252),
                    title = "Weak Words Review",
                    subtitle = "Strengthen your vocabulary",
                    onClick = {
                        onNavigate("weak_words")
                        onCloseDrawer()
                    }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SidebarItem(
                    icon = Icons.Filled.Insights,
                    iconBgColor = Color(0xFF6B4EE6),
                    title = "Fluency Certifications",
                    subtitle = "Test your skills",
                    onClick = {
                        onNavigate("certifications")
                        onCloseDrawer()
                    }
                )

            }

            // PREFERENCES
            SidebarSection(title = "PREFERENCES") {
                SidebarItem(
                    icon = Icons.Filled.Language,
                    iconBgColor = Color(0xFF58CC02),
                    title = "Native Language",
                    subtitle = "Your mother tongue",
                    trailingText = userProfile.nativeLanguage,
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SidebarItem(
                    icon = Icons.Filled.VerifiedUser,
                    iconBgColor = Color(0xFF8E9EFA),
                    title = "${userProfile.targetLanguage} Level",
                    subtitle = "Current proficiency",
                    trailingText = userProfile.level,
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SidebarItem(
                    icon = Icons.Filled.Schedule,
                    iconBgColor = Color(0xFFFF9600),
                    title = "Daily Goal",
                    subtitle = "Minutes per day",
                    trailingText = "${userProfile.dailyGoalMinutes} min",
                    onClick = { 
                        onNavigate("profile")
                        onCloseDrawer()
                    }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SidebarToggleItem(
                    icon = Icons.Filled.RecordVoiceOver,
                    iconBgColor = Color(0xFFCE82FF),
                    title = "AI Voice (Female)",
                    subtitle = "Use female voice for lessons",
                    checked = userProfile.useFemaleVoice,
                    onCheckedChange = { isFemale ->
                        viewModel.updateProfile(userProfile.copy(useFemaleVoice = isFemale))
                    }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                
                val currentTheme = userProfile.themePreference
                val nextTheme = when(currentTheme) {
                    "light" -> "dark"
                    "dark" -> "system"
                    else -> "light"
                }
                val themeLabel = when(currentTheme) {
                    "light" -> "Light Mode"
                    "dark" -> "Dark Mode"
                    else -> "System Theme"
                }
                
                SidebarItem(
                    icon = Icons.Filled.DarkMode,
                    iconBgColor = Color(0xFF6B4EE6),
                    title = "App Theme",
                    subtitle = "Current: $themeLabel",
                    onClick = { 
                        viewModel.updateProfile(userProfile.copy(themePreference = nextTheme))
                    }
                )
            }

            // SUPPORT & LEGAL
            SidebarSection(title = "SUPPORT & LEGAL") {
                SidebarItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    iconBgColor = Color(0xFF1CB0F6),
                    title = "Help Center",
                    subtitle = "Get help and find answers",
                    onClick = { }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SidebarItem(
                    icon = Icons.Filled.Settings,
                    iconBgColor = Color(0xFFAFAFAF),
                    title = "App Settings",
                    subtitle = "Customize your experience",
                    onClick = { 
                        onNavigate("settings")
                        onCloseDrawer()
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SidebarSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B4B4B),
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
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
fun SidebarItem(
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
                    fontSize = 15.sp, 
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
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFF9600),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = subtitle, 
                fontSize = 12.sp, 
                color = subtitleColor,
                lineHeight = 14.sp
            )
        }
        
        if (trailingText != null) {
            Text(
                text = trailingText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFAFAFAF),
                modifier = Modifier.padding(end = 4.dp)
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFFAFAFAF),
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFAFAFAF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun SidebarToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
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
            Text(
                text = title, 
                fontSize = 15.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF4B4B4B)
            )
            Text(
                text = subtitle, 
                fontSize = 12.sp, 
                color = Color.Gray,
                lineHeight = 14.sp
            )
        }
        
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF58CC02),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE5E5E5)
            )
        )
    }
}
