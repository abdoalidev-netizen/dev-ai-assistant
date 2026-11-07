package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.constants.DevConstants
import com.example.viewmodel.NavTab

/**
 * FloatingBottomNavBar.kt
 * شريط تنقل سفلي عائم ذو طابع زجاجي مستقبلي (Floating Bottom Bar).
 * يحتوي على 5 أيقونات متحركة مع مؤشر نابض للتبويب النشط، ويدعم المساحات الآمنة لشريط النظام.
 */

data class NavItem(
    val tab: NavTab,
    val labelRes: Int,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector,
    val testTag: String
)

@Composable
fun FloatingBottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val items = remember {
        listOf(
            NavItem(NavTab.CHAT, R.string.tab_chat, Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline, "nav_tab_chat"),
            NavItem(NavTab.STUDIO, R.string.tab_studio, Icons.Filled.Image, Icons.Outlined.Image, "nav_tab_studio"),
            NavItem(NavTab.TOOLS, R.string.tab_tools, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_tab_tools"),
            NavItem(NavTab.HISTORY, R.string.tab_history, Icons.Filled.History, Icons.Outlined.History, "nav_tab_history"),
            NavItem(NavTab.SETTINGS, R.string.tab_settings, Icons.Filled.Settings, Icons.Outlined.Settings, "nav_tab_settings")
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating pill container
        val pillShape = RoundedCornerShape(32.dp)
        val bgBrush = if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xF0181434),
                    Color(0xE6100E26)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xF5FFFFFF),
                    Color(0xEEF3EFFF)
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = pillShape,
                    spotColor = Color(0x667C3AED)
                )
                .clip(pillShape)
                .background(brush = bgBrush, shape = pillShape)
                .border(
                    width = 1.2.dp,
                    brush = DevConstants.BorderGlowGradient,
                    shape = pillShape
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = selectedTab == item.tab
                val interactionSource = remember { MutableInteractionSource() }

                val animatedIconColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF7C3AED) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    label = "nav_icon_color"
                )

                val animatedOffsetY by animateDpAsState(
                    targetValue = if (isSelected) (-3).dp else 0.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "nav_offset_y"
                )

                Column(
                    modifier = Modifier
                        .testTag(item.testTag)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(item.tab) }
                        )
                        .offset(y = animatedOffsetY)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Active Pill or Indicator
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0x247C3AED) else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.iconFilled else item.iconOutlined,
                            contentDescription = stringResource(id = item.labelRes),
                            tint = animatedIconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = stringResource(id = item.labelRes),
                        fontSize = 11.sp,
                        color = animatedIconColor,
                        style = MaterialTheme.typography.labelSmall
                    )

                    // Small indicator dot when selected
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(brush = DevConstants.PrimaryGradient)
                        )
                    }
                }
            }
        }
    }
}
