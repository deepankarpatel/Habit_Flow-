
package com.habitflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.ui.screens.AddHabitScreen
import com.habitflow.app.ui.screens.HabitHomeScreen
import com.habitflow.app.ui.theme.HabitFlowTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            HabitFlowTheme {

                // Store habits in Compose state
                val habits = remember {
                    mutableStateListOf(
                        Habit(
                            1,
                            "Drink Water",
                            "💧",
                            "5 of 8 glasses"
                        ),
                        Habit(
                            2,
                            "Read a Book",
                            "📖",
                            "3 of 3 pages"
                        ),
                        Habit(
                            3,
                            "Exercise",
                            "🏃",
                            "Daily target"
                        ),
                        Habit(
                            4,
                            "Meditation",
                            "🧘",
                            "10 minutes"
                        )
                    )
                }

                // Screen navigation state
                var showAddHabit by remember {
                    mutableStateOf(false)
                }

                if (showAddHabit) {

                    AddHabitScreen(

                        onBack = {
                            showAddHabit = false
                        },

                        onSave = { newHabit ->

                            habits.add(newHabit)

                            showAddHabit = false
                        }
                    )

                } else {

                    HabitHomeScreen(
                        habits = habits,
                        onAddHabit = {
                            showAddHabit = true
                        }
                    )
                }
            }
        }
    }
}
