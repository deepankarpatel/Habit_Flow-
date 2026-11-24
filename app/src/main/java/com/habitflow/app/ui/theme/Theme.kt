package com.habitflow.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val HabitGreen = Color(0xFF315C48)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF82B39A),
    secondary = Color(0xFF689F84),
    tertiary = Color(0xFFA2C7B3),
    background = Color(0xFF181C19),
    surface = Color(0xFF222723),
    onPrimary = Color.Black,
    onBackground = Color(0xFFE2E3DF),
    onSurface = Color(0xFFE2E3DF)
)

private val LightColorScheme = lightColorScheme(
    primary = HabitGreen,
    secondary = Color(0xFF689F84),
    tertiary = Color(0xFF858B85),
    background = Color(0xFFF7F6F1),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF252B27),
    onSurface = Color(0xFF252B27)
)

@Composable
fun HabitFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
