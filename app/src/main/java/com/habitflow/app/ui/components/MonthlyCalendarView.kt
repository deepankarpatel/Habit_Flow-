package com.habitflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.habitflow.app.ui.theme.AppTheme

private val Background: Color @Composable get() = AppTheme.colors.background
private val Green: Color @Composable get() = AppTheme.colors.accent
private val Dark: Color @Composable get() = AppTheme.colors.text
private val Muted: Color @Composable get() = AppTheme.colors.muted
private val Red: Color @Composable get() = AppTheme.colors.danger
@Composable
fun MonthlyCalendarView(
    completedDates: Set<LocalDate>,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    val daysInMonth = currentYearMonth.lengthOfMonth()
    val firstDayOfWeek = currentYearMonth.atDay(1).dayOfWeek.value // 1 (Mon) .. 7 (Sun)
    val today = LocalDate.now()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Month Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
            )

            Row {
                TextButton(
                    onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }
                ) {
                    Text(text = "←", fontSize = 18.sp, color = Green)
                }

                TextButton(
                    onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }
                ) {
                    Text(text = "→", fontSize = 18.sp, color = Green)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Weekday Column Titles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Box(
                    modifier = Modifier.width(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Muted)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        val totalCells = firstDayOfWeek - 1 + daysInMonth
        val rows = (totalCells + 6) / 7

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0..6) {
                        val dayNumber = row * 7 + col - (firstDayOfWeek - 2)
                        if (dayNumber in 1..daysInMonth) {
                            val date = currentYearMonth.atDay(dayNumber)
                            val isCompleted = date in completedDates
                            val isToday = date == today

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCompleted -> Green
                                            isToday -> AppTheme.colors.accentSoft
                                            else -> AppTheme.colors.divider
                                        }
                                    )
                                    .clickable { onDateSelected(date.toString()) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = if (isToday || isCompleted) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCompleted) Color.White else Dark
                                )
                            }
                        } else {
                            Box(modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
        }
    }
}
