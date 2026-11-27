package com.habitflow.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD9532F),
    secondary = Color(0xFFF4A24C),
    tertiary = Color(0xFFF4A24C),
    background = Color(0xFF1A1412),
    surface = Color(0xFF261E1B),
    surfaceContainerHigh = Color(0xFF30251F),
    secondaryContainer = Color(0xFF4A2E25),
    onSecondaryContainer = Color(0xFFF8DDD2),
    onPrimary = Color.White,
    onBackground = Color(0xFFF4E9E4),
    onSurface = Color(0xFFF4E9E4)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD9532F),
    secondary = Color(0xFFF4A24C),
    tertiary = Color(0xFFF4A24C),
    background = Color(0xFFFFF8F3),
    surface = Color.White,
    secondaryContainer = Color(0xFFFDEBE3),
    onSecondaryContainer = Color(0xFF2B2220),
    onPrimary = Color.White,
    onBackground = Color(0xFF2B2220),
    onSurface = Color(0xFF2B2220)
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

    // Status/navigation bar icons follow the app's toggle, not the system setting
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalAppColors provides if (darkTheme) DarkAppColors else LightAppColors
    ) {
        MaterialTheme(colorScheme = colorScheme, typography = Typography) {
            // Surface makes unstyled text/icons default to the right colour
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colorScheme.background,
                content = content
            )
        }
    }
}