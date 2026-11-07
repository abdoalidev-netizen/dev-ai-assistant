package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * AuroraBackground.kt
 * خلفية الشفق المتحركة (Aurora Effect) خفيفة وناعمة تعطي عمقاً وفخامة لشاشات التطبيق
 * بدون استهلاك مفرط للمعالج أو البطارية.
 */
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora_transition")

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aurora_progress"
    )

    val baseBgColor = if (isDark) Color(0xFF0B0A1A) else Color(0xFFF7F5FF)
    val colorOrb1 = if (isDark) Color(0x387C3AED) else Color(0x227C3AED) // Purple
    val colorOrb2 = if (isDark) Color(0x28EC4899) else Color(0x18EC4899) // Pink
    val colorOrb3 = if (isDark) Color(0x2406B6D4) else Color(0x1406B6D4) // Cyan

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Base solid background
        drawRect(color = baseBgColor)

        // Orb 1: Upper right moving gently
        val orb1Center = Offset(
            x = width * (0.8f - 0.2f * animProgress),
            y = height * (0.15f + 0.1f * animProgress)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colorOrb1, Color.Transparent),
                center = orb1Center,
                radius = width * 0.7f
            ),
            center = orb1Center,
            radius = width * 0.7f
        )

        // Orb 2: Lower left floating
        val orb2Center = Offset(
            x = width * (0.15f + 0.25f * animProgress),
            y = height * (0.75f - 0.15f * animProgress)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colorOrb2, Color.Transparent),
                center = orb2Center,
                radius = width * 0.75f
            ),
            center = orb2Center,
            radius = width * 0.75f
        )

        // Orb 3: Center ambient light
        val orb3Center = Offset(
            x = width * (0.5f + 0.15f * (0.5f - animProgress)),
            y = height * (0.45f + 0.1f * animProgress)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colorOrb3, Color.Transparent),
                center = orb3Center,
                radius = width * 0.6f
            ),
            center = orb3Center,
            radius = width * 0.6f
        )
    }
}
