package com.habitflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitflow.app.data.local.database.HabitDatabase
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.domain.model.HabitItemUiState
import com.habitflow.app.ui.screens.AddHabitScreen
import com.habitflow.app.ui.screens.HabitDetailScreen
import com.habitflow.app.ui.screens.HabitHomeScreen
import com.habitflow.app.ui.screens.HabitsListScreen
import com.habitflow.app.ui.screens.ReportsScreen
import com.habitflow.app.ui.screens.SettingsScreen
import com.habitflow.app.ui.theme.HabitFlowTheme
import com.habitflow.app.ui.viewmodel.HabitViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room database
        val database = HabitDatabase.getDatabase(applicationContext)

        // Create repository with habit and completion DAOs
        val repository = HabitRepository(
            habitDao = database.habitDao(),
            completionDao = database.habitCompletionDao()
        )

        // Create ViewModel factory
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return HabitViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            HabitFlowTheme {

                // Get ViewModel
                val habitViewModel: HabitViewModel = viewModel(
                    factory = factory
                )

                // Observe habit items with daily progress from Room
                val habitItems by habitViewModel.habitItems.collectAsStateWithLifecycle()

                // Navigation state
                var selectedTab by remember { mutableIntStateOf(0) }
                var showAddHabit by remember { mutableStateOf(false) }
                var selectedHabitForDetail by remember { mutableStateOf<HabitItemUiState?>(null) }

                val bottomBar: @Composable () -> Unit = {
                    NavigationBar(containerColor = Color.White) {
                        val tabs = listOf(
                            "Home" to "⌂",
                            "Habits" to "✓",
                            "Reports" to "▥",
                            "Settings" to "⚙"
                        )

                        tabs.forEachIndexed { index, tab ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = { Text(text = tab.second, fontSize = 20.sp) },
                                label = { Text(tab.first) }
                            )
                        }
                    }
                }

                when {
                    showAddHabit -> {
                        AddHabitScreen(
                            onBack = {
                                showAddHabit = false
                            },
                            onSave = { newHabit ->
                                habitViewModel.addHabit(newHabit)
                                showAddHabit = false
                            }
                        )
                    }

                    selectedHabitForDetail != null -> {
                        HabitDetailScreen(
                            habitItem = selectedHabitForDetail!!,
                            onBack = {
                                selectedHabitForDetail = null
                            },
                            onSave = { updatedHabit ->
                                habitViewModel.updateHabit(updatedHabit)
                                selectedHabitForDetail = null
                            },
                            onDelete = { habitToDelete ->
                                habitViewModel.deleteHabit(habitToDelete)
                                selectedHabitForDetail = null
                            }
                        )
                    }

                    selectedTab == 0 -> {
                        HabitHomeScreen(
                            habitItems = habitItems,
                            onAddHabit = {
                                showAddHabit = true
                            },
                            onIncrement = { habitId, target ->
                                habitViewModel.incrementProgress(habitId, target)
                            },
                            onDecrement = { habitId, target ->
                                habitViewModel.decrementProgress(habitId, target)
                            },
                            onToggleCompletion = { habitId, target ->
                                habitViewModel.toggleCompletion(habitId, target)
                            },
                            onHabitClick = { clickedItem ->
                                selectedHabitForDetail = clickedItem
                            },
                            bottomBar = bottomBar
                        )
                    }

                    selectedTab == 1 -> {
                        HabitsListScreen(
                            habitItems = habitItems,
                            onAddHabit = {
                                showAddHabit = true
                            },
                            onIncrement = { habitId, target ->
                                habitViewModel.incrementProgress(habitId, target)
                            },
                            onDecrement = { habitId, target ->
                                habitViewModel.decrementProgress(habitId, target)
                            },
                            onToggleCompletion = { habitId, target ->
                                habitViewModel.toggleCompletion(habitId, target)
                            },
                            onHabitClick = { clickedItem ->
                                selectedHabitForDetail = clickedItem
                            },
                            bottomBar = bottomBar
                        )
                    }

                    selectedTab == 2 -> {
                        ReportsScreen(
                            habitItems = habitItems,
                            bottomBar = bottomBar
                        )
                    }

                    selectedTab == 3 -> {
                        SettingsScreen(
                            bottomBar = bottomBar
                        )
                    }
                }
            }
        }
    }
}
