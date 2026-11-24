package com.habitflow.app.domain.usecase

import java.time.LocalDate

object StreakCalculator {

    // Calculates consecutive days completed up to today (or yesterday if today is not completed yet)
    fun calculateCurrentStreak(
        completedDates: Set<LocalDate>,
        today: LocalDate = LocalDate.now()
    ): Int {
        if (completedDates.isEmpty()) return 0

        var checkDate = today
        // If today is not completed, check if yesterday was completed to keep the active streak open
        if (!completedDates.contains(today)) {
            checkDate = today.minusDays(1)
        }

        var streak = 0
        while (completedDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }

        return streak
    }

    // Calculates the longest consecutive streak in history
    fun calculateBestStreak(completedDates: Set<LocalDate>): Int {
        if (completedDates.isEmpty()) return 0

        val sortedDates = completedDates.sorted()
        var maxStreak = 0
        var currentRun = 0
        var previousDate: LocalDate? = null

        for (date in sortedDates) {
            if (previousDate == null || date == previousDate.plusDays(1)) {
                currentRun++
            } else if (date != previousDate) {
                currentRun = 1
            }
            previousDate = date
            if (currentRun > maxStreak) {
                maxStreak = currentRun
            }
        }

        return maxStreak
    }
}
