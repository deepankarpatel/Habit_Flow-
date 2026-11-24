package com.habitflow.app.data.repository

import com.habitflow.app.data.local.dao.HabitCompletionDao
import com.habitflow.app.data.local.dao.HabitDao
import com.habitflow.app.data.local.entity.HabitCompletionEntity
import com.habitflow.app.data.local.entity.toHabit
import com.habitflow.app.data.local.entity.toHabitEntity
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.domain.model.HabitItemUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao
) {

    // Observe all habits combined with their completions for a specific date
    fun getHabitsForDate(date: String): Flow<List<HabitItemUiState>> {
        return combine(
            habitDao.getAllHabits(),
            completionDao.getCompletionsForDate(date)
        ) { habits, completions ->
            val completionMap = completions.associateBy { it.habitId }

            habits.map { entity ->
                val habit = entity.toHabit()
                val completion = completionMap[habit.id]
                val completedCount = completion?.completedCount ?: 0
                val target = if (habit.target > 0) habit.target else 1
                val isCompleted = completion?.isCompleted ?: (completedCount >= target)

                HabitItemUiState(
                    habit = habit,
                    date = date,
                    completedCount = completedCount,
                    target = target,
                    isCompleted = isCompleted
                )
            }
        }
    }

    // Observe all raw habits
    fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map { entities ->
            entities.map { it.toHabit() }
        }
    }

    // Observe set of dates with at least one completed record
    fun getAllCompletedDates(): Flow<Set<LocalDate>> {
        return completionDao.getAllCompletedRecords().map { list ->
            list.mapNotNull {
                try {
                    java.time.LocalDate.parse(it.date)
                } catch (_: Exception) {
                    null
                }
            }.toSet()
        }
    }

    // Increment progress by 1 for a specific habit and date
    suspend fun incrementProgress(habitId: Int, date: String, target: Int) {
        val currentCompletion = completionDao.getCompletionDirect(habitId, date)
        val currentCount = currentCompletion?.completedCount ?: 0
        val newCount = currentCount + 1
        val isCompleted = newCount >= target

        completionDao.upsertCompletion(
            HabitCompletionEntity(
                id = currentCompletion?.id ?: 0,
                habitId = habitId,
                date = date,
                completedCount = newCount,
                isCompleted = isCompleted,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // Decrement progress by 1 for a specific habit and date
    suspend fun decrementProgress(habitId: Int, date: String, target: Int) {
        val currentCompletion = completionDao.getCompletionDirect(habitId, date)
        val currentCount = currentCompletion?.completedCount ?: 0
        val newCount = (currentCount - 1).coerceAtLeast(0)
        val isCompleted = newCount >= target

        completionDao.upsertCompletion(
            HabitCompletionEntity(
                id = currentCompletion?.id ?: 0,
                habitId = habitId,
                date = date,
                completedCount = newCount,
                isCompleted = isCompleted,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // Toggle completion for a specific habit and date
    suspend fun toggleCompletion(habitId: Int, date: String, target: Int) {
        val currentCompletion = completionDao.getCompletionDirect(habitId, date)
        val isCurrentlyCompleted = currentCompletion?.isCompleted ?: false

        val (newCount, newIsCompleted) = if (isCurrentlyCompleted) {
            0 to false
        } else {
            target to true
        }

        completionDao.upsertCompletion(
            HabitCompletionEntity(
                id = currentCompletion?.id ?: 0,
                habitId = habitId,
                date = date,
                completedCount = newCount,
                isCompleted = newIsCompleted,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // Save a new habit
    suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit.toHabitEntity())
    }

    // Update an existing habit
    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit.toHabitEntity())
    }

    // Delete a habit and its completions
    suspend fun deleteHabit(habit: Habit) {
        completionDao.deleteCompletionsForHabit(habit.id)
        habitDao.deleteHabit(habit.toHabitEntity())
    }
}
