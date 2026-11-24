package com.habitflow.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getIntExtra(EXTRA_HABIT_ID, 0)
        val habitName = intent.getStringExtra(EXTRA_HABIT_NAME) ?: "Habit"
        val emoji = intent.getStringExtra(EXTRA_EMOJI) ?: "🎯"

        if (habitId > 0) {
            NotificationHelper.showHabitNotification(
                context = context,
                habitId = habitId,
                habitName = habitName,
                emoji = emoji
            )
        }
    }

    companion object {
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_HABIT_NAME = "extra_habit_name"
        const val EXTRA_EMOJI = "extra_emoji"
    }
}
