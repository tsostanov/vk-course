package com.example.vkcourse.applist.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal val CatalogBlue = Color(0xFF1764E8)

private val LightColors = lightColorScheme(
    primary = CatalogBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EFFF),
    onPrimaryContainer = Color(0xFF2455A4),
    background = Color(0xFFF3F5F9),
    onBackground = Color(0xFF18243B),
    surface = Color.White,
    onSurface = Color(0xFF18243B),
    onSurfaceVariant = Color(0xFF626E80),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAFC8FF),
    primaryContainer = Color(0xFF223B62),
    onPrimaryContainer = Color(0xFFC6D8FF),
    background = Color(0xFF101722),
    onBackground = Color(0xFFF0F3FA),
    surface = Color(0xFF1C2635),
    onSurface = Color(0xFFF0F3FA),
    onSurfaceVariant = Color(0xFFB8C2D3),
)

@Composable
fun AppCatalogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
