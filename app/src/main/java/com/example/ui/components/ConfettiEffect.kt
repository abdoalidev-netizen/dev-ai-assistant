package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * ConfettiEffect.kt
 * تأثير احتفالي ناعم وجذاب يظهر عند إنجاز المهام أو تصدير المحادثات أو توليد الصور بنجاح.
 */

private data class ConfettiParticle(
    val x: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ConfettiEffect(
    trigger: Int,
    modifier: Modifier = Modifier
) {
    if (trigger <= 0) return

    val progress = remember(trigger) { Animatable(0f) }
    val particles = remember(trigger) {
        val colors = listOf(
            Color(0xFF7C3AED),
            Color(0xFFEC4899),
            Color(0xFFF97316),
            Color(0xFF22D3EE),
            Color(0xFFFDE047),
            Color(0xFF10B981)
        )
        List(45) {
            ConfettiParticle(
                x = Random.nextFloat(),
                initialY = Random.nextFloat() * 0.2f,
                speed = 0.8f + Random.nextFloat() * 0.5f,
                size = 7f + Random.nextFloat() * 8f,
                color = colors[Random.nextInt(colors.size)]
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            particles.forEach { p ->
                val currentY = (p.initialY + progress.value * p.speed) * h
                val alpha = (1f - progress.value).coerceIn(0f, 1f)
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size,
                    center = Offset(p.x * w, currentY)
                )
            }
        }
    }
}
