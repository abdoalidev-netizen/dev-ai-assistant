package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.DeviceActionDialog
import com.example.ui.components.FloatingBottomNavBar
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemoryManagerScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.DevTheme
import com.example.viewmodel.DevViewModel
import com.example.viewmodel.NavTab

/**
 * MainActivity.kt
 * نقطة انطلاق التطبيق الرئيسية (Main Entry Point).
 * تدير التوجيه بين الشاشات (Splash -> Login -> Main App)، والتصميم المتجاوب،
 * والاتجاه اللغوي (RTL للعربية و LTR للإنجليزية)، وأزرار الرجوع، وتأكيد أوامر النظام.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: DevViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val prefs by viewModel.preferences.collectAsState()
            val showSplash by viewModel.showSplash.collectAsState()
            val currentTab by viewModel.currentTab.collectAsState()
            val activeToolId by viewModel.activeToolId.collectAsState()
            val showMemoryManager by viewModel.showMemoryManager.collectAsState()
            val pendingDeviceAction by viewModel.pendingDeviceAction.collectAsState()
            val confettiTrigger by viewModel.confettiTrigger.collectAsState()

            // Direction: RTL for Arabic, LTR for English
            val isRtl = prefs.language != "en"
            val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            // Theme calculation: dark, light, or follow system
            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (prefs.themeMode) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                DevTheme(
                    darkTheme = isDarkTheme,
                    fontScale = prefs.fontScale
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            // 1. Splash Screen
                            showSplash -> {
                                SplashScreen(onSplashFinished = { viewModel.hideSplash() })
                            }

                            // 2. Google Login Screen (First screen for authentication)
                            !prefs.isLoggedIn -> {
                                LoginScreen(
                                    onLoginSuccess = { name, email ->
                                        viewModel.loginWithGoogle(name, email)
                                    }
                                )
                            }

                            // 3. Main App UI with Floating Navigation Bar
                            else -> {
                                MainAppContent(
                                    viewModel = viewModel,
                                    currentTab = currentTab,
                                    activeToolId = activeToolId,
                                    showMemoryManager = showMemoryManager
                                )
                            }
                        }

                        // Device Action Confirmation Dialog
                        pendingDeviceAction?.let { request ->
                            DeviceActionDialog(
                                request = request,
                                onConfirm = { viewModel.confirmDeviceAction(request) },
                                onDismiss = { viewModel.cancelDeviceAction() }
                            )
                        }

                        // Confetti Celebration Effect
                        ConfettiEffect(trigger = confettiTrigger)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: DevViewModel,
    currentTab: NavTab,
    activeToolId: String?,
    showMemoryManager: Boolean
) {
    // Back Handler: return to Chat tab when in sub-screens or other tabs
    BackHandler(enabled = activeToolId != null || showMemoryManager || currentTab != NavTab.CHAT) {
        when {
            activeToolId != null -> viewModel.closeTool()
            showMemoryManager -> viewModel.toggleMemoryManager(false)
            currentTab != NavTab.CHAT -> viewModel.setTab(NavTab.CHAT)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom bar only when not in full modal tool or memory screens
            if (activeToolId == null && !showMemoryManager) {
                FloatingBottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { viewModel.setTab(it) }
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_transition",
            modifier = Modifier.fillMaxSize()
        ) { targetTab ->
            when (targetTab) {
                NavTab.CHAT -> ChatScreen(viewModel = viewModel)
                NavTab.STUDIO -> StudioScreen(viewModel = viewModel)
                NavTab.TOOLS -> ToolsScreen(viewModel = viewModel)
                NavTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                NavTab.SETTINGS -> {
                    if (showMemoryManager) {
                        MemoryManagerScreen(viewModel = viewModel)
                    } else {
                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
