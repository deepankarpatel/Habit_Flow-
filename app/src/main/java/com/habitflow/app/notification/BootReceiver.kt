package com.habitflow.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.habitflow.app.data.local.database.HabitDatabase
import com.habitflow.app.data.local.entity.toHabit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = HabitDatabase.getDatabase(context)
                    val habits = db.habitDao().getAllHabits().first()

                    habits.forEach { entity ->
                        val habit = entity.toHabit()
                        if (habit.reminderType != "NONE") {
                            ReminderScheduler.scheduleHabitReminder(context, habit)
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
