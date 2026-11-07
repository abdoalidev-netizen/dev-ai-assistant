package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

/**
 * Theme.kt
 * نظام التصميم المتكامل (Material 3) لتطبيق Dev.
 * يحدد لوحات الألوان للوضع الداكن والفاتح، والأشكال المنحنية الفخمة (Shapes)، والطباعة.
 */

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = DarkOnBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkOnBackground,
    secondary = PinkAccent,
    onSecondary = DarkOnBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkOnBackground,
    tertiary = CyanAccent,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurface,
    error = ErrorRed,
    onError = DarkOnBackground,
    outline = GlassBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = LightSurface,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightOnBackground,
    secondary = PinkAccent,
    onSecondary = LightSurface,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightOnBackground,
    tertiary = CyanDark,
    onTertiary = LightSurface,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface,
    error = ErrorRed,
    onError = LightSurface,
    outline = GlassBorderLight
)

val DevShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun DevTheme(
    darkTheme: Boolean = true,
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getDevTypography(fontScale),
        shapes = DevShapes,
        content = content
    )
}
