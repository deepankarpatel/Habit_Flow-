
package com.habitflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.domain.model.Habit
import com.habitflow.app.ui.theme.HabitFlowTheme

private val Green = Color(0xFF315C48)

@Composable
fun HabitCard(
    habit: Habit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F4EF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = habit.emoji,
                    fontSize = 23.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF252B27)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = habit.progressText,
                    fontSize = 12.sp,
                    color = Color(0xFF858B85)
                )
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        if (habit.isCompleted)
                            Green
                        else
                            Color(0xFFF0F1ED)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (habit.isCompleted) "✓" else "",
                    color = Color.White,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardPreview() {
    HabitFlowTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HabitCard(
                habit = Habit(
                    id = 1,
                    name = "Drink Water",
                    emoji = "💧",
                    progressText = "7/8 glasses",
                    isCompleted = false
                ),
                onClick = {}
            )
            HabitCard(
                habit = Habit(
                    id = 2,
                    name = "Morning Meditation",
                    emoji = "🧘",
                    progressText = "10 mins",
                    isCompleted = true
                ),
                onClick = {}
            )
        }
    }
}
