package com.habitflow.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.domain.model.HabitItemUiState
import com.habitflow.app.ui.components.CalendarStrip
import com.habitflow.app.ui.components.EmptyHabitsState
import com.habitflow.app.ui.components.HabitCard
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.habitflow.app.ui.theme.AppTheme
import com.habitflow.app.ui.components.AnimatedEntrance

private val Background: Color @Composable get() = AppTheme.colors.background
private val Green: Color @Composable get() = AppTheme.colors.accent
private val Dark: Color @Composable get() = AppTheme.colors.text
private val Muted: Color @Composable get() = AppTheme.colors.muted
private val Red: Color @Composable get() = AppTheme.colors.danger

@Composable
fun HabitHomeScreen(
    habitItems: List<HabitItemUiState>,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onAddHabit: () -> Unit,
    onIncrement: (habitId: Int, target: Int) -> Unit,
    onDecrement: (habitId: Int, target: Int) -> Unit,
    onToggleCompletion: (habitId: Int, target: Int) -> Unit,
    onHabitClick: (HabitItemUiState) -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {

    val completedCount = habitItems.count { it.isCompleted }
    val totalHabits = habitItems.size

    val overallProgressFraction = if (totalHabits > 0) {
        habitItems.sumOf { it.progressFraction.toDouble() }.toFloat() / totalHabits
    } else {
        0f
    }

    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    val todayFormatted = try {
        LocalDate.parse(selectedDate).format(DateTimeFormatter.ofPattern("EEEE, dd MMMM"))
    } catch (_: Exception) {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM"))
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
                text = "HabitFlow",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Green
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "$greeting 👋",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = todayFormatted,
                fontSize = 14.sp,
                color = Muted
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Calendar Strip
            CalendarStrip(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "DAILY PROGRESS",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Muted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.colors.card
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your daily goals",
                            fontWeight = FontWeight.SemiBold,
                            color = Dark
                        )

                        Text(
                            text = "${(overallProgressFraction * 100).toInt()}%",
                            fontWeight = FontWeight.Bold,
                            color = Green
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.track)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(overallProgressFraction)
                                .clip(CircleShape)
                                .background(Green)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$completedCount of $totalHabits habits completed",
                        fontSize = 12.sp,
                        color = Muted
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onAddHabit
                ) {
                    Text(
                        text = "+ Add",
                        color = Green,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "$totalHabits habits",
                    fontSize = 13.sp,
                    color = Green
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (habitItems.isEmpty()) {
                EmptyHabitsState(onAddHabit = onAddHabit)
            } else {
                habitItems.forEachIndexed { index, habitItem ->
                    key(habitItem.habit.id) {
                        AnimatedEntrance(index = index) {
                            HabitCard(
                                habitItem = habitItem,
                                onIncrement = {
                                    onIncrement(habitItem.habit.id, habitItem.target)
                                },
                                onDecrement = {
                                    onDecrement(habitItem.habit.id, habitItem.target)
                                },
                                onToggleComplete = {
                                    onToggleCompletion(habitItem.habit.id, habitItem.target)
                                },
                                onClick = {
                                    onHabitClick(habitItem)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

