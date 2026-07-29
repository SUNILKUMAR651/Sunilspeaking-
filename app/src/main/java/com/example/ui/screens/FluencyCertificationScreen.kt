package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.ui.components.ConfettiAnimation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.viewmodel.LexiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FluencyCertificationScreen(viewModel: LexiViewModel, onBack: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    var showConfetti by remember { mutableStateOf(false) }
    val certificates = listOf(
        CertificateItem("A1 Beginner", "Basic vocabulary & grammar", 0.2f),
        CertificateItem("A2 Elementary", "Everyday conversations", 0.4f),
        CertificateItem("B1 Intermediate", "Expressing ideas clearly", 0.6f),
        CertificateItem("B2 Upper Intermediate", "Professional fluency", 0.8f),
        CertificateItem("C1 Advanced", "Native-like mastery", 1.0f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fluency Certificates", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.WorkspacePremium,
                        contentDescription = "Certifications",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Your Certification Journey",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Earn verifiable digital badges by mastering modules and improving your overall fluency.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Overall Progress
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Fluency Level", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${(userProfile.certificationProgress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { userProfile.certificationProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.background
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Certificates List
            certificates.forEach { cert ->
                val isUnlocked = userProfile.unlockedCertificates.contains(cert.title)
                val canUnlock = !isUnlocked && userProfile.certificationProgress >= cert.requiredProgress
                
                CertificateCard(
                    cert = cert,
                    isUnlocked = isUnlocked,
                    canUnlock = canUnlock,
                    currentProgress = userProfile.certificationProgress,
                    onClaim = {
                        val newUnlocked = userProfile.unlockedCertificates + cert.title
                        viewModel.updateProfile(userProfile.copy(
                            unlockedCertificates = newUnlocked,
                            level = cert.title
                        ))
                        showConfetti = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (showConfetti) {
            ConfettiAnimation(onFinished = { showConfetti = false })
        }
    }
}

@Composable
fun CertificateCard(
    cert: CertificateItem,
    isUnlocked: Boolean,
    canUnlock: Boolean,
    currentProgress: Float,
    onClaim: () -> Unit
) {
    val borderColor = if (isUnlocked) Color(0xFFFFD700) else MaterialTheme.colorScheme.outlineVariant
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isUnlocked) 2.dp else 1.dp,
                brush = if (isUnlocked) Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFF57F17))) else Brush.linearGradient(listOf(borderColor, borderColor)),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) Color(0xFFFFD700).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = "Unlocked", tint = Color(0xFFFFD700), modifier = Modifier.size(32.dp))
                } else if (canUnlock) {
                    Icon(Icons.Filled.WorkspacePremium, contentDescription = "Ready to claim", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                } else {
                    Icon(Icons.Filled.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cert.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    cert.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (!isUnlocked && !canUnlock) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (currentProgress / cert.requiredProgress).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                }
            }
            
            if (canUnlock) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black)
                ) {
                    Text("Claim")
                }
            }
        }
    }
}

data class CertificateItem(
    val title: String,
    val description: String,
    val requiredProgress: Float
)
