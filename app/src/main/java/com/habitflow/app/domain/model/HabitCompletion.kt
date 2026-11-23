package com.habitflow.app.domain.model

data class HabitCompletion(
    val id: Int = 0,
    val habitId: Int,
    val date: String,
    val completedCount: Int = 0,
    val isCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
