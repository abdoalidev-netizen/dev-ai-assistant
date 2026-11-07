package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Type.kt
 * سلم الطباعة والنصوص لتطبيق Dev يدعم الخطوط العربية والإنجليزية بأناقة وسلاسة في القراءة.
 */
fun getDevTypography(fontScaleMultiplier: Float = 1.0f): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = (32 * fontScaleMultiplier).sp,
            lineHeight = (40 * fontScaleMultiplier).sp,
            letterSpacing = 0.sp
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = (26 * fontScaleMultiplier).sp,
            lineHeight = (34 * fontScaleMultiplier).sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (22 * fontScaleMultiplier).sp,
            lineHeight = (28 * fontScaleMultiplier).sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20 * fontScaleMultiplier).sp,
            lineHeight = (26 * fontScaleMultiplier).sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = (18 * fontScaleMultiplier).sp,
            lineHeight = (24 * fontScaleMultiplier).sp,
            letterSpacing = 0.15.sp
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = (16 * fontScaleMultiplier).sp,
            lineHeight = (22 * fontScaleMultiplier).sp,
            letterSpacing = 0.15.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (15 * fontScaleMultiplier).sp,
            lineHeight = (23 * fontScaleMultiplier).sp,
            letterSpacing = 0.25.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * fontScaleMultiplier).sp,
            lineHeight = (20 * fontScaleMultiplier).sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * fontScaleMultiplier).sp,
            lineHeight = (18 * fontScaleMultiplier).sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (13 * fontScaleMultiplier).sp,
            lineHeight = (18 * fontScaleMultiplier).sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * fontScaleMultiplier).sp,
            lineHeight = (16 * fontScaleMultiplier).sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = (10 * fontScaleMultiplier).sp,
            lineHeight = (14 * fontScaleMultiplier).sp,
            letterSpacing = 0.5.sp
        )
    )
}

val Typography = getDevTypography(1.0f)
