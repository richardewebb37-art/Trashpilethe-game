package com.trashapp.ui.theme

import androidx.compose.ui.graphics.Color

// Wild West Theme Colors
val WildWestColors = lightColorScheme(
    primary = Color(0xFFD2691E),        // Sienna (leather)
    onPrimary = Color(0xFFF5DEB3),      // Wheat (text on leather)
    primaryContainer = Color(0xFF8B4513), // Saddle brown
    onPrimaryContainer = Color(0xFFF5DEB3),
    
    secondary = Color(0xFFB8860B),      // Dark goldenrod
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFFFD700), // Gold
    onSecondaryContainer = Color(0xFF000000),
    
    tertiary = Color(0xFF654321),       // Dark brown
    
    background = Color(0xFF3E2723),     // Dark wood
    onBackground = Color(0xFFF5DEB3),   // Cream text
    
    surface = Color(0xFF5D4037),        // Medium wood
    onSurface = Color(0xFFFAF0E6),      // Linen
    
    error = Color(0xFFD32F2F),          // Red
    onError = Color(0xFFFFFFFF),
    
    outline = Color(0xFF8D6E63),        // Light brown outline
)

object AppColors {
    // Wild West specific colors
    val SepiaDark = Color(0xFF8B7355)
    val SepiaLight = Color(0xFFD2B48C)
    val Gold = Color(0xFFFFD700)
    val Brass = Color(0xFFB5A642)
    val AgedWood = Color(0xFF3E2723)
    val MediumWood = Color(0xFF5D4037)
    val Parchment = Color(0xFFF5DEB3)
    val DarkRed = Color(0xFF8B0000)
    
    // Card colors
    val CardBack = DarkRed
    val CardFace = Parchment
    val CardBorder = Color(0xFF654321)
    
    // UI elements
    val ButtonPrimary = Color(0xFF8B4513)
    val ButtonSecondary = Color(0xFFD2691E)
    val TextOnButton = Color(0xFFF5DEB3)
}