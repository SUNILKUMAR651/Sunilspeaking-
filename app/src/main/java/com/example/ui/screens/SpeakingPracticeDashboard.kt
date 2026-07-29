package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.School
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

data class SpeakingCategory(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val bgColor: Color,
    val iconColor: Color
)

val speakingCategories = listOf(
    SpeakingCategory("job_interview", "Job Interview English Practice", "Guided interview role-play with confidence...", Icons.Filled.BusinessCenter, Color(0xFFF0F4FF), Color(0xFF4B72FF)),
    SpeakingCategory("hr_round", "HR Round Speaking Test", "Practice common HR speaking questions...", Icons.Filled.HeadsetMic, Color(0xFFFFF0F5), Color(0xFFFF528A)),
    SpeakingCategory("bpo_voice", "BPO / Call Center Voice Test", "Boost clarity for support calls with pronunciation...", Icons.Filled.HeadsetMic, Color(0xFFE8FAF0), Color(0xFF00C48C)),
    SpeakingCategory("oet_speaking", "OET Speaking (Nurses / Doctors)", "Healthcare speaking for professional medical...", Icons.Filled.LocalHospital, Color(0xFFFFF0F5), Color(0xFFFF4B4B)),
    SpeakingCategory("canada_pr", "Canada PR / IRCC Language Proof", "Prepare for Canadian language interviews and...", Icons.Filled.Language, Color(0xFFFFF0F0), Color(0xFFFF3333)),
    SpeakingCategory("uk_visa", "UK Visa English Requirement Prep", "Confident responses for UK visa interviews...", Icons.Filled.Language, Color(0xFFF0F5FF), Color(0xFF3366FF)),
    SpeakingCategory("australia_pr", "Australia PR IELTS Prep", "IELTS speaking prep focused on Australian...", Icons.Filled.Language, Color(0xFFFFF8E1), Color(0xFFFFB300)),
    SpeakingCategory("sample_speaking", "Sample Speaking Test", "Train speaking prompts and timing under time...", Icons.Filled.ChatBubble, Color(0xFFE8FAF0), Color(0xFF00C48C)),
    SpeakingCategory("us_visa", "US Work Visa (H1B) Interview", "US work visa interview practice.", Icons.Filled.BusinessCenter, Color(0xFFF0F4FF), Color(0xFF4B72FF)),
    SpeakingCategory("university_admission", "University Admission Interview", "Admission interview speaking with structure.", Icons.Filled.School, Color(0xFFF5E8FF), Color(0xFF9C27B0)),
    SpeakingCategory("mba_interview", "MBA Interview English Practice", "MBA interview speaking with confidence and...", Icons.Filled.BusinessCenter, Color(0xFFFFF3E0), Color(0xFFFF9800))
)

@Composable
fun SpeakingPracticeDashboard(onBack: () -> Unit, onNavigateToCategory: (String) -> Unit) {
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
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Speaking Practice",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    WideBanner("IELTS Speaking Test", "IELTS speaking with AI feedback.", Color(0xFF6C63FF), Modifier.weight(1f))
                    WideBanner("PTE Speaking Test", "PTE speaking drills with feedback.", Color(0xFF00C48C), Modifier.weight(1f))
                }
            }

            items(speakingCategories) { category ->
                SpeakingCategoryCard(category) {
                    onNavigateToCategory(category.id)
                }
            }
        }
    }
}

@Composable
fun WideBanner(title: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Topics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun SpeakingCategoryCard(category: SpeakingCategory, onClick: () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = category.bgColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    category.icon,
                    contentDescription = null,
                    tint = category.iconColor,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(category.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(4.dp))
            Text(category.description, fontSize = 12.sp, color = Color(0xFF64748B), maxLines = 2)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = category.iconColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Topics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
