
package com.habitflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.habitflow.app.domain.model.Habit

@Entity(tableName = "habits")
data class HabitEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val emoji: String,

    val category: String,

    val target: Int,

    val unit: String,

    val reminderType: String,

    val intervalMinutes: Int,

    val startTime: String,

    val endTime: String,

    // Stored as comma-separated text
    val activeDays: String,

    val isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()

)

// Convert database entity into domain model
fun HabitEntity.toHabit(): Habit {

    val currentProgress = if (isCompleted) target else 0
    val formattedProgressText = if (unit.isBlank()) {
        "$currentProgress of $target"
    } else {
        "$currentProgress of $target $unit"
    }

    return Habit(
        id = id,
        name = name,
        emoji = emoji,
        progressText = formattedProgressText,
        category = category,
        target = target,
        unit = unit,
        reminderType = reminderType,
        intervalMinutes = intervalMinutes,
        startTime = startTime,
        endTime = endTime,
        isCompleted = isCompleted,
        activeDays = if (activeDays.isBlank()) emptyList() else activeDays.split(",")
    )
}

// Convert domain model into database entity
fun Habit.toHabitEntity(): HabitEntity {

    return HabitEntity(
        id = id,
        name = name,
        emoji = emoji,
        category = category,
        target = target,
        unit = unit,
        reminderType = reminderType,
        intervalMinutes = intervalMinutes,
        startTime = startTime,
        endTime = endTime,
        isCompleted = isCompleted,
        activeDays = activeDays.joinToString(",")
    )
}
