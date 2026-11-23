package com.habitflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.domain.model.HabitItemUiState
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
    private val repository: HabitRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

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

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
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

    // Add a habit to the database
    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    // Update a habit definition
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    // Delete a habit
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }
}
