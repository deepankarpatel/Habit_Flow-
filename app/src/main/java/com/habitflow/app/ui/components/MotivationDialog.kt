package com.habitflow.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.habitflow.app.ui.theme.AppTheme

val motivationQuotes = listOf(
    "Small steps every day add up to big changes.",
    "You don't have to be perfect, just consistent.",
    "One good day at a time is how streaks are built.",
    "It always seems impossible until it's done.",
    "Lose?? I don't lose! I win that's my job that what i fukin' do...",
    "You are born to control storms, stop acting like rain hurts you...",
    "And just because you are a dream. i sleep a lot...✨",
    "Your future self is counting on what you do today.",
    "When the why is clear, the how is easy...",
    "Next time when you think of something beautiful don't forget to count yourself too✨",
    "Lagta hai mujhe aapse loan lena padega, tab jake apka interest mujh pe badhega",
    "Don't break the chain. Even a small win counts.",
    "The magic you are looking for is in the work you're avoiding",

)

@Composable
fun MotivationDialog(
    quote: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.card,
        title = {
            Text(
                text = "Today's Deep thought✨",
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.text
            )
        },
        text = {
            Text(
                text = quote,
                fontSize = 16.sp,
                color = AppTheme.colors.muted
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.accent)
            ) {
                Text("Let's go Bun✨")
            }
        }
    )
}