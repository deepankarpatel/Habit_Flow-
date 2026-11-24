package com.habitflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.habitflow.app.ui.theme.HabitFlowTheme

import com.habitflow.app.ui.theme.AppTheme

private val Background: Color @Composable get() = AppTheme.colors.background
private val Green: Color @Composable get() = AppTheme.colors.accent
private val Dark: Color @Composable get() = AppTheme.colors.text
private val Muted: Color @Composable get() = AppTheme.colors.muted
private val Red: Color @Composable get() = AppTheme.colors.danger
private val LightGreen: Color @Composable get() = AppTheme.colors.accentSoft

@Composable
fun HabitCard(
    habitItem: HabitItemUiState,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onToggleComplete: () -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val habit = habitItem.habit

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.card
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppTheme.colors.track),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = habit.emoji,
                        fontSize = 23.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Name & Progress
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = habit.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Dark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = habitItem.progressText,
                        fontSize = 12.sp,
                        color = Muted
                    )
                }

                // Controls: Minus, Plus, Complete Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Decrement (-) Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.divider)
                            .clickable { onDecrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Dark
                        )
                    }

                    // Increment (+) Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(LightGreen)
                            .clickable { onIncrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Green
                        )
                    }

                    // Completion (✓) Checkbox
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (habitItem.isCompleted) Green else AppTheme.colors.divider
                            )
                            .clickable { onToggleComplete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (habitItem.isCompleted) "✓" else "",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Progress bar indicator
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.divider)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(habitItem.progressFraction)
                        .clip(CircleShape)
                        .background(if (habitItem.isCompleted) Green else AppTheme.colors.secondary)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    HabitFlowTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HabitCard(
                habitItem = HabitItemUiState(
                    habit = Habit(1, "Drink Water", "💧", "5 of 8 glasses", target = 8, unit = "glasses"),
                    date = "2026-03-09",
                    completedCount = 5,
                    target = 8,
                    isCompleted = false
                ),
                onIncrement = {},
                onDecrement = {},
                onToggleComplete = {},
                onClick = {}
            )
        }
    }
}
