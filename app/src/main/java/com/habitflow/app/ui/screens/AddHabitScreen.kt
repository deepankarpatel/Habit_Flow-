package com.habitflow.app.ui.screens

import androidx.compose.runtime.Composable
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.ui.components.HabitFormScreen

@Composable
fun AddHabitScreen(
    onBack: () -> Unit,
    onSave: (Habit) -> Unit
) {
    HabitFormScreen(
        title = "New habit",
        initial = null,
        onBack = onBack,
        onSave = onSave
    )
}