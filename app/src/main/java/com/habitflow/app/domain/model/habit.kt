
package com.habitflow.app.domain.model

data class Habit(
    val id: Int,
    val name: String,
    val emoji: String,
    val progressText: String,
    val isCompleted: Boolean = false
)
