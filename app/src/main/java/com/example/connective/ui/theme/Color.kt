package com.example.connective.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BluePrimary = Color(0xFF0284C7)      // Bright Royal / Sky Blue
val BlueDark = Color(0xFF0369A1)         // Deep Ocean Blue
val BlueDeepNavy = Color(0xFF0C4A6E)     // Rich Dark Navy Header
val BlueAccent = Color(0xFF38BDF8)       // Vibrant Light Blue Highlight

val LightBlueBg = Color(0xFFE0F2FE)      // Soft Sky-100 Light Blue Background
val LightBlueSoft = Color(0xFFF0F9FF)    // Sky-50 Extra Soft Light Blue
val BackgroundLight = Color(0xFFEBF5FF) // Light Blue Tinted Page Background
val SurfaceLight = Color(0xFFFFFFFF)    // Pure White Card Surface

val TextDark = Color(0xFF0F172A)  // Slate-900 crisp typography
val TextNavy = Color(0xFF0C4A6E)  // Navy typography
val TextGray = Color(0xFF475569)  // Slate-600 secondary typography

// Light Blue to Bright Blue Gradients
val LightBlueHeaderGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0284C7), Color(0xFF0369A1), Color(0xFF0C4A6E))
)

val LightBlueCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
)

val HeroBannerGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0284C7), Color(0xFF0EA5E9), Color(0xFF0369A1))
)