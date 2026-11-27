package com.habitflow.app.ui.screens

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
import com.habitflow.app.domain.usecase.StreakCalculator
import com.habitflow.app.ui.components.MonthlyCalendarView
import java.time.LocalDate
import com.habitflow.app.ui.theme.AppTheme
import com.habitflow.app.ui.components.PulsingFlame
import com.habitflow.app.ui.components.rememberCountUp


private val Background: Color @Composable get() = AppTheme.colors.background
private val Green: Color @Composable get() = AppTheme.colors.accent
private val Dark: Color @Composable get() = AppTheme.colors.text
private val Muted: Color @Composable get() = AppTheme.colors.muted
private val Red: Color @Composable get() = AppTheme.colors.danger

@Composable
fun ReportsScreen(
    habitItems: List<HabitItemUiState>,
    completedDates: Set<LocalDate> = emptySet(),
    onDateSelected: (String) -> Unit = {},
    bottomBar: @Composable () -> Unit
) {
    val totalHabits = habitItems.size
    val completedToday = habitItems.count { it.isCompleted }
    val overallPercentage = if (totalHabits > 0) {
        (habitItems.sumOf { it.progressFraction.toDouble() }.toFloat() / totalHabits * 100).toInt()
    } else {
        0
    }

    val currentStreak = StreakCalculator.calculateCurrentStreak(completedDates)
    val bestStreak = StreakCalculator.calculateBestStreak(completedDates)
    val totalDays = completedDates.size
    val animatedPercent = rememberCountUp(overallPercentage)
    val animatedStreak = rememberCountUp(currentStreak)
    val animatedTotal = rememberCountUp(totalDays)

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
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Completion", fontSize = 12.sp, color = Muted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "${animatedPercent}%", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Green)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$completedToday of $totalHabits done", fontSize = 11.sp, color = Muted)
                    }
                }

                // Streak Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Active Streak", fontSize = 12.sp, color = Muted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PulsingFlame(active = currentStreak > 0)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "$animatedStreak days", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Dark)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Best: $bestStreak days", fontSize = 11.sp, color = Muted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Days Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Total Active History", fontSize = 12.sp, color = Muted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "⭐ $totalDays Days Recorded", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Dark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Monthly Calendar Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Monthly Overview",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Dark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    MonthlyCalendarView(
                        completedDates = completedDates,
                        onDateSelected = onDateSelected
                    )
                }
            }
        }
    }
}
