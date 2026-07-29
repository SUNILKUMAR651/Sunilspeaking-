package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FluentDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E676), // Vibrant Neon Green
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00C853),
    secondary = Color(0xFF2979FF), // Bright Blue
    onSecondary = Color.White,
    background = Color(0xFF0F1218), // Deep dark fluent background
    onBackground = Color.White,
    surface = Color(0xFF1E222D), // Slightly lighter for cards
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2E3B), // Higher contrast for accents
    onSurfaceVariant = Color(0xFFB0B3B8)
)

@Composable
fun LexiCoreTheme(
    darkTheme: Boolean = true, // Force dark mode for the Fluent look
    content: @Composable () -> Unit
) {
    val colorScheme = FluentDarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
