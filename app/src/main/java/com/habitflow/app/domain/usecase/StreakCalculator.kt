package com.habitflow.app.domain.usecase

import java.time.DayOfWeek
import java.time.LocalDate

object StreakCalculator {

    private val ALL_DAYS: Set<DayOfWeek> = DayOfWeek.entries.toSet()

    // Consecutive scheduled days completed, ending today (or the last scheduled day
    // if today isn't done yet). Days outside activeDays neither add to nor break the streak.
    fun calculateCurrentStreak(
        completedDates: Set<LocalDate>,
        today: LocalDate = LocalDate.now(),
        activeDays: Set<DayOfWeek> = ALL_DAYS
    ): Int {
        if (completedDates.isEmpty() || activeDays.isEmpty()) return 0

        var date = today
        if (date !in completedDates) date = date.minusDays(1)

        var streak = 0
        while (true) {
            if (date.dayOfWeek !in activeDays && date !in completedDates) {
                date = date.minusDays(1)   // rest day: skip
                continue
            }
            if (date !in completedDates) break
            streak++
            date = date.minusDays(1)
        }
        return streak
    }

    // Longest run of consecutive scheduled days completed.
    fun calculateBestStreak(
        completedDates: Set<LocalDate>,
        activeDays: Set<DayOfWeek> = ALL_DAYS
    ): Int {
        if (completedDates.isEmpty() || activeDays.isEmpty()) return 0

        var best = 0
        var run = 0
        for (date in completedDates.sorted()) {
            // Find the previous scheduled day before this one
            var prev = date.minusDays(1)
            var guard = 0
            while (prev.dayOfWeek !in activeDays && guard < 7) {
                prev = prev.minusDays(1)
                guard++
            }
            run = if (prev in completedDates) run + 1 else 1
            if (run > best) best = run
        }
        return best
    }
}