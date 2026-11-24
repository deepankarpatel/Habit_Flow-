package com.habitflow.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.repository.BackupRestoreManager
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.data.repository.UserPreferencesRepository
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.domain.model.HabitItemUiState
import com.habitflow.app.notification.ReminderScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModel(
    private val repository: HabitRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val backupRestoreManager: BackupRestoreManager
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val isDarkTheme: StateFlow<Boolean> = userPreferencesRepository.isDarkTheme
    val notificationsEnabled: StateFlow<Boolean> = userPreferencesRepository.notificationsEnabled

    // Observe habits with daily progress for the selected date
    val habitItems: StateFlow<List<HabitItemUiState>> = _selectedDate
        .flatMapLatest { date ->
            repository.getHabitsForDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Observe all completed dates across history
    val completedDates: StateFlow<Set<LocalDate>> = repository.getAllCompletedDates()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setDarkTheme(enabled: Boolean) {
        userPreferencesRepository.setDarkTheme(enabled)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        userPreferencesRepository.setNotificationsEnabled(enabled)
    }

    // Increment progress for a habit
    fun incrementProgress(habitId: Int, target: Int) {
        viewModelScope.launch {
            repository.incrementProgress(habitId, _selectedDate.value, target)
        }
    }

    // Decrement progress for a habit
    fun decrementProgress(habitId: Int, target: Int) {
        viewModelScope.launch {
            repository.decrementProgress(habitId, _selectedDate.value, target)
        }
    }

    // Toggle completion status for a habit
    fun toggleCompletion(habitId: Int, target: Int) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, _selectedDate.value, target)
        }
    }

    // Add a habit to the database and schedule its reminder
    fun addHabit(context: Context, habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
            ReminderScheduler.scheduleHabitReminder(context, habit)
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    // Update a habit definition and reschedule reminder
    fun updateHabit(context: Context, habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
            ReminderScheduler.scheduleHabitReminder(context, habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    // Delete a habit and cancel reminder
    fun deleteHabit(context: Context, habit: Habit) {
        viewModelScope.launch {
            ReminderScheduler.cancelHabitReminder(context, habit.id)
            repository.deleteHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = backupRestoreManager.exportBackupJson()
            onResult(json)
        }
    }

    fun importBackup(jsonString: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = backupRestoreManager.importBackupJson(jsonString)
            onResult(success)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            backupRestoreManager.resetAllData()
        }
    }
}
