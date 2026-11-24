
package com.habitflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitflow.app.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Upsert

@Dao
interface HabitDao {

    // Insert a new habit
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    // Get all habits
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    // Get a particular habit
    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Int): HabitEntity?

    // Update an existing habit
    @Update
    suspend fun updateHabit(habit: HabitEntity)

    // Delete a habit
    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Upsert
    suspend fun upsertHabit(habit: HabitEntity)

    // Delete all habits
    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
}
