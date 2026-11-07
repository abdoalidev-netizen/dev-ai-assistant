package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * GlassCard.kt
 * مكوّن بطاقة زجاجية فاخرة (Glassmorphic Card).
 * يدمج الشفافية الدقيقة والحدود البراقة المنسابة بتدرج ناعم والظلال الأنيقة.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    borderBrush: Brush = Brush.linearGradient(
        colors = listOf(
            Color(0x60A78BFA),
            Color(0x20EC4899),
            Color(0x4006B6D4)
        )
    ),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 6.dp,
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable BoxScope.() -> Unit
) {
    val fillBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xCC181534),
                Color(0x99110F28)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xF0FFFFFF),
                Color(0xCCF7F5FF)
            )
        )
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = if (isDark) Color(0x667C3AED) else Color(0x227C3AED)
            )
            .clip(shape)
            .background(brush = fillBrush, shape = shape)
            .border(width = borderWidth, brush = borderBrush, shape = shape)
    ) {
        content()
    }
}
