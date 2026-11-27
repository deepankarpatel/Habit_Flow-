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
    background = Color(0xFFFFF8F3),
    card = Color.White,
    text = Color(0xFF2B2220),
    muted = Color(0xFF8A7B75),
    accent = Color(0xFFD9532F),
    secondary = Color(0xFFF4A24C),
    divider = Color(0xFFF3E7E0),
    accentSoft = Color(0xFFFDEBE3),
    track = Color(0xFFF1E6DF),
    danger = Color(0xFFB3261E)
)

val DarkAppColors = AppColors(
    background = Color(0xFF1A1412),
    card = Color(0xFF261E1B),
    text = Color(0xFFF4E9E4),
    muted = Color(0xFFB3A199),
    accent = Color(0xFFD9532F),
    secondary = Color(0xFFF4A24C),
    divider = Color(0xFF372B26),
    accentSoft = Color(0xFF3A2620),
    track = Color(0xFF352A26),
    danger = Color(0xFFEF8A80)
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

object AppTheme {
    val colors: AppColors
        @Composable get() = LocalAppColors.current
}