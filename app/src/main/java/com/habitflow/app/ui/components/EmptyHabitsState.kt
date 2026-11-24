package com.habitflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.ui.theme.AppTheme

private val Background: Color @Composable get() = AppTheme.colors.background
private val Green: Color @Composable get() = AppTheme.colors.accent
private val Dark: Color @Composable get() = AppTheme.colors.text
private val Muted: Color @Composable get() = AppTheme.colors.muted
private val Red: Color @Composable get() = AppTheme.colors.danger
@Composable
fun EmptyHabitsState(
    onAddHabit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.accentSoft),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🌱",
                fontSize = 36.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No habits yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Dark
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Start building a better routine, one habit at a time.",
            fontSize = 13.sp,
            color = Muted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAddHabit,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Green
            )
        ) {
            Text(
                text = "+ Add Your First Habit",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
