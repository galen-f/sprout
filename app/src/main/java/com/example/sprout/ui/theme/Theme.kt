package com.example.sprout.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SproutGreen,
    onPrimary = Color.White,
    primaryContainer = SproutDew,
    onPrimaryContainer = SproutGreenDark,
    secondary = SproutSoil,
    onSecondary = Color.White,
    secondaryContainer = SproutCreamDark,
    onSecondaryContainer = SproutSoil,
    tertiary = SproutSage,
    background = SproutCream,
    onBackground = SproutGreenDark,
    surface = SproutCream,
    onSurface = SproutGreenDark,
    surfaceVariant = SproutCreamDark,
    outline = SproutSage,
)

private val DarkColorScheme = darkColorScheme(
    primary = SproutGreenDk,
    onPrimary = SproutBackgroundDk,
    primaryContainer = SproutGreenDark,
    onPrimaryContainer = SproutDew,
    secondary = SproutSoilLight,
    onSecondary = SproutBackgroundDk,
    background = SproutBackgroundDk,
    onBackground = SproutOnSurfaceDk,
    surface = SproutSurfaceDk,
    onSurface = SproutOnSurfaceDk,
)

@Composable
fun SproutTheme(
    themeMode: String = "system",
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = SproutShapes,
        content = content,
    )
}
