package com.example.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.domain.model.Message
import com.example.domain.model.MessageSender
import com.example.ui.components.AuroraBackground
import com.example.ui.components.ChatInputField
import com.example.ui.components.ChatMessageItem
import com.example.ui.components.DevAvatar
import com.example.ui.components.DevTopAppBar
import com.example.ui.components.GlassCard
import com.example.ui.components.SuggestionChips
import com.example.ui.components.SuggestionItem
import com.example.viewmodel.DevViewModel

/**
 * ChatScreen.kt
 * قلب التطبيق والمحادثة الذكية:
 * يدعم التوليد التدفقي الحي مع مؤشر كتابة، عرض Markdown، جداول، وأكواد مع تلوين ونسخ وشرح،
 * وبطاقات مصادر الويب، وبطاقات الترحيب والاقتراحات الغنية.
 */
@Composable
fun ChatScreen(
    viewModel: DevViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val streamingSources by viewModel.streamingSources.collectAsState()
    val chatInput by viewModel.chatInput.collectAsState()
    val attachedImage by viewModel.attachedImageBase64.collectAsState()
    val activePersona by viewModel.activePersona.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val isSpeaking by viewModel.isTtsPlaying.collectAsState()

    val listState = rememberLazyListState()

    // التمرير التلقائي لأسفل عند وصول رسالة جديدة أو تدفق نص
    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty() || streamingText.isNotEmpty()) {
            val totalItems = messages.size + (if (isStreaming) 1 else 0)
            if (totalItems > 0) {
                listState.animateScrollToItem(totalItems - 1)
            }
        }
    }

    val quickSuggestions = remember {
        listOf(
            SuggestionItem("برمجة تطبيق", "اكتب لي كود شاشة أندرويد متكاملة باستخدام Jetpack Compose", Icons.Default.Code),
            SuggestionItem("صياغة إيميل رسمي", "صِغ لي إيميل رسمي بالإنجليزية لطلب شراكة عمل", Icons.Default.Description),
            SuggestionItem("شرح فيزياء الكم", "اشرح لي نظرية التشابك الكمي بأسلوب فاينمان المبسط", Icons.Default.Psychology),
            SuggestionItem("خطة دراسية", "ضع لي جدولاً دراسياً مكثفاً لتعلم لغة Kotlin في 14 يوماً", Icons.Default.School),
            SuggestionItem("ترجمة وسياق", "ترجم النص التالي بدقة مع بدائل: 'The early bird catches the worm'", Icons.Default.Translate)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_screen")
    ) {
        AuroraBackground(isDark = preferences.themeMode == "dark")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            // Top App Bar
            DevTopAppBar(
                title = DevConstants.APP_NAME,
                activePersona = activePersona,
                availablePersonas = viewModel.availablePersonas,
                onPersonaSelected = { viewModel.setPersona(it) },
                selectedModel = preferences.selectedModel,
                onModelToggle = { viewModel.updateModel(it) },
                searchGroundingEnabled = preferences.searchGroundingEnabled,
                onSearchGroundingToggle = { viewModel.updateSearchGrounding(it) },
                onNewChat = { viewModel.createNewChat(activePersona.id) },
                onExportChat = { viewModel.exportActiveConversation(it) },
                onClearChat = { viewModel.clearAllConversations() }
            )

            // AI Response Language Quick Selector Bar
            AiLanguageSelectorBar(
                selectedLang = preferences.aiLanguage,
                onSelectLang = { viewModel.updateAiLanguage(it) }
            )

            // Chat Body (Empty state vs Message list)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty() && !isStreaming) {
                    // Empty State Hero Banner & Suggestion Cards
                    EmptyChatWelcomeView(
                        onSelectSuggestion = { prompt ->
                            viewModel.setChatInput(prompt)
                            viewModel.sendMessage(prompt)
                        }
                    )
                } else {
                    // Messages LazyColumn
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            ChatMessageItem(
                                message = msg,
                                isStreaming = false,
                                isSpeaking = isSpeaking,
                                onCopy = { viewModel.copyToClipboard(it) },
                                onShare = { text ->
                                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_TEXT, text)
                                    }
                                    shareIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                    viewModel.getApplication<com.example.DevApplication>().startActivity(shareIntent)
                                },
                                onSpeak = { viewModel.speakText(it, msg.id) },
                                onStopSpeaking = { viewModel.stopSpeaking() },
                                onRegenerate = { viewModel.regenerateMessage(msg.id) },
                                onRate = { rating -> viewModel.rateMessage(msg.id, rating) },
                                onToggleFavorite = { viewModel.toggleFavoriteMessage(msg.id, msg.isFavorite) },
                                onEdit = if (msg.sender == MessageSender.USER) {
                                    {
                                        viewModel.setChatInput(msg.content)
                                    }
                                } else null,
                                onCodeExplain = { code ->
                                    viewModel.sendMessage("اشرح هذا الكود البرمجي بالتفصيل:\n```\n$code\n```")
                                }
                            )
                        }

                        // Live Streaming Bubble
                        if (isStreaming && streamingText.isNotEmpty()) {
                            item(key = "streaming_current") {
                                val dummyMessage = Message(
                                    id = "streaming",
                                    conversationId = "",
                                    sender = MessageSender.ASSISTANT,
                                    content = streamingText,
                                    groundingSources = streamingSources
                                )
                                ChatMessageItem(
                                    message = dummyMessage,
                                    isStreaming = true,
                                    onCopy = {},
                                    onShare = {},
                                    onSpeak = {},
                                    onStopSpeaking = {},
                                    onRegenerate = {},
                                    onRate = {},
                                    onToggleFavorite = {}
                                )
                            }
                        }
                    }
                }
            }

            // Quick suggestion chips bar above input
            SuggestionChips(
                suggestions = quickSuggestions,
                onSelect = { prompt ->
                    viewModel.setChatInput(prompt)
                    viewModel.sendMessage(prompt)
                }
            )

            // Bottom Input Field - Elevated cleanly above bottom navigation bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 84.dp)
            ) {
                ChatInputField(
                    text = chatInput,
                    onTextChanged = { viewModel.setChatInput(it) },
                    onSend = { viewModel.sendMessage() },
                    onStop = { viewModel.stopGenerating() },
                    isStreaming = isStreaming,
                    attachedImageBase64 = attachedImage,
                    onImageAttached = { viewModel.attachImage(it) }
                )
            }
        }
    }
}

