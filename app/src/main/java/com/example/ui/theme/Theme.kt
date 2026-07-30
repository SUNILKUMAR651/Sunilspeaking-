package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FluentDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E676),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00C853),
    secondary = Color(0xFF2979FF),
    onSecondary = Color.White,
    background = Color(0xFF0F1218),
    onBackground = Color.White,
    surface = Color(0xFF1E222D),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2E3B),
    onSurfaceVariant = Color(0xFFB0B3B8)
)

private val FluentLightColorScheme = lightColorScheme(
    primary = Color(0xFF1CB0F6), // Vibrant blue from Duolingo
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF4FF),
    secondary = Color(0xFF58CC02), // Green
    onSecondary = Color.White,
    background = Color(0xFFF7F9FC),
    onBackground = Color(0xFF4B4B4B),
    surface = Color.White,
    onSurface = Color(0xFF4B4B4B),
    surfaceVariant = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFFAFAFAF)
)

@Composable
fun LexiCoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FluentDarkColorScheme else FluentLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
