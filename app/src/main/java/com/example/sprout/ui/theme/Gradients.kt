package com.example.sprout.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val SoilGradient = Brush.linearGradient(
    colors = listOf(SproutSoil, SproutSoilLight),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
)

val CanopyGradient = Brush.linearGradient(
    colors = listOf(SproutGreen, SproutGreenLight),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, 0f),
)

val DewGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)),
)
