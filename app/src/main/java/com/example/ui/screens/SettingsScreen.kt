package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.viewmodel.DevViewModel

/**
 * SettingsScreen.kt
 * شاشة الإعدادات والتفضيلات:
 * نموذج الذكاء الاصطناعي، المظهر، اللغة، حجم الخط، سرعة TTS، الاهتزاز، الذاكرة،
 * ومربع حوار "عن التطبيق" المبرز لهوية المطوّر Abdo Ahmed Ali.
 */
@Composable
fun SettingsScreen(
    viewModel: DevViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs by viewModel.preferences.collectAsState()
    val showAboutDialog by viewModel.showAboutDialog.collectAsState()
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var apiKeyInput by remember { mutableStateOf(prefs.customApiKey) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen")
    ) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.settings_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 0. User Profile & Account (Dynamic Google User)
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(DevConstants.PrimaryGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (prefs.userName.takeIf { it.isNotBlank() } ?: "U").take(1).uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = prefs.userName.ifBlank { "مستخدم Dev" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = prefs.userEmail.ifBlank { "حساب Google متصل" },
                                        fontSize = 11.sp,
                                        color = Color(0xFF22D3EE)
                                    )
                                }
                            }

                            // Sign-Out Button
                            TextButton(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تسجيل خروج", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 1. AI Model Selection
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, null, tint = Color(0xFFFDE047), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(id = R.string.setting_model), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ModelCardOption(
                                    title = "Gemini Flash",
                                    subtitle = "فائق السرعة وخفيف",
                                    isSelected = prefs.selectedModel == DevConstants.MODEL_FLASH,
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.updateModel(DevConstants.MODEL_FLASH) }

                                ModelCardOption(
                                    title = "Gemini Pro",
                                    subtitle = "تفكير عميق وتفصيل",
                                    isSelected = prefs.selectedModel == DevConstants.MODEL_PRO,
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.updateModel(DevConstants.MODEL_PRO) }
                            }
                        }
                    }
                }

                // 2. Theme & Appearance
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DarkMode, null, tint = Color(0xFFA78BFA), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(id = R.string.setting_theme), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SmallPill(
                                    label = "داكن (Luxe)",
                                    isSelected = prefs.themeMode == "dark",
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.updateTheme("dark") }
                                SmallPill(
                                    label = "فاتح (Clean)",
                                    isSelected = prefs.themeMode == "light",
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.updateTheme("light") }
                            }
                        }
                    }
                }

                // 3. Language & RTL
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(id = R.string.setting_language), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SmallPill(
                                    label = "العربية (RTL)",
                                    isSelected = prefs.language == "ar",
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.updateLanguage("ar") }
                                SmallPill(
                                    label = "English (LTR)",
                                    isSelected = prefs.language == "en",
                                    modifier = Modifier.weight(1f)
                                ) { viewModel.updateLanguage("en") }
                            }
                        }
                    }
                }

                // 4. TTS Voice Speed
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.RecordVoiceOver, null, tint = Color(0xFFEC4899), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(id = R.string.setting_tts_speed), fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Text("${prefs.ttsSpeed}x", color = Color(0xFFEC4899), fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = prefs.ttsSpeed,
                                onValueChange = { viewModel.updateTtsSpeed(it) },
                                valueRange = 0.7f..1.5f,
                                steps = 7,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFEC4899),
                                    activeTrackColor = Color(0xFFEC4899)
                                )
                            )
                        }
                    }
                }

                // 5. Long-term Memory
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleMemoryManager(true) }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Memory, null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(stringResource(id = R.string.setting_memory), fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("عرض وتعديل الحقائق والتفضيلات المحفوظة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                            Switch(
                                checked = prefs.longTermMemoryEnabled,
                                onCheckedChange = { viewModel.updateMemoryEnabled(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                            )
                        }
                    }
                }

                // 6. Custom API Key
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    apiKeyInput = prefs.customApiKey
                                    showApiKeyDialog = true
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("مفتاح Gemini API مخصص", fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(if (prefs.customApiKey.isNotBlank()) "تم ضبط مفتاح مخصص" else "يتم استخدام المفتاح المدمج", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }
                }

                // 7. About App & Developer Info
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleAboutDialog(true) }
                            .testTag("settings_about_button"),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(id = R.string.about_app), fontWeight = FontWeight.Bold, color = Color.White)
                                Text("المطور: ${DevConstants.DEVELOPER_FULL}", fontSize = 11.sp, color = Color(0xFF22D3EE))
                            }
                        }
                    }
                }

                // Developer Signature at Bottom
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = DevConstants.DEVELOPER_FULL,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA78BFA)
                        )
                        Text(
                            text = "الإصدار ${DevConstants.APP_VERSION} • فخر الصناعة البرمجية",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(72.dp))
        }

        // About Dev Dialog
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleAboutDialog(false) },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DevAvatar(size = 32.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.about_dialog_title), color = Color.White)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(id = R.string.about_developer_bio),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                        Text(
                            text = stringResource(id = R.string.tech_disclosure),
                            fontSize = 12.sp,
                            color = Color(0xFF22D3EE),
                            lineHeight = 18.sp
                        )
                        Text(
                            text = stringResource(id = R.string.privacy_note),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.toggleAboutDialog(false) }) {
                        Text("حسناً")
                    }
                }
            )
        }

        // API Key Dialog
        if (showApiKeyDialog) {
            AlertDialog(
                onDismissRequest = { showApiKeyDialog = false },
                title = { Text("إعداد مفتاح Gemini API") },
                text = {
                    Column {
                        Text("يمكنك إدخال مفتاح Gemini الخاص بك لتجاوز أي قيود:", fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            placeholder = { Text("AIzaSy…") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.updateCustomApiKey(apiKeyInput.trim())
                            showApiKeyDialog = false
                        }
                    ) {
                        Text("حفظ")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showApiKeyDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}

@Composable
fun ModelCardOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0xFF7C3AED) else Color(0x221F1C42))
            .border(
                1.dp,
                if (isSelected) Color(0xFFA78BFA) else Color(0x33A78BFA),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
            Text(subtitle, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun SmallPill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF7C3AED) else Color(0x221F1C42))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
        )
    }
}
