package com.habitflow.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.domain.model.HabitItemUiState
import com.habitflow.app.ui.components.DeleteConfirmationDialog
import com.habitflow.app.ui.theme.HabitFlowTheme
import com.habitflow.app.domain.usecase.StreakCalculator
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import com.habitflow.app.ui.theme.AppTheme

private val Background: Color @Composable get() = AppTheme.colors.background
private val Green: Color @Composable get() = AppTheme.colors.accent
private val Dark: Color @Composable get() = AppTheme.colors.text
private val Muted: Color @Composable get() = AppTheme.colors.muted
private val Red: Color @Composable get() = AppTheme.colors.danger

@Composable
fun HabitDetailScreen(
    habitItem: HabitItemUiState,
    onBack: () -> Unit,
    onSave: (Habit) -> Unit,
    onDelete: (Habit) -> Unit,
    completedDates: Set<LocalDate> = emptySet()
) {
    val habit = habitItem.habit

    val scheduledDays = habit.activeDays.mapNotNull { name ->
        DayOfWeek.entries.firstOrNull { it.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) == name }
    }.toSet()
    val currentStreak = StreakCalculator.calculateCurrentStreak(completedDates, activeDays = scheduledDays)
    val bestStreak = StreakCalculator.calculateBestStreak(completedDates, activeDays = scheduledDays)

    var habitName by remember { mutableStateOf(habit.name) }
    var selectedEmoji by remember { mutableStateOf(habit.emoji) }
    var selectedCategory by remember { mutableStateOf(habit.category) }

    var target by remember { mutableStateOf(habit.target.toString()) }
    var unit by remember { mutableStateOf(habit.unit) }

    var reminderType by remember { mutableStateOf(habit.reminderType) }
    var interval by remember { mutableIntStateOf(habit.intervalMinutes) }

    var startTime by remember { mutableStateOf(habit.startTime) }
    var endTime by remember { mutableStateOf(habit.endTime) }

    var selectedDays by remember { mutableStateOf(habit.activeDays.toSet()) }

    var errorMessage by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val emojis = listOf(
        "💧", "📖", "🏃", "🧘",
        "💪", "🥗", "💊", "🎯",
        "✍️", "🚶", "😴", "🎵"
    )

    val categories = listOf(
        "Health", "Fitness", "Learning",
        "Personal", "Productivity"
    )

    val days = listOf(
        "Mon", "Tue", "Wed", "Thu",
        "Fri", "Sat", "Sun"
    )

    val units = listOf(
        "glasses", "pages", "minutes",
        "steps", "hours", "times"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 25.dp, bottom = 30.dp)
    ) {

        // Header
        TextButton(onClick = onBack) {
            Text("← Back", color = Green)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Habit Details",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Dark
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Edit configuration or delete this habit.",
            fontSize = 14.sp,
            color = Muted
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Current streak", fontSize = 12.sp, color = Muted)
                    Text("🔥 $currentStreak days", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Dark)
                }
                Column {
                    Text("Best streak", fontSize = 12.sp, color = Muted)
                    Text("🏆 $bestStreak days", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Dark)
                }
                Column {
                    Text("Total", fontSize = 12.sp, color = Muted)
                    Text("${completedDates.size} days", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Dark)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Habit name
        SectionTitle("Habit name")

        OutlinedTextField(
            value = habitName,
            onValueChange = {
                habitName = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g. Drink Water") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Emoji selection
        SectionTitle("Choose an icon")

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            emojis.forEach { emoji ->
                FilterChip(
                    selected = selectedEmoji == emoji,
                    onClick = { selectedEmoji = emoji },
                    label = { Text(text = emoji, fontSize = 20.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Category
        SectionTitle("Category")

        Column {
            categories.chunked(3).forEach { categoryRow ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryRow.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Daily target
        SectionTitle("Daily target")

        OutlinedTextField(
            value = target,
            onValueChange = {
                target = it.filter { char -> char.isDigit() }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter target") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            units.forEach { item ->
                FilterChip(
                    selected = unit == item,
                    onClick = { unit = item },
                    label = { Text(item) }
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Reminder type
        SectionTitle("Reminder type")

        val reminderOptions = listOf(
            "INTERVAL" to "Interval",
            "RANDOM" to "Random",
            "FIXED_TIME" to "Fixed time",
            "NONE" to "No reminder"
        )

        reminderOptions.forEach { option ->
            FilterChip(
                selected = reminderType == option.first,
                onClick = { reminderType = option.first },
                label = { Text(option.second) }
            )
        }

        if (reminderType == "INTERVAL") {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Remind me every")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(30, 60, 90, 120).forEach { minutes ->
                    FilterChip(
                        selected = interval == minutes,
                        onClick = { interval = minutes },
                        label = { Text("$minutes min") }
                    )
                }
            }
        }

        if (reminderType != "NONE") {
            Spacer(modifier = Modifier.height(18.dp))
            SectionTitle(
                if (reminderType == "FIXED_TIME") "Reminder time" else "Reminder time window"
            )

            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        if (reminderType == "FIXED_TIME") "Time (HH:mm)" else "Start time (HH:mm)"
                    )
                },
                placeholder = { Text("09:00") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            if (reminderType != "FIXED_TIME") {
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("End time (HH:mm)") },
                    placeholder = { Text("21:00") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Repeat on active days
        SectionTitle("Repeat on")

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            days.forEach { day ->
                FilterChip(
                    selected = day in selectedDays,
                    onClick = {
                        selectedDays = if (day in selectedDays) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                    },
                    label = { Text(day.take(2)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Save Changes Button
        Button(
            onClick = {
                val validStart = Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(startTime)
                val validEnd = Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(endTime)

                when {
                    habitName.isBlank() -> {
                        errorMessage = "Please enter a habit name."
                    }
                    target.toIntOrNull() == null || target.toInt() <= 0 -> {
                        errorMessage = "Please enter a valid target."
                    }
                    selectedDays.isEmpty() -> {
                        errorMessage = "Select at least one day."
                    }
                    reminderType != "NONE" && !validStart -> {
                        errorMessage = "Enter a valid start time (HH:mm)."
                    }
                    reminderType != "FIXED_TIME" && reminderType != "NONE" && !validEnd -> {
                        errorMessage = "Enter a valid end time (HH:mm)."
                    }
                    reminderType != "FIXED_TIME" && reminderType != "NONE" && startTime >= endTime -> {
                        errorMessage = "End time must be after start time."
                    }
                    else -> {
                        val targetInt = target.toInt()
                        val updatedHabit = habit.copy(
                            name = habitName.trim(),
                            emoji = selectedEmoji,
                            category = selectedCategory,
                            target = targetInt,
                            unit = unit,
                            reminderType = reminderType,
                            intervalMinutes = interval,
                            startTime = startTime,
                            endTime = endTime,
                            activeDays = selectedDays.toList()
                        )
                        onSave(updatedHabit)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green)
        ) {
            Text(
                text = "Save Changes",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Delete Habit Button
        OutlinedButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Red)
        ) {
            Text(
                text = "Delete Habit",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Red
            )
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            habitName = habit.name,
            onConfirm = {
                showDeleteDialog = false
                onDelete(habit)
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = Dark
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Preview(showBackground = true)
@Composable
fun HabitDetailScreenPreview() {
    HabitFlowTheme {
        HabitDetailScreen(
            habitItem = HabitItemUiState(
                habit = Habit(1, "Drink Water", "💧", "5 of 8 glasses", target = 8, unit = "glasses"),
                date = "2026-03-09",
                completedCount = 5,
                target = 8,
                isCompleted = false
            ),
            onBack = {},
            onSave = {},
            onDelete = {}
        )
    }
}
