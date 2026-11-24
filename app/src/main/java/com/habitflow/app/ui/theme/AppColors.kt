package com.habitflow.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val background: Color,   // screen background
    val card: Color,         // cards, dialogs, bars
    val text: Color,         // primary text
    val muted: Color,        // secondary text
    val accent: Color,       // brand green
    val secondary: Color,    // lighter green (in-progress)
    val divider: Color,
    val accentSoft: Color,   // pale green tint
    val track: Color,        // progress track / chips
    val danger: Color
)

val LightAppColors = AppColors(
    background = Color(0xFFF7F6F1),
    card = Color.White,
    text = Color(0xFF252B27),
    muted = Color(0xFF858B85),
    accent = Color(0xFF315C48),
    secondary = Color(0xFF689F84),
    divider = Color(0xFFF0F1ED),
    accentSoft = Color(0xFFE8F0EC),
    track = Color(0xFFE9EDE9),
    danger = Color(0xFFD32F2F)
)

val DarkAppColors = AppColors(
    background = Color(0xFF181C19),
    card = Color(0xFF222723),
    text = Color(0xFFE2E3DF),
    muted = Color(0xFF9AA39C),
    accent = Color(0xFF4E8B6E),
    secondary = Color(0xFF7DB398),
    divider = Color(0xFF2E352F),
    accentSoft = Color(0xFF263A30),
    track = Color(0xFF2B322C),
    danger = Color(0xFFE57373)
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

object AppTheme {
    val colors: AppColors
        @Composable get() = LocalAppColors.current
}