/**
 * شريط اختيار لغة الذكاء الاصطناعي السريع والمباشر من الصفحة الرئيسية
 */
@Composable
fun AiLanguageSelectorBar(
    selectedLang: String,
    onSelectLang: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = remember {
        listOf(
            Triple("auto", "⚡ تلقائي (Auto)", "التعرف الذكي على لغة سؤالك"),
            Triple("ar", "🇸🇦 العربية", "العربية الفصحى واللهجات"),
            Triple("en", "🇺🇸 English", "Fluent English"),
            Triple("fr", "🇫🇷 Français", "Français élégant"),
            Triple("es", "🇪🇸 Español", "Español fluido"),
            Triple("de", "🇩🇪 Deutsch", "Deutsches Fachwissen"),
            Triple("tr", "🇹🇷 Türkçe", "Akıcı Türkçe"),
            Triple("ru", "🇷🇺 Русский", "Грамотный русский"),
            Triple("zh", "🇨🇳 中文", "标准普通话"),
            Triple("ja", "🇯🇵 日本語", "自然な日本語")
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = null,
            tint = Color(0xFF22D3EE),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "لغة الرد:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8)
        )

        languages.forEach { (code, label, _) ->
            val isSelected = selectedLang == code
            FilterChip(
                selected = isSelected,
                onClick = { onSelectLang(code) },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0x337C3AED),
                    selectedLabelColor = Color(0xFF22D3EE),
                    containerColor = Color(0x1A1E1B4B),
                    labelColor = Color(0xFFCBD5E1)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color(0x33A78BFA),
                    selectedBorderColor = Color(0xFF22D3EE)
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun EmptyChatWelcomeView(
    onSelectSuggestion: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DevAvatar(size = 72.dp, isThinking = false)

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(id = R.string.welcome_to_dev),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.welcome_subtitle),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Grid of Colorful Suggestions
        val starterIdeas = listOf(
            StarterCardData("🚀 برمجة تطبيق أندرويد", "اكتب لي كود تطبيق ذكي في Jetpack Compose مع التعليقات", Color(0xFF7C3AED)),
            StarterCardData("✍️ صياغة إيميل رسمي", "صِغ لي إيميل رسمي احترافي للاعتذار عن موعد مع بدائل", Color(0xFFEC4899)),
            StarterCardData("💡 شرح معقد ببساطة", "اشرح لي خوارزميات الذكاء الاصطناعي وكيف تعمل النماذج اللغوية", Color(0xFF06B6D4)),
            StarterCardData("🌐 ترجمة وثقافة لغوية", "ترجم هذا النص للفرنسية والألمانية مع توضيح الفروق الثقافية", Color(0xFF10B981))
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            starterIdeas.forEach { idea ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectSuggestion(idea.prompt) },
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(idea.accentColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = idea.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = idea.prompt,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class StarterCardData(
    val title: String,
    val prompt: String,
    val accentColor: Color
)
