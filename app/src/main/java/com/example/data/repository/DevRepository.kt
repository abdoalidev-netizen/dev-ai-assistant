package com.example.data.repository

import android.content.Context
import com.example.constants.DevConstants
import com.example.data.local.ConversationDao
import com.example.data.local.ConversationEntity
import com.example.data.local.GeneratedImageEntity
import com.example.data.local.ImageDao
import com.example.data.local.MemoryDao
import com.example.data.local.MemoryEntity
import com.example.data.local.MessageDao
import com.example.data.local.MessageEntity
import com.example.data.remote.GeminiService
import com.example.data.remote.StreamChunk
import com.example.domain.model.Conversation
import com.example.domain.model.GeneratedImageItem
import com.example.domain.model.GroundingSource
import com.example.domain.model.MemoryItem
import com.example.domain.model.Message
import com.example.domain.model.MessageSender
import com.example.domain.model.Persona
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * DevRepository.kt
 * مستودع البيانات المركزي (Repository Pattern).
 * يربط بين تخزين Room المحلي وقاعدة البيانات، وإعدادات المستخدم، وخدمة Gemini عن بُعد.
 */
class DevRepository(
    private val context: Context,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val memoryDao: MemoryDao,
    private val imageDao: ImageDao,
    private val geminiService: GeminiService
) {

    // --- المحادثات ---

    fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations().map { list ->
            list.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createConversation(title: String = "محادثة جديدة", personaId: String = "general"): Conversation {
        return withContext(Dispatchers.IO) {
            val id = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val entity = ConversationEntity(
                id = id,
                title = title,
                createdAt = now,
                updatedAt = now,
                isPinned = false,
                isFavorite = false,
                personaId = personaId
            )
            conversationDao.insertConversation(entity)
            entity.toDomain()
        }
    }

    suspend fun updateConversationTitle(id: String, newTitle: String) {
        withContext(Dispatchers.IO) {
            conversationDao.updateTitle(id, newTitle, System.currentTimeMillis())
        }
    }

    suspend fun togglePinConversation(id: String, currentPinned: Boolean) {
        withContext(Dispatchers.IO) {
            conversationDao.updatePinned(id, !currentPinned)
        }
    }

    suspend fun toggleFavoriteConversation(id: String, currentFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            conversationDao.updateFavorite(id, !currentFavorite)
        }
    }

    suspend fun deleteConversation(id: String) {
        withContext(Dispatchers.IO) {
            conversationDao.deleteConversationById(id)
        }
    }

    suspend fun deleteAllConversations() {
        withContext(Dispatchers.IO) {
            conversationDao.deleteAllConversations()
        }
    }

    // --- الرسائل ---

    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(conversationId).map { list ->
            list.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun saveMessage(message: Message) {
        withContext(Dispatchers.IO) {
            messageDao.insertMessage(message.toEntity())
            conversationDao.updateTitle(
                id = message.conversationId,
                newTitle = if (message.content.length > 30) message.content.take(30) + "…" else message.content,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    suspend fun updateMessageRating(messageId: String, rating: Int) {
        withContext(Dispatchers.IO) {
            messageDao.updateRating(messageId, rating)
        }
    }

    suspend fun toggleFavoriteMessage(messageId: String, currentFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            messageDao.updateFavorite(messageId, !currentFavorite)
        }
    }

    // --- إرسال الرسائل وتدفق الرد من Gemini ---

    fun streamAssistantResponse(
        conversationId: String,
        prompt: String,
        modelName: String,
        apiKey: String,
        systemInstruction: String,
        history: List<Message>,
        imageBase64: String? = null,
        enableSearchGrounding: Boolean = false,
        responseStyle: String = "detailed"
    ): Flow<StreamChunk> {
        val historyPairs = history.map {
            val role = if (it.sender == MessageSender.USER) "user" else "model"
            Pair(role, it.content)
        }
        return geminiService.streamChat(
            modelName = modelName,
            apiKey = apiKey,
            systemInstruction = systemInstruction,
            conversationHistory = historyPairs,
            currentPrompt = prompt,
            imageBase64 = imageBase64,
            enableSearchGrounding = enableSearchGrounding,
            responseStyle = responseStyle
        )
    }

    // --- استوديو الصور ---

    fun getAllGeneratedImages(): Flow<List<GeneratedImageItem>> {
        return imageDao.getAllImages().map { list ->
            list.map {
                GeneratedImageItem(
                    id = it.id,
                    originalPrompt = it.originalPrompt,
                    enhancedPrompt = it.enhancedPrompt,
                    style = it.style,
                    aspectRatio = it.aspectRatio,
                    filePath = it.filePath,
                    timestamp = it.timestamp
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun enhancePrompt(rawPrompt: String, apiKey: String): Result<String> {
        val instruction = """أنت خبير هندسة أوامر وتوليد صور احترافي.
حول فكرة المستخدم إلى برومبت دقيق جداً باللغة الإنجليزية لتوليد صور عالية الجودة.
حدد: تفاصيل الموضوع، الإضاءة (Cinematic, Soft, Volumetric)، العدسة (85mm, Macro, Wide angle)، الأسلوب الفني، الدقة والتفاصيل الدقيقة (Unreal Engine 5 render, highly detailed, photorealistic).
أرجع فقط البرومبت الإنجليزي المحسّن بدون أي مقدمات أو شروحات."""

        return geminiService.generateContentSingle(
            modelName = DevConstants.MODEL_FLASH,
            apiKey = apiKey,
            prompt = "حوّل هذه الفكرة لبرومبت صور فائق الدقة: $rawPrompt",
            systemInstruction = instruction
        )
    }

    suspend fun generateAndSaveImage(
        apiKey: String,
        originalPrompt: String,
        enhancedPrompt: String,
        style: String,
        aspectRatio: String
    ): Result<GeneratedImageItem> = withContext(Dispatchers.IO) {
        val fullPrompt = "$enhancedPrompt, in style of $style, ultra-detailed, 8k resolution"
        val result = geminiService.generateImage(apiKey, fullPrompt, aspectRatio)

        result.fold(
            onSuccess = { (mimeType, bytes) ->
                try {
                    val id = UUID.randomUUID().toString()
                    val extension = if (mimeType.contains("jpeg", true)) "jpg" else "png"
                    val imagesDir = File(context.filesDir, "generated_art").apply { mkdirs() }
                    val imageFile = File(imagesDir, "dev_img_$id.$extension")

                    FileOutputStream(imageFile).use { out ->
                        out.write(bytes)
                        out.flush()
                    }

                    val entity = GeneratedImageEntity(
                        id = id,
                        originalPrompt = originalPrompt,
                        enhancedPrompt = enhancedPrompt,
                        style = style,
                        aspectRatio = aspectRatio,
                        filePath = imageFile.absolutePath,
                        timestamp = System.currentTimeMillis()
                    )
                    imageDao.insertImage(entity)

                    Result.success(
                        GeneratedImageItem(
                            id = id,
                            originalPrompt = originalPrompt,
                            enhancedPrompt = enhancedPrompt,
                            style = style,
                            aspectRatio = aspectRatio,
                            filePath = imageFile.absolutePath,
                            timestamp = entity.timestamp
                        )
                    )
                } catch (e: Exception) {
                    Result.failure(Exception("فشل حفظ ملف الصورة: ${e.localizedMessage}"))
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    // --- الذاكرة طويلة المدى ---

    fun getAllMemoryItems(): Flow<List<MemoryItem>> {
        return memoryDao.getAllMemoryItems().map { list ->
            list.map { MemoryItem(it.id, it.topic, it.content, it.timestamp) }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addMemoryItem(topic: String, content: String) {
        withContext(Dispatchers.IO) {
            val entity = MemoryEntity(
                id = UUID.randomUUID().toString(),
                topic = topic,
                content = content,
                timestamp = System.currentTimeMillis()
            )
            memoryDao.insertMemoryItem(entity)
        }
    }

    suspend fun deleteMemoryItem(id: String) {
        withContext(Dispatchers.IO) {
            memoryDao.deleteMemoryItemById(id)
        }
    }

    suspend fun clearAllMemory() {
        withContext(Dispatchers.IO) {
            memoryDao.clearAllMemory()
        }
    }

    suspend fun getMemorySummaryForPrompt(): String {
        return withContext(Dispatchers.IO) {
            try {
                val items = memoryDao.getAllMemoryItems().first()
                if (items.isEmpty()) return@withContext ""
                val sb = StringBuilder("\nمعلومات وتفضيلات سابقة حفظها المستخدم في ذاكرتك:")
                items.forEach { sb.append("\n- [${it.topic}]: ${it.content}") }
                sb.toString()
            } catch (_: Exception) {
                ""
            }
        }
    }

    // --- الأوضاع والشخصيات (Personas) ---

    fun getBuiltInPersonas(): List<Persona> {
        return listOf(
            Persona(
                id = "general",
                name = "Dev General",
                nameAr = "Dev الافتراضي",
                descriptionAr = "مساعد ذكي شامل وفخم لكل المهام اليومية والتقنية",
                systemPrompt = DevConstants.SYSTEM_INSTRUCTION,
                iconKey = "sparkles",
                accentHex = 0xFF7C3AED
            ),
            Persona(
                id = "coder",
                name = "Code Master",
                nameAr = "المبرمج المحترف",
                descriptionAr = "خبير معماريات برمجية، كتابة كود نظيف وتصحيح الأخطاء",
                systemPrompt = DevConstants.SYSTEM_INSTRUCTION + "\nأنت الآن في وضع 'المبرمج المحترف'. ركز على أفضل الممارسات البرمجية، اكتب أكواد كاملة نظيفة مع تعليقات وشرح دقيق لكل خطوة ومراعاة الأداء والأمان.",
                iconKey = "code",
                accentHex = 0xFF06B6D4
            ),
            Persona(
                id = "math_genius",
                name = "Math Genius",
                nameAr = "عبقري الرياضيات والعلوم",
                descriptionAr = "حل المسائل الرياضية المعقدة، التفاضل والتكامل، الجبر والهندسة خطوة بخطوة",
                systemPrompt = DevConstants.SYSTEM_INSTRUCTION + "\nأنت في وضع 'عبقري الرياضيات والعلوم'. أنت مرجع عالمي دقيق في كافة فروع الرياضيات (الحساب، الجبر، التفاضل والتكامل، الهندسة، الإحصاء والاحتمالات، والفيزياء الرياضية). اشرح كل خطوة رياضية بدقة متناهية مع ذكر القوانين واستخدام الرموز والجداول لتوضيح الحل بدون أي خطأ حسابي.",
                iconKey = "math",
                accentHex = 0xFF3B82F6
            ),
            Persona(
                id = "translator",
                name = "Polyglot Linguist (50+ Languages)",
                nameAr = "المترجم الموسوعي (50+ لغة)",
                descriptionAr = "ترجمة احترافية فورية بين أكثر من 50 لغة عالمية وجميع اللهجات العربية",
                systemPrompt = DevConstants.SYSTEM_INSTRUCTION + "\nأنت في وضع 'المترجم الموسوعي (Polyglot)'. أنت تتقن أكثر من 50 لغة عالمية وجميع اللهجات العربية. قدّم ترجمة فائقة الدقة تراعي المعنى البلاغي والسياق الثقافي والبدائل الشائعة وطريقة النطق عند الطلب.",
                iconKey = "translate",
                accentHex = 0xFF10B981
            ),
            Persona(
                id = "writer",
                name = "Creative Author",
                nameAr = "الكاتب الإبداعي",
                descriptionAr = "صياغة مقالات، خطابات، إيميلات، ومحتوى جذاب ومؤثر",
                systemPrompt = DevConstants.SYSTEM_INSTRUCTION + "\nأنت في وضع 'الكاتب الإبداعي'. تميز بأسلوبك البليغ واللغة الرصينة والبناء المتقن للنصوص.",
                iconKey = "pen",
                accentHex = 0xFFEC4899
            ),
            Persona(
                id = "tutor",
                name = "Academic Tutor",
                nameAr = "المعلّم الأكاديمي",
                descriptionAr = "شرح مبسط للمفاهيم الصعبة مع أمثلة واختبارات تفاعلية",
                systemPrompt = DevConstants.SYSTEM_INSTRUCTION + "\nأنت في وضع 'المعلم الأكاديمي'. استخدم أسلوب فاينمان للتبسيط، واطرح أمثلة واقعية، وقدم اختبارات قصيرة لقياس الفهم.",
                iconKey = "school",
                accentHex = 0xFFF59E0B
            ),
            Persona(
                id = "analyst",
                name = "Data Analyst",
                nameAr = "محلل البيانات",
                descriptionAr = "تحليل الأرقام، استخلاص الرؤى، وتنسيق الجداول والبيانات",
                systemPrompt = DevConstants.SYSTEM_INSTRUCTION + "\nأنت في وضع 'محلل البيانات'. ركز على الدقة الرياضية، الجداول المنسقة، والتحليل المنطقي والرياضي.",
                iconKey = "analytics",
                accentHex = 0xFF8B5CF6
            ),
            Persona(
                id = "designer",
                name = "UI/UX Designer",
                nameAr = "خبير التصميم",
                descriptionAr = "تصميم واجهات المستخدم، تناغم الألوان، وتجربة الاستخدام",
                systemPrompt = DevConstants.SYSTEM_INSTRUCTION + "\nأنت في وضع 'خبير التصميم'. قدم أفكاراً عصرية في أنظمة التصميم وواجهات المستخدم وتنسيق الألوان وتجربة المستخدم.",
                iconKey = "palette",
                accentHex = 0xFFF97316
            ),
            Persona(
                id = "trainer",
                name = "Personal Coach",
                nameAr = "المدرب الشخصي",
                descriptionAr = "خطط تغذية ولياقة بدنية وعادات يومية متوازنة",
                systemPrompt = DevConstants.SYSTEM_INSTRUCTION + "\nأنت في وضع 'المدرب الشخصي'. قدم إرشادات رياضية وتغذوية متوازنة بأسلوب تشجيعي وإيجابي.",
                iconKey = "fitness",
                accentHex = 0xFF14B8A6
            )
        )
    }

    // --- أدوات ذكية سريعة (Smart Tools) ---

    suspend fun executeSmartTool(
        toolType: String,
        input: String,
        extraParam: String,
        apiKey: String
    ): Result<String> {
        val prompt = when (toolType) {
            "app_builder" -> """أنت مهندس معماري برمجيات فائق التطور والذكاء وخبير في إنشاء وبناء وتطوير تطبيقات أندرويد العالمية المكتملة (Kotlin + Jetpack Compose + Material 3) واستخراج ملفات الـ APK الجاهزة للتثبيت.
المطلوب بناء وتطوير تطبيق كامل بناءً على الوصف التالي:
$input

نوع التطبيق / التخصيص المطلوب: (${if (extraParam.isNotBlank()) extraParam else "تطبيق متكامل مع كود وAPK"})

قم بإنشاء وتطوير التطبيق بأعلى درجات الاحترافية والتفصيل، وقدم الدليل الشامل التالي:
1. 📱 **هوية التطبيق والمعمارية (Architecture & Tech Stack)**:
   - اسم الحزمة المقترح ومكتبات Gradle (Compose, Material 3, Navigation, Room, Coroutines Flow).
   - هيكلية المشروع وفق أفضل ممارسات Clean Architecture و MVVM.

2. 🎨 **نظام التصميم والواجهات (Design System & Jetpack Compose UI)**:
   - لوحة الألوان للوضعين الداكن والفاتح، والأيقونات، والشاشات الكاملة مع كود Compose كامل وجاهز للنسخ والتشغيل.

3. ⚙️ **منطق الأعمال والبيانات (ViewModel & Data Layer)**:
   - الكود الكامل لطبقة البيانات والحفظ المحلي وإدارة الحالة StateFlow بدون أي أكواد ناقصة.

4. 📦 **خطوات بناء وتحميل ملف APK النهائي (Build & Download APK)**:
   - الشرح الواضح والدقيق خطوة بخطوة لكيفية استخراج وبناء ملف الـ APK مباشرة من قائمة إعدادات المنصة (Settings Menu -> Export / Download APK) أو من خلال سطر أوامر Gradle (`gradle assembleDebug` أو `gradle assembleRelease`).
   - إرشادات التثبيت على الهاتف وتخطي أي تحذيرات وضمان عمل التطبيق بسلاسة تامة وبدون أي تعليق."""

            "translator" -> """أنت مترجم لغوي فوري موسوعي تتقن أكثر من 50 لغة عالمية بطلاقة.
ترجم النص التالي بدقة فائقة إلى ($extraParam):
$input

تنسيق الرد المطلوب:
1. **الترجمة الدقيقة والمعتمدة**
2. **طريقة النطق الصوتي (Phonetics)** إذا كانت اللغة غير عربية
3. **مرادفات وبدائل شائعة** لنفس المعنى
4. **شرح السياق الثقافي واللغوي** (رسمي، عامي، أو أدبي)"""

            "math" -> """أنت عبقري الرياضيات والعلوم الأول في العالم.
حل المسألة الرياضية التالية بأعلى درجات الدقة والكمال الرياضي:
$input

تنسيق الرد المطلوب:
1. **الهدف والبيانات المعطاة والمطلوب إيجاده**
2. **القوانين والنظريات الرياضية المستخدمة**
3. **خطوات الحل التفصيلية خطوة بخطوة مع توضيح العمليات الحسابية بدقة**
4. **الناتج النهائي المبسط والمؤكد**
5. **التحقق من صحة الحل (Verification Check)**"""

            "ocr" -> "قم باستخراج وفهرسة وتوضيح كل النصوص والبيانات والرسائل والمعادلات الموجودة في هذا المحتوى:\n$input"
            "summarizer" -> "قم بتلخيص المحتوى التالي في 3 أقسام: ملخص موجز في سطرين، أهم 5 نقاط رئيسية، وأهم الاستنتاجات:\n$input"
            "writer" -> "أعد صياغة هذا النص بالأسلوب أو النبرة ($extraParam) مع التدقيق اللغوي الكامل:\n$input"
            "code" -> "افحص الكود التالي بلغة ($extraParam)، أصلح أي أخطاء، اشرح الحل بوضوح، واقترح تحسينات في الأداء:\n$input"
            "prompt" -> "حوّل الفكرة التالية إلى برومبت احترافي فائق الدقة والقوة لاستخدامه في نماذج الذكاء الاصطناعي:\n$input"
            "tutor" -> "اشرح هذا المفهوم بمستوى ($extraParam)، مع مثال توضيحي واقعي و3 أسئلة اختبار ذاتية مع الإجابات:\n$input"
            else -> input
        }

        return geminiService.generateContentSingle(
            modelName = if (toolType == "math") DevConstants.MODEL_PRO else DevConstants.MODEL_FLASH,
            apiKey = apiKey,
            prompt = prompt
        )
    }

    // Extension Mappers
    private fun ConversationEntity.toDomain() = Conversation(
        id = id,
        title = title,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isPinned = isPinned,
        isFavorite = isFavorite,
        personaId = personaId
    )

    private fun MessageEntity.toDomain(): Message {
        val sourcesList = mutableListOf<GroundingSource>()
        if (!sourcesJson.isNullOrBlank()) {
            try {
                val array = JSONArray(sourcesJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    sourcesList.add(GroundingSource(obj.optString("title"), obj.optString("url")))
                }
            } catch (_: Exception) {}
        }

        return Message(
            id = id,
            conversationId = conversationId,
            sender = if (sender == "USER") MessageSender.USER else MessageSender.ASSISTANT,
            content = content,
            timestamp = timestamp,
            isStreaming = false,
            imageBase64 = imageBase64,
            imageUri = imageUri,
            attachmentName = attachmentName,
            rating = rating,
            isFavorite = isFavorite,
            groundingSources = sourcesList
        )
    }

    private fun Message.toEntity(): MessageEntity {
        val sourcesJsonStr = if (groundingSources.isNotEmpty()) {
            val arr = JSONArray()
            groundingSources.forEach {
                arr.put(JSONObject().apply {
                    put("title", it.title)
                    put("url", it.url)
                })
            }
            arr.toString()
        } else null

        return MessageEntity(
            id = id,
            conversationId = conversationId,
            sender = if (sender == MessageSender.USER) "USER" else "ASSISTANT",
            content = content,
            timestamp = timestamp,
            imageBase64 = imageBase64,
            imageUri = imageUri,
            attachmentName = attachmentName,
            rating = rating,
            isFavorite = isFavorite,
            sourcesJson = sourcesJsonStr
        )
    }
}
