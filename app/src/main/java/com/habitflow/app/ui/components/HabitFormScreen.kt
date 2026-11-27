package com.habitflow.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.ui.theme.AppTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val emojiOptions = listOf(
    "💧", "📖", "🏃", "🧘", "💪", "🥗", "💊", "🎯", "✍️", "🚶", "😴", "🎵"
)
private val categoryOptions = listOf("Health", "Fitness", "Learning", "Personal", "Productivity")
private val unitOptions = listOf("glasses", "pages", "minutes", "steps", "hours", "times")
private val intervalOptions = listOf(30, 60, 90, 120)
private val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

private enum class ReminderMode(val key: String, val label: String) {
    NONE("NONE", "Off"),
    DAILY("FIXED_TIME", "Daily"),
    REPEAT("INTERVAL", "Repeating")
}

private enum class TimeField { START, END }

// Old data may contain "RANDOM", which was never implemented: treat it as a daily reminder.
private fun modeFrom(type: String?): ReminderMode = when (type) {
    "NONE" -> ReminderMode.NONE
    "INTERVAL" -> ReminderMode.REPEAT
    null -> ReminderMode.DAILY
    else -> ReminderMode.DAILY
}

/**
 * One form used for both "New habit" and "Edit habit".
 * initial == null  -> create mode.
 * onDelete != null -> shows the delete button (edit mode).
 * topContent       -> optional content shown above the form (e.g. streak stats).
 */
@Composable
fun HabitFormScreen(
    title: String,
    initial: Habit?,
    onBack: () -> Unit,
    onSave: (Habit) -> Unit,
    onDelete: (() -> Unit)? = null,
    topContent: @Composable ColumnScope.() -> Unit = {}
) {
    val colors = AppTheme.colors

    var name by remember { mutableStateOf(initial?.name ?: "") }
    var emoji by remember { mutableStateOf(initial?.emoji ?: emojiOptions.first()) }
    var category by remember { mutableStateOf(initial?.category ?: "Health") }
    var target by remember { mutableStateOf((initial?.target ?: 8).toString()) }
    var unit by remember { mutableStateOf(initial?.unit ?: "glasses") }
    var mode by remember { mutableStateOf(modeFrom(initial?.reminderType)) }
    var interval by remember { mutableIntStateOf(initial?.intervalMinutes ?: 60) }
    var startTime by remember { mutableStateOf(initial?.startTime ?: "09:00") }
    var endTime by remember { mutableStateOf(initial?.endTime ?: "21:00") }
    var days by remember { mutableStateOf(initial?.activeDays?.toSet() ?: allDays.toSet()) }
    var error by remember { mutableStateOf("") }
    var showDelete by remember { mutableStateOf(false) }
    var pickingTime by remember { mutableStateOf<TimeField?>(null) }

    fun trySave() {
        val targetValue = target.toIntOrNull() ?: 0
        val problem = when {
            name.isBlank() -> "Please enter a habit name."
            targetValue <= 0 -> "Daily goal must be greater than 0."
            days.isEmpty() -> "Select at least one day."
            mode == ReminderMode.REPEAT && startTime >= endTime -> "'Until' time must be after 'From' time."
            else -> null
        }
        if (problem != null) {
            error = problem
            return
        }
        onSave(
            Habit(
                id = initial?.id ?: 0,
                name = name.trim(),
                emoji = emoji,
                progressText = "0 of $targetValue $unit",
                isCompleted = initial?.isCompleted ?: false,
                category = category,
                target = targetValue,
                unit = unit,
                reminderType = mode.key,
                intervalMinutes = interval,
                startTime = startTime,
                endTime = endTime,
                activeDays = allDays.filter { it in days }
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 22.sp, color = colors.text)
            }
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.text,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { trySave() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp)
            ) {
                Text("Save", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Form
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            topContent()

            AnimatedVisibility(visible = error.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.danger.copy(alpha = 0.12f))
                        .padding(14.dp)
                ) {
                    Text(error, fontSize = 13.sp, color = colors.danger)
                }
            }

            // ---- Habit -------------------------------------------------
            FormCard("Habit") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.accentSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            error = ""
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("e.g. Drink water") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors()
                    )
                }

                FieldLabel("Icon")
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emojiOptions.forEach { option ->
                        val selected = option == emoji
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (selected) colors.accentSoft else colors.track)
                                .border(
                                    width = if (selected) 2.dp else 0.dp,
                                    color = if (selected) colors.accent else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { emoji = option },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(option, fontSize = 20.sp)
                        }
                    }
                }

                FieldLabel("Category")
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryOptions.forEach { option ->
                        Chip(option, option == category) { category = option }
                    }
                }
            }

            // ---- Goal --------------------------------------------------
            FormCard("Daily goal") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = target,
                        onValueChange = {
                            if (it.length <= 5 && it.all { c -> c.isDigit() }) {
                                target = it
                                error = ""
                            }
                        },
                        modifier = Modifier.width(110.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors()
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("$unit per day", fontSize = 14.sp, color = colors.muted)
                }
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    unitOptions.forEach { option ->
                        Chip(option, option == unit) { unit = option }
                    }
                }
            }

            // ---- Reminder ---------------------------------------------
            FormCard("Reminder") {
                Segmented(
                    options = ReminderMode.entries.map { it.label },
                    selectedIndex = mode.ordinal,
                    onSelect = {
                        mode = ReminderMode.entries[it]
                        error = ""
                    }
                )

                AnimatedVisibility(visible = mode == ReminderMode.DAILY) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TimeButton("Remind me at", startTime, Modifier.weight(1f)) {
                            pickingTime = TimeField.START
                        }
                    }
                }

                AnimatedVisibility(visible = mode == ReminderMode.REPEAT) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        FieldLabel("Repeat every")
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            intervalOptions.forEach { minutes ->
                                Chip("$minutes min", minutes == interval) { interval = minutes }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TimeButton("From", startTime, Modifier.weight(1f)) {
                                pickingTime = TimeField.START
                            }
                            TimeButton("Until", endTime, Modifier.weight(1f)) {
                                pickingTime = TimeField.END
                            }
                        }
                    }
                }

                FieldLabel("Active days")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    allDays.forEach { day ->
                        val selected = day in days
                        val bg by animateColorAsState(
                            if (selected) colors.accent else colors.track,
                            label = "day"
                        )
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(bg)
                                .clickable {
                                    days = if (selected) days - day else days + day
                                    error = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.first().toString(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) Color.White else colors.muted
                            )
                        }
                    }
                }
            }

            if (onDelete != null) {
                TextButton(
                    onClick = { showDelete = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Delete habit",
                        color = colors.danger,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showDelete && onDelete != null) {
        DeleteConfirmationDialog(
            habitName = initial?.name ?: "",
            onConfirm = {
                showDelete = false
                onDelete()
            },
            onDismiss = { showDelete = false }
        )
    }

    pickingTime?.let { field ->
        TimePickerDialog(
            initial = if (field == TimeField.START) startTime else endTime,
            onConfirm = { picked ->
                if (field == TimeField.START) startTime = picked else endTime = picked
                pickingTime = null
                error = ""
            },
            onDismiss = { pickingTime = null }
        )
    }
}

// ---------------------------------------------------------------------
// Small building blocks
// ---------------------------------------------------------------------

@Composable
private fun FormCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                color = AppTheme.colors.muted
            )
            content()
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = AppTheme.colors.muted
    )
}

