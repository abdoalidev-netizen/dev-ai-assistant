package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GroundingSource
import com.example.domain.model.Message
import com.example.domain.model.MessageSender
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ChatMessageItem.kt
 * فقاعة الرسالة (رسائل المستخدم ورسائل Dev).
 * يدعم التدرجات الفخمة، كروت المصادر، وأزرار التحكم: نسخ، مشاركة، استماع صوتي، تقييم، مفضلة، وإعادة التوليد.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatMessageItem(
    message: Message,
    isStreaming: Boolean = false,
    isSpeaking: Boolean = false,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSpeak: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onRegenerate: () -> Unit,
    onRate: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onCodeExplain: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val context = LocalContext.current
    val isUser = message.sender == MessageSender.USER
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            DevAvatar(
                size = 36.dp,
                isThinking = isStreaming,
                modifier = Modifier.padding(end = 8.dp, top = 4.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f, fill = false),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Main Bubble
            if (isUser) {
                // User Message: Gradient Bubble
                val userBubbleShape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 4.dp,
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )

                Surface(
                    modifier = Modifier
                        .testTag("user_message_bubble")
                        .shadow(6.dp, shape = userBubbleShape, spotColor = Color(0x66EC4899))
                        .clip(userBubbleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF7C3AED), Color(0xFFEC4899))
                            ),
                            shape = userBubbleShape
                        ),
                    color = Color.Transparent
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(
                            text = message.content,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = timeStr,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 10.sp
                            )
                            if (onEdit != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onEdit() }
                                )
                            }
                        }
                    }
                }
            } else {
                // Assistant (Dev) Message: Glassmorphism Bubble
                val assistantBubbleShape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 24.dp,
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )

                GlassCard(
                    modifier = Modifier.testTag("assistant_message_bubble"),
                    shape = assistantBubbleShape,
                    isDark = isDark,
                    borderBrush = Brush.linearGradient(
                        colors = listOf(Color(0x40A78BFA), Color(0x20EC4899))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header label: Dev ✨
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "Dev",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA78BFA),
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "✨", fontSize = 12.sp)

                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = timeStr,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                fontSize = 10.sp
                            )
                        }

                        // Markdown text rendering
                        MarkdownText(
                            markdown = message.content,
                            textColor = MaterialTheme.colorScheme.onSurface,
                            onCodeExplain = onCodeExplain
                        )

                        // Streaming typing cursor
                        if (isStreaming) {
                            val infiniteTransition = rememberInfiniteTransition(label = "cursor")
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.2f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(400),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "cursor_alpha"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(width = 8.dp, height = 16.dp)
                                    .alpha(alpha)
                                    .background(Color(0xFF22D3EE), RoundedCornerShape(2.dp))
                            )
                        }

                        // Google Search Grounding Sources Cards
                        if (message.groundingSources.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "المصادر المرجعية:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF22D3EE)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                message.groundingSources.forEach { source ->
                                    SourceChip(source = source) {
                                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(source.url))
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    }
                                }
                            }
                        }

                        // Action Bar: Copy, Share, TTS, Regenerate, Thumbs, Bookmark
                        if (!isStreaming) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Copy
                                IconButton(
                                    onClick = { onCopy(message.content) },
                                    modifier = Modifier.size(32.dp).testTag("action_copy")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Share
                                IconButton(
                                    onClick = { onShare(message.content) },
                                    modifier = Modifier.size(32.dp).testTag("action_share")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // TTS (Listen/Stop)
                                IconButton(
                                    onClick = {
                                        if (isSpeaking) onStopSpeaking() else onSpeak(message.content)
                                    },
                                    modifier = Modifier.size(32.dp).testTag("action_tts")
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Listen",
                                        tint = if (isSpeaking) Color(0xFFEC4899) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Regenerate
                                IconButton(
                                    onClick = onRegenerate,
                                    modifier = Modifier.size(32.dp).testTag("action_regenerate")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Regenerate",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Thumbs Up
                                IconButton(
                                    onClick = { onRate(if (message.rating == 1) 0 else 1) },
                                    modifier = Modifier.size(32.dp).testTag("action_thumbs_up")
                                ) {
                                    Icon(
                                        imageVector = if (message.rating == 1) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                        contentDescription = "Like",
                                        tint = if (message.rating == 1) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Thumbs Down
                                IconButton(
                                    onClick = { onRate(if (message.rating == -1) 0 else -1) },
                                    modifier = Modifier.size(32.dp).testTag("action_thumbs_down")
                                ) {
                                    Icon(
                                        imageVector = if (message.rating == -1) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                        contentDescription = "Dislike",
                                        tint = if (message.rating == -1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Favorite Bookmark
                                IconButton(
                                    onClick = onToggleFavorite,
                                    modifier = Modifier.size(32.dp).testTag("action_favorite")
                                ) {
                                    Icon(
                                        imageVector = if (message.isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (message.isFavorite) Color(0xFFFDE047) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SourceChip(
    source: GroundingSource,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(0.8.dp, Color(0x4422D3EE), RoundedCornerShape(12.dp)),
        color = Color(0x22151330),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = Color(0xFF22D3EE),
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = source.title.take(20) + if (source.title.length > 20) "…" else "",
                fontSize = 11.sp,
                color = Color(0xFFE2E8F0)
            )
        }
    }
}
