package com.habitflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.habitflow.app.ui.theme.AppTheme

private val Background: Color @Composable get() = AppTheme.colors.background
private val Green: Color @Composable get() = AppTheme.colors.accent
private val Dark: Color @Composable get() = AppTheme.colors.text
private val Muted: Color @Composable get() = AppTheme.colors.muted
private val Red: Color @Composable get() = AppTheme.colors.danger

@Composable
fun CalendarStrip(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLocalDate = try {
        LocalDate.parse(selectedDate)
    } catch (_: Exception) {
        LocalDate.now()
    }

    // Display past 6 days + today (7 days window)
    val days = (6 downTo 0).map { offset ->
        LocalDate.now().minusDays(offset.toLong())
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { date ->
            val isSelected = date == selectedLocalDate
            val dayName = date.format(DateTimeFormatter.ofPattern("EEE"))
            val dayNum = date.dayOfMonth.toString()
            val dateStr = date.toString()

            Box(
                modifier = Modifier
                    .width(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) Green else AppTheme.colors.card)
                    .clickable { onDateSelected(dateStr) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else Muted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = dayNum,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Dark
                    )
                }
            }
        }
    }
}
