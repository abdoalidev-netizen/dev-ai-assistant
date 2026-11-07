package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constants.DevConstants
import com.example.ui.components.AuroraBackground
import com.example.ui.components.DevAvatar
import kotlinx.coroutines.delay

/**
 * SplashScreen.kt
 * شاشة البداية المتحركة والفخمة لتطبيق Dev:
 * يظهر الشعار بتوهج مضيء ثم يتحول لاسم "Dev" بتدرج لوني، وتحته "by Abdo Ahmed Ali (عبده أحمد علي)"،
 * وتستمر لمدة ثانيتين تقريباً لتوفير تجربة انطلاق سلسة.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.4f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        scale.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        delay(1300)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0A1A))
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        AuroraBackground(isDark = true)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .padding(24.dp)
        ) {
            // Glowing Dev Avatar
            DevAvatar(size = 96.dp, isThinking = true)

            Spacer(modifier = Modifier.height(24.dp))

            // App Name "Dev" with Brand Gradient
            Text(
                text = DevConstants.APP_NAME,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                style = androidx.compose.ui.text.TextStyle(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF7C3AED), Color(0xFFEC4899), Color(0xFFF97316))
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Developer Attribution (Abdo Ahmed Ali)
            Text(
                text = "by ${DevConstants.DEVELOPER_NAME_EN}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF22D3EE),
                letterSpacing = 0.5.sp
            )

            Text(
                text = DevConstants.DEVELOPER_NAME_AR,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tagline
            Text(
                text = "مساعد ذكي شامل وفخم مدعوم بنماذج Gemini",
                fontSize = 12.sp,
                color = Color(0xFFA78BFA).copy(alpha = 0.8f)
            )
        }
    }
}
