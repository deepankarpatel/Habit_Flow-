package com.habitflow.app

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitflow.app.data.local.database.HabitDatabase
import com.habitflow.app.data.repository.BackupRestoreManager
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.data.repository.UserPreferencesRepository
import com.habitflow.app.domain.model.HabitItemUiState
import com.habitflow.app.notification.NotificationHelper
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

        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(applicationContext)

        // Initialize Room database
        val database = HabitDatabase.getDatabase(applicationContext)

        // Create repositories & managers
        val repository = HabitRepository(
            habitDao = database.habitDao(),
            completionDao = database.habitCompletionDao()
        )
        val userPreferencesRepository = UserPreferencesRepository(applicationContext)
        val backupRestoreManager = BackupRestoreManager(database)

        // Create ViewModel factory
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return HabitViewModel(
                        repository = repository,
                        userPreferencesRepository = userPreferencesRepository,
                        backupRestoreManager = backupRestoreManager
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            // Get ViewModel
            val habitViewModel: HabitViewModel = viewModel(factory = factory)

            val isDarkTheme by habitViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val notificationsEnabled by habitViewModel.notificationsEnabled.collectAsStateWithLifecycle()

            HabitFlowTheme(darkTheme = isDarkTheme) {

                val context = LocalContext.current

                // Request Notification Permission on Android 13+ (API 33+)
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ -> }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // Observe habit items with daily progress from Room
                val habitItems by habitViewModel.habitItems.collectAsStateWithLifecycle()
                val selectedDate by habitViewModel.selectedDate.collectAsStateWithLifecycle()
                val completedDates by habitViewModel.completedDates.collectAsStateWithLifecycle()

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
                                habitViewModel.addHabit(context, newHabit)
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
                                habitViewModel.updateHabit(context, updatedHabit)
                                selectedHabitForDetail = null
                            },
                            onDelete = { habitToDelete ->
                                habitViewModel.deleteHabit(context, habitToDelete)
                                selectedHabitForDetail = null
                            }
                        )
                    }

                    selectedTab == 0 -> {
                        HabitHomeScreen(
                            habitItems = habitItems,
                            selectedDate = selectedDate,
                            onDateSelected = { date ->
                                habitViewModel.setSelectedDate(date)
                            },
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
                            completedDates = completedDates,
                            onDateSelected = { date ->
                                habitViewModel.setSelectedDate(date)
                                selectedTab = 0
                            },
                            bottomBar = bottomBar
                        )
                    }

                    selectedTab == 3 -> {
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onDarkThemeChange = { enabled ->
                                habitViewModel.setDarkTheme(enabled)
                            },
                            notificationsEnabled = notificationsEnabled,
                            onNotificationsChange = { enabled ->
                                habitViewModel.setNotificationsEnabled(enabled)
                            },
                            onExportBackup = {
                                habitViewModel.exportBackup { json ->
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("HabitFlow Backup JSON", json)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Backup copied to clipboard!", Toast.LENGTH_LONG).show()
                                }
                            },
                            onImportBackup = { json ->
                                habitViewModel.importBackup(json) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Data restored successfully!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to parse backup JSON.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onResetData = {
                                habitViewModel.resetAllData()
                                Toast.makeText(context, "All app data reset.", Toast.LENGTH_SHORT).show()
                            },
                            bottomBar = bottomBar
                        )
                    }
                }
            }
        }
    }
}
