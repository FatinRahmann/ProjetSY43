package com.example.projetsy43.ui.theme

import android.app.Activity
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

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,        // Black for buttons
    onPrimary = White,            // Text on primary
    background = White,             // Screen background
    surface = LightGray,          // Cards/dialogs
    onBackground = Black,        // Default text
    onSurface = Black,           // Text on surfaces
)

private val DarkColorScheme = darkColorScheme(
    primary = White,        // for button
    onPrimary = Black,            // Text on primary
    background = DarkGray,              // Screen background
    surface = Black,                    // Cards/dialogs
    onBackground = White,       // Default text
    onSurface = LightGrayText,          // Text on surfaces
)


@Composable
fun ProjetSY43Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,  // Your typography
        content = content
    )
}