package com.habitflow.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.domain.model.HabitItemUiState

private val Background = Color(0xFFF7F6F1)
private val Green = Color(0xFF315C48)
private val Dark = Color(0xFF252B27)
private val Muted = Color(0xFF858B85)

@Composable
fun ReportsScreen(
    habitItems: List<HabitItemUiState>,
    bottomBar: @Composable () -> Unit
) {
    val totalHabits = habitItems.size
    val completedToday = habitItems.count { it.isCompleted }
    val overallPercentage = if (totalHabits > 0) {
        (habitItems.sumOf { it.progressFraction.toDouble() }.toFloat() / totalHabits * 100).toInt()
    } else {
        0
    }

    Scaffold(
        containerColor = Background,
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(top = 25.dp, bottom = 20.dp)
        ) {
            Text(
                text = "Reports & Insights",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Track your consistency and streaks.",
                fontSize = 14.sp,
                color = Muted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today's Progress Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Completion", fontSize = 12.sp, color = Muted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "$overallPercentage%", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Green)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$completedToday of $totalHabits done", fontSize = 11.sp, color = Muted)
                    }
                }

                // Active Habits Count Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Total Habits", fontSize = 12.sp, color = Muted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "$totalHabits", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Dark)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Active routines", fontSize = 11.sp, color = Muted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Consistency Overview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Weekly Consistency",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Dark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { idx, day ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (idx < 5 && overallPercentage > 0) Green else Color(0xFFF0F1ED),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (idx < 5 && overallPercentage > 0) {
                                        Text(text = "✓", color = Color.White, fontSize = 14.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(text = day, fontSize = 12.sp, color = Muted)
                            }
                        }
                    }
                }
            }
        }
    }
}
