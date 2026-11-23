package com.habitflow.app.domain.model

data class HabitItemUiState(
    val habit: Habit,
    val date: String,
    val completedCount: Int = 0,
    val target: Int = 1,
    val isCompleted: Boolean = false
) {
    val progressFraction: Float
        get() = if (target > 0) (completedCount.toFloat() / target).coerceIn(0f, 1f) else 0f

    val progressText: String
        get() {
            val unitStr = if (habit.unit.isNotBlank()) " ${habit.unit}" else ""
            return "$completedCount of $target$unitStr"
        }
}
