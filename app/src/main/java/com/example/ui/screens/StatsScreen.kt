package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AreaChart
import com.example.ui.components.ChartDataPoint

import com.example.data.UserProfile

@Composable
fun StatsSection(profile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val vocabData = profile.vocabularyHistory.mapIndexed { index, value ->
            ChartDataPoint(days.getOrElse(index % days.size) { "" }, value)
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            AreaChart(
                data = vocabData,
                lineColor = Color(0xFF69F0AE),
                gradientColors = listOf(Color(0xFF69F0AE).copy(alpha = 0.5f), Color(0xFF69F0AE).copy(alpha = 0.0f)),
                title = "Vocabulary Growth (Words)",
                modifier = Modifier.padding(16.dp)
            )
        }
        
        val weeks = listOf("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6")
        val lessonsData = profile.lessonHistory.mapIndexed { index, value ->
            ChartDataPoint(weeks.getOrElse(index % weeks.size) { "" }, value)
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            AreaChart(
                data = lessonsData,
                lineColor = Color(0xFF448AFF),
                gradientColors = listOf(Color(0xFF448AFF).copy(alpha = 0.5f), Color(0xFF448AFF).copy(alpha = 0.0f)),
                title = "Lessons Completed",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
