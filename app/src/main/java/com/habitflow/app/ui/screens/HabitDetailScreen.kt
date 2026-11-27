package com.habitflow.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.domain.model.HabitItemUiState
import com.habitflow.app.domain.usecase.StreakCalculator
import com.habitflow.app.ui.components.HabitFormScreen
import com.habitflow.app.ui.components.PulsingFlame
import com.habitflow.app.ui.components.rememberCountUp
import com.habitflow.app.ui.theme.AppTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HabitDetailScreen(
    habitItem: HabitItemUiState,
    onBack: () -> Unit,
    onSave: (Habit) -> Unit,
    onDelete: (Habit) -> Unit,
    completedDates: Set<LocalDate> = emptySet()
) {
    val habit = habitItem.habit

    val scheduledDays = habit.activeDays.mapNotNull { name ->
        DayOfWeek.entries.firstOrNull {
            it.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) == name
        }
    }.toSet()
    val currentStreak = StreakCalculator.calculateCurrentStreak(
        completedDates, activeDays = scheduledDays
    )
    val bestStreak = StreakCalculator.calculateBestStreak(
        completedDates, activeDays = scheduledDays
    )

    HabitFormScreen(
        title = "Edit habit",
        initial = habit,
        onBack = onBack,
        onSave = onSave,
        onDelete = { onDelete(habit) },
        topContent = {
            StreakStats(
                current = currentStreak,
                best = bestStreak,
                total = completedDates.size
            )
        }
    )
}

@Composable
private fun StreakStats(current: Int, best: Int, total: Int) {
    val colors = AppTheme.colors
    val animatedCurrent = rememberCountUp(current)
    val animatedBest = rememberCountUp(best)
    val animatedTotal = rememberCountUp(total)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Current streak", fontSize = 12.sp, color = colors.muted)
                Row {
                    PulsingFlame(active = current > 0, fontSize = 20.sp)
                    Text(
                        text = " $animatedCurrent days",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                }
            }
            Column {
                Text("Best", fontSize = 12.sp, color = colors.muted)
                Text(
                    text = "$animatedBest days",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            }
            Column {
                Text("Total", fontSize = 12.sp, color = colors.muted)
                Text(
                    text = "$animatedTotal days",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            }
        }
    }
}
