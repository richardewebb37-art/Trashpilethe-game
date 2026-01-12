package com.trashapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val WildWestTypography = Typography(
    // Large titles (game title, big announcements)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 57.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp
    ),
    
    // Medium titles (screen headers)
    displayMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 45.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp
    ),
    
    // Section headers
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold
    ),
    
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold
    ),
    
    // Subtitles
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium
    ),
    
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    ),
    
    // Body text
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    ),
    
    // Labels (buttons, tags)
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    ),
    
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
    )
)