package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.constants.DevConstants
import com.example.data.remote.toBase64Jpeg
import java.io.InputStream
import java.util.Locale

/**
 * ChatInputField.kt
 * خانة الإدخال الذكية الفاخرة لتطبيق Dev.
 * تدعم الكتابة، الإملاء الصوتي بالعربية والإنجليزية، إرفاق الصور من المعرض أو الكاميرا، وزر الإيقاف التدفقي.
 */
@Composable
fun ChatInputField(
    text: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    isStreaming: Boolean,
    attachedImageBase64: String?,
    onImageAttached: (String?) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val context = LocalContext.current
    var showAttachMenu by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onImageAttached(bitmap.toBase64Jpeg())
        }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    onImageAttached(bitmap.toBase64Jpeg())
                }
            } catch (_: Exception) {}
        }
    }

    // Voice dictation launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                val newText = if (text.isBlank()) spoken else "$text $spoken"
                onTextChanged(newText)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Thumbnail preview if an image is attached
        if (attachedImageBase64 != null) {
            Row(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33A78BFA))
                    .border(1.dp, Color(0x66A78BFA), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color(0xFF22D3EE),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "صورة مرفقة للتحليل والأسئلة",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .clickable { onImageAttached(null) }
                )
            }
        }

        // Main input container
        val inputShape = RoundedCornerShape(28.dp)
        val containerBrush = if (isDark) {
            Brush.verticalGradient(
                colors = listOf(Color(0xF01A1738), Color(0xE613112A))
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFF7F5FF))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 10.dp, shape = inputShape, spotColor = Color(0x447C3AED))
                .clip(inputShape)
                .background(brush = containerBrush, shape = inputShape)
                .border(
                    width = 1.2.dp,
                    brush = DevConstants.BorderGlowGradient,
                    shape = inputShape
                )
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment Button (+)
            Box {
                IconButton(
                    onClick = { showAttachMenu = true },
                    modifier = Modifier.testTag("chat_attach_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Attach",
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = showAttachMenu,
                    onDismissRequest = { showAttachMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("معرض الصور", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Image, null, tint = Color(0xFFEC4899)) },
                        onClick = {
                            showAttachMenu = false
                            galleryLauncher.launch("image/*")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("التقاط صورة بالكاميرا", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.CameraAlt, null, tint = Color(0xFF22D3EE)) },
                        onClick = {
                            showAttachMenu = false
                            cameraLauncher.launch(null)
                        }
                    )
                }
            }

            // Text Input Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.chat_input_placeholder),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                        fontSize = 15.sp
                    )
                }

                BasicTextField(
                    value = text,
                    onValueChange = onTextChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chat_text_input"),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    ),
                    cursorBrush = SolidColor(Color(0xFF22D3EE)),
                    maxLines = 5
                )
            }

            // Voice Dictation Button (Microphone)
            if (text.isBlank() && !isStreaming) {
                IconButton(
                    onClick = {
                        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث الآن مع Dev...")
                        }
                        try {
                            speechLauncher.launch(speechIntent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier.testTag("chat_voice_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Dictation",
                        tint = Color(0xFF22D3EE),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Action Button: Send OR Stop
            if (isStreaming) {
                // Stop Generation Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                        .clickable { onStop() }
                        .testTag("chat_stop_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                // Send Message Button
                val canSend = text.isNotBlank() || attachedImageBase64 != null
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) DevConstants.PrimaryGradient else Brush.linearGradient(
                                listOf(Color(0x33A78BFA), Color(0x22A78BFA))
                            )
                        )
                        .clickable(enabled = canSend) { onSend() }
                        .testTag("chat_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Send",
                        tint = if (canSend) Color.White else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
