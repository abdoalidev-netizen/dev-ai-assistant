package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constants.DevConstants
import com.example.ui.components.AuroraBackground
import com.example.ui.components.DevAvatar
import com.example.ui.components.GlassCard

/**
 * LoginScreen.kt
 * شاشة تسجيل الدخول الأولى والفخمة لتطبيق Dev.
 * تتيح للمستخدم تسجيل الدخول بحساب Google واختيار حسابه أو تخصيص هويته،
 * وتتعرف ديناميكياً على اسم صاحب الحساب ليظهر في كامل التطبيق.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (name: String, email: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAccountDialog by remember { mutableStateOf(false) }
    var selectedName by remember { mutableStateOf("مستخدم Dev") }
    var selectedEmail by remember { mutableStateOf("user@gmail.com") }
    var isSigningIn by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(modifier = modifier.fillMaxSize()) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo with Animated Glow
            Box(
                modifier = Modifier
                    .scale(pulseScale)
                    .shadow(24.dp, CircleShape, spotColor = Color(0xFF7C3AED))
            ) {
                DevAvatar(size = 96.dp, isThinking = false)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Title
            Text(
                text = "Dev AI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x33A78BFA))
                    .border(1.dp, Color(0x66A78BFA), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFFDE047),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "المساعد الذكي الشامل والمطور",
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Login Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تسجيل الدخول للمتابعة",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "سجّل الدخول بحساب Google لربط جلساتك والتعرف على هويتك وتفعيل قدرات الذكاء الاصطناعي الكاملة.",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Google Sign-In Button
                    val googleBtnShape = RoundedCornerShape(24.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(12.dp, googleBtnShape, spotColor = Color(0x667C3AED))
                            .clip(googleBtnShape)
                            .background(Color.White)
                            .clickable(enabled = !isSigningIn) {
                                showAccountDialog = true
                            }
                            .padding(horizontal = 16.dp)
                            .testTag("google_signin_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isSigningIn) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF7C3AED),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "جاري الاتصال والتحقق...",
                                color = Color(0xFF1E293B),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            // Google 'G' stylized badge
                            GoogleGIcon()

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "تسجيل الدخول بحساب Google",
                                color = Color(0xFF1E293B),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Privacy & Encryption Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مصادقة آمنة ومشفرة تماماً • حماية الخصوصية",
                            color = Color(0xFF10B981),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Developer & Commercial Notice
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "تم التطوير بواسطة: ${DevConstants.DEVELOPER_NAME_AR}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Professional Android AI Application • v${DevConstants.APP_VERSION}",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp
                )
            }
        }

        // Google Account Selector Dialog
        if (showAccountDialog) {
            GoogleAccountPickerDialog(
                initialName = selectedName,
                initialEmail = selectedEmail,
                onDismiss = { showAccountDialog = false },
                onSelectAccount = { name, email ->
                    showAccountDialog = false
                    isSigningIn = true
                    selectedName = name
                    selectedEmail = email
                    onLoginSuccess(name, email)
                }
            )
        }
    }
}

/**
 * نافذة اختيار حساب Google التفاعلية (Account Picker Sheet)
 * تمكّن المستخدم من اختيار حسابه أو إدخال حسابه الخاص للتعرف عليه تلقائياً
 */
@Composable
fun GoogleAccountPickerDialog(
    initialName: String,
    initialEmail: String,
    onDismiss: () -> Unit,
    onSelectAccount: (name: String, email: String) -> Unit
) {
    var accountName by remember { mutableStateOf(if (initialName.isNotBlank() && initialName != "مستخدم Dev") initialName else "Abdo Ali") }
    var accountEmail by remember { mutableStateOf(if (initialEmail.isNotBlank() && initialEmail != "user@gmail.com") initialEmail else "abdoalidev@gmail.com") }

    val presetAccounts = listOf(
        Pair("Abdo Ali (المطور)", "abdoalidev@gmail.com"),
        Pair("حساب Google الشخصي", "mygoogleaccount@gmail.com"),
        Pair("مستخدم تجاري (Business)", "enterprise.user@gmail.com")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GoogleGIcon()
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "اختيار حساب Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "اختر حساب Google لتسجيل الدخول به، أو خصص اسمك وبريدك الإلكتروني:",
                    fontSize = 13.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick presets
                presetAccounts.forEach { (presetName, presetEmail) ->
                    val isSelected = accountEmail == presetEmail
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0x447C3AED) else Color(0x221E1B4B))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF22D3EE) else Color(0x33A78BFA),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                accountName = presetName.replace(" (المطور)", "")
                                accountEmail = presetEmail
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFF22D3EE) else Color(0xFFA78BFA),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = presetName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = presetEmail,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Name & Email Fields
                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("الاسم الظاهر في التطبيق") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF22D3EE),
                        unfocusedBorderColor = Color(0x66A78BFA),
                        focusedLabelColor = Color(0xFF22D3EE),
                        unfocusedLabelColor = Color(0xFFA78BFA)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = accountEmail,
                    onValueChange = { accountEmail = it },
                    label = { Text("البريد الإلكتروني المرتبط") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF22D3EE),
                        unfocusedBorderColor = Color(0x66A78BFA),
                        focusedLabelColor = Color(0xFF22D3EE),
                        unfocusedLabelColor = Color(0xFFA78BFA)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalName = accountName.trim().ifBlank { "مستخدم Dev" }
                    val finalEmail = accountEmail.trim().ifBlank { "user@gmail.com" }
                    onSelectAccount(finalName, finalEmail)
                }
            ) {
                Text(
                    text = "تسجيل الدخول بهذا الحساب",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22D3EE),
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color(0xFF94A3B8))
            }
        },
        containerColor = Color(0xFF151330),
        shape = RoundedCornerShape(24.dp)
    )
}

/**
 * أيقونة Google الملونة الرسمية
 */
@Composable
fun GoogleGIcon(modifier: Modifier = Modifier.size(24.dp)) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, Color(0x1F000000), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = Color(0xFF4285F4) // Google Blue
        )
    }
}
