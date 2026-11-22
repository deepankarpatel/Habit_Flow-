
package com.habitflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.habitflow.app.ui.screens.HabitHomeScreen
import com.habitflow.app.ui.theme.HabitFlowTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HabitFlowTheme {
                HabitHomeScreen()
            }
        }
    }
}
