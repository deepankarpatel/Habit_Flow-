
package com.habitflow.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
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
import com.habitflow.app.ui.theme.HabitFlowTheme

private val Green = Color(0xFF315C48)
private val Dark = Color(0xFF252B27)
private val Muted = Color(0xFF858B85)
private val Background = Color(0xFFF7F6F1)

@Composable
fun AddHabitScreen(
    onBack: () -> Unit,
    onSave: (Habit) -> Unit
) {

    var habitName by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("💧") }
    var selectedCategory by remember { mutableStateOf("Health") }

    var target by remember { mutableStateOf("8") }
    var unit by remember { mutableStateOf("glasses") }

    var reminderType by remember {
        mutableStateOf("INTERVAL")
    }

    var interval by remember { mutableIntStateOf(60) }

    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("21:00") }

    var selectedDays by remember {
        mutableStateOf(
            setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        )
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

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
            text = "Create a habit",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Dark
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Build a better routine, one step at a time.",
            fontSize = 14.sp,
            color = Muted
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Habit name

        SectionTitle("Habit name")

        OutlinedTextField(
            value = habitName,
            onValueChange = {
                habitName = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("e.g. Drink Water")
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Emoji selection

        SectionTitle("Choose an icon")

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            emojis.forEach { emoji ->

                FilterChip(
                    selected = selectedEmoji == emoji,
                    onClick = { selectedEmoji = emoji },
                    label = {
                        Text(
                            text = emoji,
                            fontSize = 20.sp
                        )
                    }
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
                            onClick = {
                                selectedCategory = category
                            },
                            label = {
                                Text(category)
                            }
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
                target = it.filter { char ->
                    char.isDigit()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Enter target")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            units.forEach { item ->

                FilterChip(
                    selected = unit == item,
                    onClick = { unit = item },
                    label = {
                        Text(item)
                    }
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
                onClick = {
                    reminderType = option.first
                },
                label = {
                    Text(option.second)
                }
            )
        }

        // Interval selection

        if (reminderType == "INTERVAL") {

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Remind me every")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(30, 60, 90, 120).forEach { minutes ->

                    FilterChip(
                        selected = interval == minutes,
                        onClick = {
                            interval = minutes
                        },
                        label = {
                            Text("$minutes min")
                        }
                    )
                }
            }
        }

        // Reminder time window

        if (reminderType != "NONE") {

            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle(
                if (reminderType == "FIXED_TIME")
                    "Reminder time"
                else
                    "Reminder time window"
            )

            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        if (reminderType == "FIXED_TIME")
                            "Time (HH:mm)"
                        else
                            "Start time (HH:mm)"
                    )
                },
                placeholder = {
                    Text("09:00")
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            if (reminderType != "FIXED_TIME") {

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("End time (HH:mm)")
                    },
                    placeholder = {
                        Text("21:00")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Active days

        SectionTitle("Repeat on")

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            days.forEach { day ->

                FilterChip(
                    selected = day in selectedDays,
                    onClick = {
                        selectedDays =
                            if (day in selectedDays) {
                                selectedDays - day
                            } else {
                                selectedDays + day
                            }
                    },
                    label = {
                        Text(day.take(2))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Validation message

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 13.sp
            )
        }

        // Save button

        Button(
            onClick = {

                val validStart = Regex(
                    "^([01]\\d|2[0-3]):[0-5]\\d$"
                ).matches(startTime)

                val validEnd = Regex(
                    "^([01]\\d|2[0-3]):[0-5]\\d$"
                ).matches(endTime)

                when {
                    habitName.isBlank() -> {
                        errorMessage = "Please enter a habit name."
                    }

                    target.toIntOrNull() == null ||
                            target.toInt() <= 0 -> {
                        errorMessage = "Please enter a valid target."
                    }

                    selectedDays.isEmpty() -> {
                        errorMessage = "Select at least one day."
                    }

                    reminderType != "NONE" && !validStart -> {
                        errorMessage = "Enter a valid start time (HH:mm)."
                    }

                    reminderType != "FIXED_TIME" &&
                            reminderType != "NONE" &&
                            !validEnd -> {
                        errorMessage = "Enter a valid end time (HH:mm)."
                    }

                    reminderType != "FIXED_TIME" &&
                            reminderType != "NONE" &&
                            startTime >= endTime -> {
                        errorMessage = "End time must be after start time."
                    }

                    else -> {

                        val newHabit = Habit(
                            id = 0,
                            name = habitName.trim(),
                            emoji = selectedEmoji,
                            progressText = "0 of ${target} $unit",
                            category = selectedCategory,
                            target = target.toInt(),
                            unit = unit,
                            reminderType = reminderType,
                            intervalMinutes = interval,
                            startTime = startTime,
                            endTime = endTime,
                            activeDays = selectedDays.toList()
                        )

                        onSave(newHabit)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green
            )
        ) {
            Text(
                text = "Save Habit",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
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
fun AddHabitScreenPreview() {
    HabitFlowTheme {
        AddHabitScreen(
            onBack = {},
            onSave = {}
        )
    }
}
