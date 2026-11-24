package com.habitflow.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.habitflow.app.domain.model.Habit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import com.habitflow.app.data.local.database.HabitDatabase
import com.habitflow.app.data.local.entity.toHabit
import kotlinx.coroutines.flow.first

object ReminderScheduler {



    private fun parseTime(value: String, fallback: LocalTime): LocalTime =
        try { LocalTime.parse(value) } catch (_: Exception) { fallback }

    // Returns the next moment this habit should remind, or null if it never should.
    fun computeNextTrigger(habit: Habit, now: LocalDateTime = LocalDateTime.now()): LocalDateTime? {
        if (habit.reminderType == "NONE") return null

        val start = parseTime(habit.startTime, LocalTime.of(9, 0))
        val end = parseTime(habit.endTime, LocalTime.of(21, 0))
        val activeDays = habit.activeDays.mapNotNull { name ->
            DayOfWeek.entries.firstOrNull {
                it.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) == name
            }
        }.toSet()
        if (activeDays.isEmpty()) return null

        // Times of day to fire on an active day
        val times = mutableListOf(start)
        if (habit.reminderType == "INTERVAL") {
            val step = habit.intervalMinutes.coerceAtLeast(1).toLong()
            var t = start.plusMinutes(step)
            while (t.isAfter(start) && !t.isAfter(end)) {   // isAfter(start) stops midnight wrap-around
                times.add(t)
                t = t.plusMinutes(step)
            }
        }

        for (offset in 0..7) {
            val date = now.toLocalDate().plusDays(offset.toLong())
            if (date.dayOfWeek !in activeDays) continue
            times.map { LocalDateTime.of(date, it) }
                .firstOrNull { it.isAfter(now) }
                ?.let { return it }
        }
        return null
    }

    // Cancel alarms for all habits (used by the notifications toggle off, and by reset)
    suspend fun cancelAll(context: Context) {
        val habits = HabitDatabase.getDatabase(context).habitDao().getAllHabits().first()
        habits.forEach { cancelHabitReminder(context, it.id) }
    }

    // Reschedule alarms for all habits that have a reminder (toggle on, after import)
    suspend fun rescheduleAll(context: Context) {
        val habits = HabitDatabase.getDatabase(context).habitDao().getAllHabits().first()
        habits.forEach { entity ->
            val habit = entity.toHabit()
            if (habit.reminderType != "NONE") scheduleHabitReminder(context, habit)
        }
    }

    fun scheduleHabitReminder(context: Context, habit: Habit) {

        val prefs = context.getSharedPreferences("habitflow_prefs", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("key_notifications_enabled", true)) {
            cancelHabitReminder(context, habit.id)
            return
        }

        if (habit.reminderType == "NONE") {
            cancelHabitReminder(context, habit.id)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            putExtra(HabitReminderReceiver.EXTRA_HABIT_ID, habit.id)
            putExtra(HabitReminderReceiver.EXTRA_HABIT_NAME, habit.name)
            putExtra(HabitReminderReceiver.EXTRA_EMOJI, habit.emoji)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerDateTime = computeNextTrigger(habit)
            if (triggerDateTime == null) {
                cancelHabitReminder(context, habit.id)
                return
            }

        val triggerAtMillis = triggerDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()

        try {
            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
                )
            } else {
                // Permission not granted: still deliver, just not to the exact minute
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
            )
        }
    }

    fun cancelHabitReminder(context: Context, habitId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HabitReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}
