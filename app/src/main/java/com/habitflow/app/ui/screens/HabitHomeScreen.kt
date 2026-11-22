
package com.habitflow.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.ui.components.HabitCard
import androidx.compose.ui.tooling.preview.Preview
import com.habitflow.app.ui.theme.HabitFlowTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf


private val Background = Color(0xFFF7F6F1)
private val Green = Color(0xFF315C48)
private val Dark = Color(0xFF252B27)
private val Muted = Color(0xFF858B85)

@Composable
fun HabitHomeScreen(
    habits: SnapshotStateList<Habit>,
    onAddHabit: () -> Unit
) {

    val completedCount = habits.count { it.isCompleted }
    val totalHabits = habits.size

    val progress = if (totalHabits > 0) {
        completedCount.toFloat() / totalHabits
    } else {
        0f
    }

    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    val today = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE, dd MMMM")
    )

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
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
                        icon = {
                            Text(
                                text = tab.second,
                                fontSize = 20.sp
                            )
                        },
                        label = {
                            Text(tab.first)
                        }
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(top = 25.dp, bottom = 20.dp)
        ) {

            Text(
                text = "HabitFlow",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Green
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "$greeting 👋",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = today,
                fontSize = 14.sp,
                color = Muted
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "TODAY'S PROGRESS",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Muted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your daily goals",
                            fontWeight = FontWeight.SemiBold,
                            color = Dark
                        )

                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontWeight = FontWeight.Bold,
                            color = Green
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE9EDE9))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress)
                                .clip(CircleShape)
                                .background(Green)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$completedCount of $totalHabits habits completed",
                        fontSize = 12.sp,
                        color = Muted
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onAddHabit
                ) {
                    Text(
                        text = "+ Add",
                        color = Green,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "$totalHabits habits",
                    fontSize = 13.sp,
                    color = Green
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            habits.forEachIndexed { index, habit ->

                HabitCard(
                    habit = habit,
                    onClick = {
                        habits[index] = habit.copy(
                            isCompleted = !habit.isCompleted
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HabitHomeScreenPreview() {

    val sampleHabits = remember {
        mutableStateListOf(
            Habit(1, "Drink Water", "💧", "5 of 8 glasses"),
            Habit(2, "Read a Book", "📖", "3 of 3 pages"),
            Habit(3, "Exercise", "🏃", "Daily target"),
            Habit(4, "Meditation", "🧘", "10 minutes")
        )
    }

    HabitFlowTheme {
        HabitHomeScreen(
            habits = sampleHabits,
            onAddHabit = {}
        )
    }
}