@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = AppTheme.colors
    val bg by animateColorAsState(if (selected) colors.accent else colors.track, label = "chip")
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color.White else colors.text
        )
    }
}

@Composable
private fun Segmented(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.track)
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val bg by animateColorAsState(
                if (selected) colors.card else Color.Transparent,
                label = "segment"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .background(bg)
                    .clickable { onSelect(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (selected) colors.accent else colors.muted
                )
            }
        }
    }
}

@Composable
private fun TimeButton(label: String, value: String, modifier: Modifier, onClick: () -> Unit) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.track)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(label, fontSize = 11.sp, color = colors.muted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = formatTime(value),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.text
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AppTheme.colors.accent,
    unfocusedBorderColor = AppTheme.colors.divider,
    focusedTextColor = AppTheme.colors.text,
    unfocusedTextColor = AppTheme.colors.text,
    cursorColor = AppTheme.colors.accent
)

private fun formatTime(value: String): String = try {
    LocalTime.parse(value).format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))
} catch (_: Exception) {
    value
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initial: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    val start = try {
        LocalTime.parse(initial)
    } catch (_: Exception) {
        LocalTime.of(9, 0)
    }
    val state = rememberTimePickerState(
        initialHour = start.hour,
        initialMinute = start.minute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.card,
        text = {
            TimePicker(
                state = state,
                colors = TimePickerDefaults.colors(
                    clockDialColor = colors.track,
                    selectorColor = colors.accent,
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = colors.text,
                    periodSelectorBorderColor = colors.divider,
                    periodSelectorSelectedContainerColor = colors.accentSoft,
                    periodSelectorUnselectedContainerColor = Color.Transparent,
                    periodSelectorSelectedContentColor = colors.accent,
                    periodSelectorUnselectedContentColor = colors.muted,
                    timeSelectorSelectedContainerColor = colors.accentSoft,
                    timeSelectorUnselectedContainerColor = colors.track,
                    timeSelectorSelectedContentColor = colors.accent,
                    timeSelectorUnselectedContentColor = colors.text
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(String.format(Locale.US, "%02d:%02d", state.hour, state.minute))
                }
            ) {
                Text("OK", color = colors.accent, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.muted)
            }
        }
    )
}