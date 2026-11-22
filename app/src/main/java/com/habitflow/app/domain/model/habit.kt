
package com.habitflow.app.domain.model

data class Habit(
    val id: Int,
    val name: String,
    val emoji: String,
    val progressText: String,

    // Completion
    val isCompleted: Boolean = false,

    // Habit details
    val category: String = "Personal",
    val target: Int = 1,
    val unit: String = "",

    // Reminder configuration
    val reminderType: String = "NONE",
    val intervalMinutes: Int = 60,
    val startTime: String = "09:00",
    val endTime: String = "21:00",

    // Active weekdays
    val activeDays: List<String> = listOf(
        "Mon", "Tue", "Wed", "Thu",
        "Fri", "Sat", "Sun"
    )
)
