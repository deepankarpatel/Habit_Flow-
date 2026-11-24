package com.habitflow.app.data.repository

import com.habitflow.app.data.local.database.HabitDatabase
import com.habitflow.app.data.local.entity.HabitCompletionEntity
import com.habitflow.app.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

class BackupRestoreManager(
    private val database: HabitDatabase
) {

    suspend fun exportBackupJson(): String {
        val habitEntities = database.habitDao().getAllHabits().first()
        val completionEntities = database.habitCompletionDao().getAllCompletedRecords().first()

        val root = JSONObject()

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

            for (i in 0 until habitsArray.length()) {
                val obj = habitsArray.getJSONObject(i)
                val entity = HabitEntity(
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
                database.habitDao().insertHabit(entity)
            }

            for (i in 0 until completionsArray.length()) {
                val obj = completionsArray.getJSONObject(i)
                val entity = HabitCompletionEntity(
                    id = obj.optInt("id", 0),
                    habitId = obj.getInt("habitId"),
                    date = obj.getString("date"),
                    completedCount = obj.getInt("completedCount"),
                    isCompleted = obj.getBoolean("isCompleted"),
                    updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                )
                database.habitCompletionDao().upsertCompletion(entity)
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
