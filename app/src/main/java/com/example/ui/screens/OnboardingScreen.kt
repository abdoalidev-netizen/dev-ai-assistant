package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.constants.DevConstants
import com.example.ui.components.AuroraBackground
import com.example.ui.components.DevAvatar
import com.example.ui.components.GlassCard
import kotlinx.coroutines.launch

/**
 * OnboardingScreen.kt
 * شاشة الترحيب التفاعلية المكونة من 3 شرائح لشرح قدرات Dev، يليها إدخال الاسم والتفضيلات
 * وطلب الصلاحيات برقي ووضوح مع إمكانية المتابعة حتى عند الرفض.
 */

data class OnboardingPage(
    val titleRes: Int,
    val descRes: Int,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(
    onComplete: (name: String, language: String, theme: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })

    var userName by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("ar") }
    var selectedTheme by remember { mutableStateOf("dark") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Continue regardless of grant/deny result as instructed
        onComplete(userName, selectedLang, selectedTheme)
    }

    val pages = remember {
        listOf(
            OnboardingPage(
                R.string.onboarding_title_1,
                R.string.onboarding_desc_1,
                Icons.Default.ChatBubble,
                Color(0xFF7C3AED)
            ),
            OnboardingPage(
                R.string.onboarding_title_2,
                R.string.onboarding_desc_2,
                Icons.Default.AutoAwesome,
                Color(0xFFEC4899)
            ),
            OnboardingPage(
                R.string.onboarding_title_3,
                R.string.onboarding_desc_3,
                Icons.Default.Security,
                Color(0xFF22D3EE)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0A1A))
            .testTag("onboarding_screen")
    ) {
        AuroraBackground(isDark = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Dev brand & Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DevAvatar(size = 36.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = DevConstants.APP_NAME,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                if (pagerState.currentPage < 3) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(3) }
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.onboarding_skip),
                            color = Color(0xFFA78BFA),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                if (pageIndex < 3) {
                    // Informational Slide
                    val page = pages[pageIndex]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .shadow(20.dp, CircleShape, spotColor = page.accentColor)
                                .clip(CircleShape)
                                .background(Color(0x331F1C42))
                                .border(2.dp, page.accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = page.icon,
                                contentDescription = null,
                                tint = page.accentColor,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        Text(
                            text = stringResource(id = page.titleRes),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = stringResource(id = page.descRes),
                            fontSize = 15.sp,
                            color = Color(0xFFD1D5DB),
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                } else {
                    // Preferences & Permissions Slide
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "تخصيص تجربتك",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // User Name Input
                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            placeholder = { Text(stringResource(id = R.string.user_name_placeholder), fontSize = 14.sp) },
                            label = { Text(stringResource(id = R.string.user_name_prompt)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_name_input"),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF7C3AED),
                                unfocusedBorderColor = Color(0x66A78BFA)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Language Toggle Row
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                PillChoice(
                                    label = "العربية (RTL)",
                                    isSelected = selectedLang == "ar",
                                    onClick = { selectedLang = "ar" }
                                )
                                PillChoice(
                                    label = "English (LTR)",
                                    isSelected = selectedLang == "en",
                                    onClick = { selectedLang = "en" }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Permissions Disclosure Card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "صلاحيات اختيارية للسرعة والراحة:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF22D3EE)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Mic, null, tint = Color(0xFFA78BFA), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("الميكروفون للإملاء الصوتي", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CameraAlt, null, tint = Color(0xFFEC4899), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("الكاميرا لقراءة المستندات وOCR", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Controls (Dots indicator and Next/Start button)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    repeat(4) { idx ->
                        val isSelected = pagerState.currentPage == idx
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 22.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isSelected) Color(0xFF7C3AED) else Color.White.copy(alpha = 0.25f)
                                )
                        )
                    }
                }

                // Primary Button
                val buttonShape = RoundedCornerShape(24.dp)
                Button(
                    onClick = {
                        if (pagerState.currentPage < 3) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // Request permissions or complete
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.CAMERA
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("onboarding_primary_button"),
                    shape = buttonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = DevConstants.PrimaryGradient, shape = buttonShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (pagerState.currentPage < 3) {
                                stringResource(id = R.string.onboarding_next)
                            } else {
                                stringResource(id = R.string.onboarding_start)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PillChoice(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFF7C3AED) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
        )
    }
}
