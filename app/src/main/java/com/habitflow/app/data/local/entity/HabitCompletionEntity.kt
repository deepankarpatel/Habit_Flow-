package com.habitflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.habitflow.app.domain.model.HabitCompletion

@Entity(
    tableName = "habit_completions",
    indices = [
        Index(value = ["habitId", "date"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitCompletionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val habitId: Int,

    val date: String,

    val completedCount: Int,

    val isCompleted: Boolean,

    val updatedAt: Long = System.currentTimeMillis()
)

fun HabitCompletionEntity.toHabitCompletion(): HabitCompletion {
    return HabitCompletion(
        id = id,
        habitId = habitId,
        date = date,
        completedCount = completedCount,
        isCompleted = isCompleted,
        updatedAt = updatedAt
    )
}

fun HabitCompletion.toHabitCompletionEntity(): HabitCompletionEntity {
    return HabitCompletionEntity(
        id = id,
        habitId = habitId,
        date = date,
        completedCount = completedCount,
        isCompleted = isCompleted,
        updatedAt = updatedAt
    )
}
