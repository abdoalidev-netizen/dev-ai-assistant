package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constants.DevConstants

/**
 * DevAvatar.kt
 * الشعار الرمزي الفخم لـ Dev: الرمز </> مع شرارة الذكاء الاصطناعي ✨ وحلقات ضوئية نابضة.
 */
@Composable
fun DevAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isThinking: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isThinking) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(if (isThinking) pulseScale else 1.0f),
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing ring
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = if (isThinking) 12.dp else 4.dp,
                    shape = CircleShape,
                    spotColor = Color(0xFF7C3AED)
                )
                .border(
                    width = if (isThinking) 2.dp else 1.5.dp,
                    brush = DevConstants.PrimaryGradient,
                    shape = CircleShape
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF261C52), Color(0xFF0F0E26))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Code Brackets </>
            Text(
                text = "</>",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = (size.value * 0.38f).sp,
                letterSpacing = (-1).sp
            )

            // Sparkle badge ✨ top-right
            Text(
                text = "✨",
                fontSize = (size.value * 0.28f).sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
            )
        }
    }
}
