package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.DevApplication
import com.example.constants.DevConstants
import com.example.data.datastore.DevPreferences
import com.example.data.remote.StreamChunk
import com.example.domain.model.Conversation
import com.example.domain.model.DeviceActionRequest
import com.example.domain.model.DeviceActionType
import com.example.domain.model.GeneratedImageItem
import com.example.domain.model.GroundingSource
import com.example.domain.model.MemoryItem
import com.example.domain.model.Message
import com.example.domain.model.MessageSender
import com.example.domain.model.Persona
import com.example.services.ExportFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * DevViewModel.kt
 * العقل المدبر لطبقة العرض (MVVM ViewModel).
 * يدير تدفق البيانات، التفاعل مع الذكاء الاصطناعي، التوليد التدفقي الحي، واستوديو الصور والأدوات الذكية.
 */

enum class NavTab {
    CHAT,
    STUDIO,
    TOOLS,
    HISTORY,
    SETTINGS
}

class DevViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as DevApplication
    private val repository = app.repository
    private val prefRepo = app.preferencesRepository
    private val ttsManager = app.ttsManager
    private val exportManager = app.exportManager
    private val deviceActionExecutor = app.deviceActionExecutor

    // --- تفضيلات المستخدم ---
    val preferences: StateFlow<DevPreferences> = prefRepo.preferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, DevPreferences())

    // --- التنقل والشاشات ---
    private val _currentTab = MutableStateFlow(NavTab.CHAT)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _showSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean> = _showSplash.asStateFlow()

    private val _activeToolId = MutableStateFlow<String?>(null)
    val activeToolId: StateFlow<String?> = _activeToolId.asStateFlow()

    private val _showMemoryManager = MutableStateFlow(false)
    val showMemoryManager: StateFlow<Boolean> = _showMemoryManager.asStateFlow()

    private val _showAboutDialog = MutableStateFlow(false)
    val showAboutDialog: StateFlow<Boolean> = _showAboutDialog.asStateFlow()

    private val _pendingDeviceAction = MutableStateFlow<DeviceActionRequest?>(null)
    val pendingDeviceAction: StateFlow<DeviceActionRequest?> = _pendingDeviceAction.asStateFlow()

    private val _confettiTrigger = MutableStateFlow(0)
    val confettiTrigger: StateFlow<Int> = _confettiTrigger.asStateFlow()

    // --- المحادثات ---
    val conversations: StateFlow<List<Conversation>> = repository.getAllConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    // --- البث التدفقي (Streaming) ---
    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private val _streamingSources = MutableStateFlow<List<GroundingSource>>(emptyList())
    val streamingSources: StateFlow<List<GroundingSource>> = _streamingSources.asStateFlow()

    private var streamingJob: Job? = null

    // --- الشخصية / الوضع (Persona) ---
    val availablePersonas: List<Persona> = repository.getBuiltInPersonas()
    private val _activePersona = MutableStateFlow(availablePersonas.first())
    val activePersona: StateFlow<Persona> = _activePersona.asStateFlow()

    // --- مدخلات المحادثة ---
    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _attachedImageBase64 = MutableStateFlow<String?>(null)
    val attachedImageBase64: StateFlow<String?> = _attachedImageBase64.asStateFlow()

    private val _attachedFileName = MutableStateFlow<String?>(null)
    val attachedFileName: StateFlow<String?> = _attachedFileName.asStateFlow()

    // --- استوديو الصور ---
    private val _studioPrompt = MutableStateFlow("")
    val studioPrompt: StateFlow<String> = _studioPrompt.asStateFlow()

    private val _enhancedPrompt = MutableStateFlow("")
    val enhancedPrompt: StateFlow<String> = _enhancedPrompt.asStateFlow()

    private val _isEnhancing = MutableStateFlow(false)
    val isEnhancing: StateFlow<Boolean> = _isEnhancing.asStateFlow()

    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    private val _selectedStyle = MutableStateFlow("واقعي (Photorealistic)")
    val selectedStyle: StateFlow<String> = _selectedStyle.asStateFlow()

    private val _selectedAspectRatio = MutableStateFlow("1:1")
    val selectedAspectRatio: StateFlow<String> = _selectedAspectRatio.asStateFlow()

    private val _studioError = MutableStateFlow<String?>(null)
    val studioError: StateFlow<String?> = _studioError.asStateFlow()

    val generatedImages: StateFlow<List<GeneratedImageItem>> = repository.getAllGeneratedImages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _fullscreenImage = MutableStateFlow<GeneratedImageItem?>(null)
    val fullscreenImage: StateFlow<GeneratedImageItem?> = _fullscreenImage.asStateFlow()

    // --- الأدوات الذكية ---
    private val _toolInput = MutableStateFlow("")
    val toolInput: StateFlow<String> = _toolInput.asStateFlow()

    private val _toolExtra = MutableStateFlow("")
    val toolExtra: StateFlow<String> = _toolExtra.asStateFlow()

    private val _toolOutput = MutableStateFlow("")
    val toolOutput: StateFlow<String> = _toolOutput.asStateFlow()

    private val _isToolRunning = MutableStateFlow(false)
    val isToolRunning: StateFlow<Boolean> = _isToolRunning.asStateFlow()

    private val _toolError = MutableStateFlow<String?>(null)
    val toolError: StateFlow<String?> = _toolError.asStateFlow()

    // --- الذاكرة ---
    val memoryItems: StateFlow<List<MemoryItem>> = repository.getAllMemoryItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- حالة الصوت (TTS) ---
    val isTtsPlaying = ttsManager.isPlaying
    val currentTtsUtteranceId = ttsManager.currentUtteranceId

    init {
        // مراقبة المحادثة النشطة وجلب رسائلها من Room
        viewModelScope.launch {
            _activeConversationId.collectLatest { convId ->
                if (convId != null) {
                    repository.getMessagesForConversation(convId).collectLatest { list ->
                        _messages.value = list
                    }
                } else {
                    _messages.value = emptyList()
                }
            }
        }
    }

    // --- وظائف التنقل ---
    fun setTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun hideSplash() {
        _showSplash.value = false
    }

    fun openTool(toolId: String) {
        _activeToolId.value = toolId
        _toolInput.value = ""
        _toolOutput.value = ""
        _toolError.value = null
        // Preset default extra params
        _toolExtra.value = when (toolId) {
            "app_builder" -> "تطبيق متكامل مع كود وAPK"
            "translator" -> "الإنجليزية"
            "writer" -> "رسمي واحترافي"
            "code" -> "Kotlin"
            "tutor" -> "مبسط للمبتدئين"
            else -> ""
        }
    }

    fun closeTool() {
        _activeToolId.value = null
    }

    fun toggleMemoryManager(show: Boolean) {
        _showMemoryManager.value = show
    }

    fun toggleAboutDialog(show: Boolean) {
        _showAboutDialog.value = show
    }

    // --- وظائف المحادثة ---

    fun selectConversation(id: String) {
        _activeConversationId.value = id
        setTab(NavTab.CHAT)
    }

    fun createNewChat(personaId: String = "general") {
        viewModelScope.launch {
            stopGenerating()
            val chosenPersona = availablePersonas.find { it.id == personaId } ?: availablePersonas.first()
            _activePersona.value = chosenPersona
            val newConv = repository.createConversation(title = "محادثة جديدة", personaId = personaId)
            _activeConversationId.value = newConv.id
            setTab(NavTab.CHAT)
        }
    }

    fun setPersona(persona: Persona) {
        _activePersona.value = persona
    }

    fun setChatInput(text: String) {
        _chatInput.value = text
    }

    fun attachImage(base64: String?) {
        _attachedImageBase64.value = base64
        _attachedFileName.value = if (base64 != null) "صورة مرفقة" else null
    }

    fun clearAttachment() {
        _attachedImageBase64.value = null
        _attachedFileName.value = null
    }

    fun sendMessage(customPrompt: String? = null) {
        val prompt = (customPrompt ?: _chatInput.value).trim()
        if (prompt.isEmpty() && _attachedImageBase64.value == null) return

        val currentConvId = _activeConversationId.value

        viewModelScope.launch {
            val convId = if (currentConvId == null) {
                val newConv = repository.createConversation(
                    title = if (prompt.length > 25) prompt.take(25) + "…" else prompt,
                    personaId = _activePersona.value.id
                )
                _activeConversationId.value = newConv.id
                newConv.id
            } else {
                currentConvId
            }

            val userMsg = Message(
                id = UUID.randomUUID().toString(),
                conversationId = convId,
                sender = MessageSender.USER,
                content = prompt,
                imageBase64 = _attachedImageBase64.value,
                attachmentName = _attachedFileName.value
            )

            repository.saveMessage(userMsg)

            // تفريغ الحقول بعد الحفظ
            if (customPrompt == null) {
                _chatInput.value = ""
                clearAttachment()
            }

            // فحص أوامر الجهاز المباشرة في السؤال (مثل "اضبط منبه"، "شغل مؤقت")
            inspectForDeviceAction(prompt)

            // بدء التوليد من Gemini
            startStreamingResponse(convId, prompt, userMsg.imageBase64)
        }
    }

    private fun startStreamingResponse(convId: String, prompt: String, imageBase64: String?) {
        stopGenerating()

        streamingJob = viewModelScope.launch {
            _isStreaming.value = true
            _streamingText.value = ""
            _streamingSources.value = emptyList()

            val prefs = preferences.value
            val effectiveApiKey = app.geminiService.resolveApiKey(prefs.customApiKey)

            // بناء تعليمات النظام مع مراعاة لغة الذكاء الاصطناعي المحددة من الشاشة الرئيسية
            var systemInstruction = _activePersona.value.systemPrompt
            when (prefs.aiLanguage) {
                "ar" -> systemInstruction += "\nتنبيه مهم للغة الرد: أجب دائماً باللغة العربية الفصحى المتقنة أو بلهجة السائل."
                "en" -> systemInstruction += "\nLanguage directive: Always respond in natural, fluent, and professional English."
                "fr" -> systemInstruction += "\nDirective linguistique: Répondez toujours en français naturel et précis."
                "es" -> systemInstruction += "\nDirectiva de idioma: Responda siempre en español fluido y profesional."
                "de" -> systemInstruction += "\nSprachanweisung: Antworten Sie immer auf klarem und fehlerfreiem Deutsch."
                "tr" -> systemInstruction += "\nDil talimatı: Her zaman akıcı ve profesyonel Türkçe ile yanıt verin."
                "ru" -> systemInstruction += "\nЯзыковая директива: Всегда отвечайте на грамотном русском языке."
                "zh" -> systemInstruction += "\n语言指示：请始终使用自然、流畅的中文进行回答。"
                "ja" -> systemInstruction += "\n言語指示：常に自然で丁寧な日本語で回答してください。"
                else -> systemInstruction += "\nتنبيه تلقائي للغة: تعرّف فوراً على لغة أو لهجة سؤال المستخدم ورد عليه بنفس لغته بطلاقة تامة وفصاحة طبيعية."
            }

            if (prefs.longTermMemoryEnabled) {
                val memorySummary = repository.getMemorySummaryForPrompt()
                if (memorySummary.isNotEmpty()) {
                    systemInstruction += "\n$memorySummary"
                }
            }

            val history = _messages.value.filter { !it.isStreaming }

            repository.streamAssistantResponse(
                conversationId = convId,
                prompt = prompt,
                modelName = prefs.selectedModel,
                apiKey = effectiveApiKey,
                systemInstruction = systemInstruction,
                history = history,
                imageBase64 = imageBase64,
                enableSearchGrounding = prefs.searchGroundingEnabled,
                responseStyle = prefs.responseStyle
            ).collect { chunk ->
                when (chunk) {
                    is StreamChunk.Text -> {
                        _streamingText.value += chunk.content
                    }
                    is StreamChunk.Source -> {
                        val currentSources = _streamingSources.value.toMutableList()
                        if (currentSources.none { it.url == chunk.source.url }) {
                            currentSources.add(chunk.source)
                            _streamingSources.value = currentSources
                        }
                    }
                    is StreamChunk.Done -> {
                        finalizeAssistantMessage(convId, _streamingText.value, chunk.sources)
                    }
                    is StreamChunk.Error -> {
                        _streamingText.value += "\n\n⚠️ ${chunk.message}"
                        finalizeAssistantMessage(convId, _streamingText.value, _streamingSources.value)
                    }
                }
            }
        }
    }

    private suspend fun finalizeAssistantMessage(convId: String, content: String, sources: List<GroundingSource>) {
        if (content.isNotBlank()) {
            val assistantMsg = Message(
                id = UUID.randomUUID().toString(),
                conversationId = convId,
                sender = MessageSender.ASSISTANT,
                content = content,
                groundingSources = sources
            )
            repository.saveMessage(assistantMsg)
        }
        _isStreaming.value = false
        _streamingText.value = ""
        _streamingSources.value = emptyList()
    }

    fun stopGenerating() {
        streamingJob?.cancel()
        streamingJob = null
        if (_isStreaming.value) {
            val convId = _activeConversationId.value
            val currentContent = _streamingText.value
            if (convId != null && currentContent.isNotBlank()) {
                viewModelScope.launch {
                    finalizeAssistantMessage(convId, currentContent, _streamingSources.value)
                }
            }
            _isStreaming.value = false
            _streamingText.value = ""
        }
    }

    fun regenerateMessage(messageId: String) {
        val msgs = _messages.value
        val targetIndex = msgs.indexOfFirst { it.id == messageId }
        if (targetIndex != -1) {
            // العثور على سؤال المستخدم المقترن بالرد
            val userMsg = if (msgs[targetIndex].sender == MessageSender.ASSISTANT && targetIndex > 0) {
                msgs[targetIndex - 1]
            } else msgs[targetIndex]

            sendMessage(customPrompt = userMsg.content)
        }
    }

    fun copyToClipboard(text: String) {
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Dev AI Response", text)
        clipboard.setPrimaryClip(clip)
    }

    fun speakText(text: String, utteranceId: String) {
        ttsManager.speak(text, utteranceId, preferences.value.ttsSpeed)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun rateMessage(messageId: String, rating: Int) {
        viewModelScope.launch {
            repository.updateMessageRating(messageId, rating)
        }
    }

    fun toggleFavoriteMessage(messageId: String, current: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteMessage(messageId, current)
        }
    }

    fun togglePinConversation(id: String, current: Boolean) {
        viewModelScope.launch { repository.togglePinConversation(id, current) }
    }

    fun toggleFavoriteConversation(id: String, current: Boolean) {
        viewModelScope.launch { repository.toggleFavoriteConversation(id, current) }
    }

    fun renameConversation(id: String, newTitle: String) {
        viewModelScope.launch { repository.updateConversationTitle(id, newTitle) }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            if (_activeConversationId.value == id) {
                _activeConversationId.value = null
            }
            repository.deleteConversation(id)
        }
    }

    fun clearAllConversations() {
        viewModelScope.launch {
            _activeConversationId.value = null
            repository.deleteAllConversations()
        }
    }

    fun exportActiveConversation(format: ExportFormat) {
        val convId = _activeConversationId.value ?: return
        val conv = conversations.value.find { it.id == convId } ?: return
        val msgs = _messages.value
        val intent = exportManager.exportAndShare(conv, msgs, format)
        if (intent != null) {
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            app.startActivity(intent)
            _confettiTrigger.value++
        }
    }

    // --- استوديو الصور ---

    fun setStudioPrompt(prompt: String) {
        _studioPrompt.value = prompt
    }

    fun setStudioStyle(style: String) {
        _selectedStyle.value = style
    }

    fun setStudioAspectRatio(ratio: String) {
        _selectedAspectRatio.value = ratio
    }

    fun setFullscreenImage(image: GeneratedImageItem?) {
        _fullscreenImage.value = image
    }

    fun enhanceStudioPrompt() {
        val raw = _studioPrompt.value.trim()
        if (raw.isEmpty()) return

        viewModelScope.launch {
            _isEnhancing.value = true
            _studioError.value = null
            val apiKey = app.geminiService.resolveApiKey(preferences.value.customApiKey)
            val result = repository.enhancePrompt(raw, apiKey)
            result.fold(
                onSuccess = { enhanced ->
                    _enhancedPrompt.value = enhanced
                    _confettiTrigger.value++
                },
                onFailure = { error ->
                    _studioError.value = error.localizedMessage
                }
            )
            _isEnhancing.value = false
        }
    }

    fun generateStudioImage() {
        val raw = _studioPrompt.value.trim()
        val enhanced = _enhancedPrompt.value.ifBlank { raw }
        if (enhanced.isEmpty()) return

        viewModelScope.launch {
            _isGeneratingImage.value = true
            _studioError.value = null
            val apiKey = app.geminiService.resolveApiKey(preferences.value.customApiKey)

            val result = repository.generateAndSaveImage(
                apiKey = apiKey,
                originalPrompt = raw,
                enhancedPrompt = enhanced,
                style = _selectedStyle.value,
                aspectRatio = _selectedAspectRatio.value
            )

            result.fold(
                onSuccess = { item ->
                    _fullscreenImage.value = item
                    _confettiTrigger.value++
                },
                onFailure = { error ->
                    _studioError.value = error.localizedMessage
                }
            )
            _isGeneratingImage.value = false
        }
    }

    // --- الأدوات الذكية ---

    fun setToolInput(text: String) {
        _toolInput.value = text
    }

    fun setToolExtra(extra: String) {
        _toolExtra.value = extra
    }

    fun runTool() {
        val toolId = _activeToolId.value ?: return
        val input = _toolInput.value.trim()
        if (input.isEmpty()) return

        viewModelScope.launch {
            _isToolRunning.value = true
            _toolOutput.value = ""
            _toolError.value = null

            val apiKey = app.geminiService.resolveApiKey(preferences.value.customApiKey)
            val result = repository.executeSmartTool(
                toolType = toolId,
                input = input,
                extraParam = _toolExtra.value,
                apiKey = apiKey
            )

            result.fold(
                onSuccess = { out ->
                    _toolOutput.value = out
                    _confettiTrigger.value++
                },
                onFailure = { err ->
                    _toolError.value = err.localizedMessage
                }
            )
            _isToolRunning.value = false
        }
    }

    // --- الذاكرة ---

    fun addMemory(topic: String, content: String) {
        if (topic.isNotBlank() && content.isNotBlank()) {
            viewModelScope.launch {
                repository.addMemoryItem(topic, content)
            }
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            repository.deleteMemoryItem(id)
        }
    }

    fun clearAllMemory() {
        viewModelScope.launch {
            repository.clearAllMemory()
        }
    }

    // --- أوامر النظام وجهاز المستخدم ---

    private fun inspectForDeviceAction(text: String) {
        val lower = text.lowercase()
        when {
            lower.contains("منبه") || lower.contains("alarm") -> {
                _pendingDeviceAction.value = DeviceActionRequest(
                    actionType = DeviceActionType.SET_ALARM,
                    title = "ضبط منبه",
                    description = "تجهيز منبه على الساعة 7:00 صباحاً",
                    params = mapOf("hour" to "7", "minutes" to "0", "message" to "Dev Alarm")
                )
            }
            lower.contains("مؤقت") || lower.contains("timer") -> {
                _pendingDeviceAction.value = DeviceActionRequest(
                    actionType = DeviceActionType.SET_TIMER,
                    title = "تشغيل مؤقت",
                    description = "تجهيز مؤقت مدته 5 دقائق (300 ثانية)",
                    params = mapOf("seconds" to "300", "message" to "Dev Timer")
                )
            }
            lower.contains("حدث") || lower.contains("تقويم") || lower.contains("calendar") -> {
                _pendingDeviceAction.value = DeviceActionRequest(
                    actionType = DeviceActionType.ADD_CALENDAR_EVENT,
                    title = "إضافة موعد للتقويم",
                    description = "إضافة حدث جديد إلى تقويم جهازك",
                    params = mapOf("title" to text.take(30), "description" to text)
                )
            }
            lower.contains("إيميل") || lower.contains("بريد") || lower.contains("email") -> {
                _pendingDeviceAction.value = DeviceActionRequest(
                    actionType = DeviceActionType.SEND_EMAIL,
                    title = "تجهيز بريد إلكتروني",
                    description = "فتح تطبيق البريد لمراجعة وإرسال الرسالة",
                    params = mapOf("subject" to "موضوع من Dev", "body" to text)
                )
            }
        }
    }

    fun confirmDeviceAction(request: DeviceActionRequest) {
        deviceActionExecutor.executeAction(request)
        _pendingDeviceAction.value = null
    }

    fun cancelDeviceAction() {
        _pendingDeviceAction.value = null
    }

    // --- الإعدادات ---

    fun updateModel(model: String) {
        viewModelScope.launch { prefRepo.setSelectedModel(model) }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch { prefRepo.setThemeMode(theme) }
    }

    fun updateLanguage(lang: String) {
        viewModelScope.launch { prefRepo.setLanguage(lang) }
    }

    fun updateFontScale(scale: Float) {
        viewModelScope.launch { prefRepo.setFontScale(scale) }
    }

    fun updateTtsSpeed(speed: Float) {
        viewModelScope.launch { prefRepo.setTtsSpeed(speed) }
    }

    fun updateHaptic(enabled: Boolean) {
        viewModelScope.launch { prefRepo.setHapticEnabled(enabled) }
    }

    fun updateMemoryEnabled(enabled: Boolean) {
        viewModelScope.launch { prefRepo.setMemoryEnabled(enabled) }
    }

    fun updateSearchGrounding(enabled: Boolean) {
        viewModelScope.launch { prefRepo.setSearchGrounding(enabled) }
    }

    fun updateResponseStyle(style: String) {
        viewModelScope.launch { prefRepo.setResponseStyle(style) }
    }

    fun updateCustomApiKey(key: String) {
        viewModelScope.launch { prefRepo.setCustomApiKey(key) }
    }

    fun completeOnboarding(userName: String) {
        viewModelScope.launch {
            if (userName.isNotBlank()) prefRepo.setUserName(userName)
            prefRepo.setOnboardingCompleted(true)
        }
    }

    fun loginWithGoogle(name: String, email: String) {
        viewModelScope.launch {
            prefRepo.loginUser(name, email)
            _confettiTrigger.value++
        }
    }

    fun logout() {
        viewModelScope.launch {
            stopGenerating()
            prefRepo.logoutUser()
            _activeConversationId.value = null
        }
    }

    fun updateAiLanguage(lang: String) {
        viewModelScope.launch {
            prefRepo.setAiLanguage(lang)
        }
    }

    fun updateProxyServerUrl(url: String) {
        viewModelScope.launch {
            prefRepo.setProxyServerUrl(url)
        }
    }
}
