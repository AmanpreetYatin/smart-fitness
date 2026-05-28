package com.smartfitness.app.core.theme


import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color





val PrimaryBlue = Color(0xFF5517BC)
val PrimaryBlueLight = Color(0xFF625b71)

val BrandColor = Color(0xFF5517BC)

val LightPurple = Color(0xFFEDE7F6)   // light lavender
val DarkPurple  = Color(0xFF2E2A4A)   // dark muted purple

// Light background with subtle blue tint
val Background = Color(0xFFF5F7FF)
val ringColor = Color(0xFF00C853)
// Card colors
val CardColor = Color.White
val CardSoft = Color(0xFFEAF0FF)

// Text
val TextPrimary = Color(0xFF0A0A0A)
val TextSecondary = Color(0xFF6B7280)

val LightBlue = Color(0xFF63A4FF)
val DarkBlue = Color(0xFF004BA0)

val BrandGradient = Brush.linearGradient(
    listOf(Color(0xFF113DFA), Color(0xFF5B7CFF))
)

val ringGradient = Brush.sweepGradient(
    listOf(
        Color(0xFF00E5FF), // cyan glow
        Color(0xFF448AFF), // lighter blue
        Color(0xFF00E5FF)
    )
)
val AppBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF8FBFF),  // almost white
        Color(0xFFF1F7FF)   // very light blue tint slightly darker
    )
)






val DarkBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0D1B2A),
        Color(0xFF1B263B)
    )
)