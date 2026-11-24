package com.habitflow.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.habitflow.app.data.local.database.HabitDatabase
import com.habitflow.app.data.local.entity.toHabit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getIntExtra(EXTRA_HABIT_ID, 0)
        if (habitId <= 0) return

        val appContext = context.applicationContext
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = appContext.getSharedPreferences("habitflow_prefs", Context.MODE_PRIVATE)
                if (!prefs.getBoolean("key_notifications_enabled", true)) return@launch

                val entity = HabitDatabase.getDatabase(appContext)
                    .habitDao().getHabitById(habitId) ?: return@launch  // habit was deleted
                val habit = entity.toHabit()

                NotificationHelper.showHabitNotification(
                    context = appContext,
                    habitId = habit.id,
                    habitName = habit.name,
                    emoji = habit.emoji
                )
                // Chain the next reminder
                ReminderScheduler.scheduleHabitReminder(appContext, habit)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_HABIT_NAME = "extra_habit_name"
        const val EXTRA_EMOJI = "extra_emoji"
    }
}