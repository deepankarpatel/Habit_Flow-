package com.habitflow.app.data.repository

import com.habitflow.app.data.local.database.HabitDatabase
import com.habitflow.app.data.local.entity.HabitCompletionEntity
import com.habitflow.app.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import androidx.room.withTransaction

class BackupRestoreManager(
    private val database: HabitDatabase
) {

    suspend fun exportBackupJson(): String {
        val habitEntities = database.habitDao().getAllHabits().first()
        val completionEntities = database.habitCompletionDao().getAllCompletions()

        val root = JSONObject()
        root.put("version", 1)

        val habitsArray = JSONArray()
        for (habit in habitEntities) {
            val obj = JSONObject().apply {
                put("id", habit.id)
                put("name", habit.name)
                put("emoji", habit.emoji)
                put("category", habit.category)
                put("target", habit.target)
                put("unit", habit.unit)
                put("reminderType", habit.reminderType)
                put("intervalMinutes", habit.intervalMinutes)
                put("startTime", habit.startTime)
                put("endTime", habit.endTime)
                put("activeDays", habit.activeDays)
                put("isCompleted", habit.isCompleted)
                put("createdAt", habit.createdAt)
            }
            habitsArray.put(obj)
        }
        root.put("habits", habitsArray)

        val completionsArray = JSONArray()
        for (completion in completionEntities) {
            val obj = JSONObject().apply {
                put("id", completion.id)
                put("habitId", completion.habitId)
                put("date", completion.date)
                put("completedCount", completion.completedCount)
                put("isCompleted", completion.isCompleted)
                put("updatedAt", completion.updatedAt)
            }
            completionsArray.put(obj)
        }
        root.put("completions", completionsArray)

        return root.toString(2)
    }

    suspend fun importBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            val habitsArray = root.optJSONArray("habits") ?: JSONArray()
            val completionsArray = root.optJSONArray("completions") ?: JSONArray()

            // 1. Parse everything first. Any bad row throws before the DB is touched.
            val habits = (0 until habitsArray.length()).map { i ->
                val obj = habitsArray.getJSONObject(i)
                HabitEntity(
                    id = obj.getInt("id"),
                    name = obj.getString("name"),
                    emoji = obj.getString("emoji"),
                    category = obj.optString("category", "Personal"),
                    target = obj.optInt("target", 1),
                    unit = obj.optString("unit", ""),
                    reminderType = obj.optString("reminderType", "NONE"),
                    intervalMinutes = obj.optInt("intervalMinutes", 60),
                    startTime = obj.optString("startTime", "09:00"),
                    endTime = obj.optString("endTime", "21:00"),
                    activeDays = obj.optString("activeDays", "Mon,Tue,Wed,Thu,Fri,Sat,Sun"),
                    isCompleted = obj.optBoolean("isCompleted", false),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                )
            }
            val habitIds = habits.map { it.id }.toSet()

            val completions = (0 until completionsArray.length()).map { i ->
                val obj = completionsArray.getJSONObject(i)
                HabitCompletionEntity(
                    id = 0, // let Room assign ids; (habitId, date) is the real key
                    habitId = obj.getInt("habitId"),
                    date = obj.getString("date"),
                    completedCount = obj.getInt("completedCount"),
                    isCompleted = obj.getBoolean("isCompleted"),
                    updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                )
            }.filter { it.habitId in habitIds } // avoid foreign key failures

            // 2. Write all-or-nothing.
            database.withTransaction {
                habits.forEach { database.habitDao().upsertHabit(it) }
                completions.forEach { database.habitCompletionDao().upsertCompletion(it) }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun resetAllData() {
        database.habitDao().deleteAllHabits()
    }
}
