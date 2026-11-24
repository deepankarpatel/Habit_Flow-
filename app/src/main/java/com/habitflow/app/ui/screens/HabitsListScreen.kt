package com.habitflow.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.domain.model.HabitItemUiState
import com.habitflow.app.ui.components.EmptyHabitsState
import com.habitflow.app.ui.components.HabitCard
import com.habitflow.app.ui.theme.AppTheme

private val Background: Color @Composable get() = AppTheme.colors.background
private val Green: Color @Composable get() = AppTheme.colors.accent
private val Dark: Color @Composable get() = AppTheme.colors.text
private val Muted: Color @Composable get() = AppTheme.colors.muted
private val Red: Color @Composable get() = AppTheme.colors.danger

@Composable
fun HabitsListScreen(
    habitItems: List<HabitItemUiState>,
    onAddHabit: () -> Unit,
    onIncrement: (habitId: Int, target: Int) -> Unit,
    onDecrement: (habitId: Int, target: Int) -> Unit,
    onToggleCompletion: (habitId: Int, target: Int) -> Unit,
    onHabitClick: (HabitItemUiState) -> Unit,
    bottomBar: @Composable () -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Habits",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Dark
                    )
                    Text(
                        text = "${habitItems.size} habit(s) configured",
                        fontSize = 13.sp,
                        color = AppTheme.colors.muted
                    )
                }

                TextButton(onClick = onAddHabit) {
                    Text(
                        text = "+ Add",
                        color = Green,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (habitItems.isEmpty()) {
                EmptyHabitsState(onAddHabit = onAddHabit)
            } else {
                habitItems.forEach { habitItem ->
                    key(habitItem.habit.id) {
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
