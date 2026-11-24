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
    primary = Color(0xFF4E8B6E),
    secondary = Color(0xFF689F84),
    tertiary = Color(0xFFA2C7B3),
    background = Color(0xFF181C19),
    surface = Color(0xFF222723),
    surfaceContainerHigh = Color(0xFF2A302B),   // dialogs
    secondaryContainer = Color(0xFF2F4A3C),     // nav bar selection pill
    onSecondaryContainer = Color(0xFFD7EBDF),
    onPrimary = Color.White,
    onBackground = Color(0xFFE2E3DF),
    onSurface = Color(0xFFE2E3DF)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF315C48),
    secondary = Color(0xFF689F84),
    tertiary = Color(0xFF858B85),
    background = Color(0xFFF7F6F1),
    surface = Color.White,
    secondaryContainer = Color(0xFFE8F0EC),
    onSecondaryContainer = Color(0xFF252B27),
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