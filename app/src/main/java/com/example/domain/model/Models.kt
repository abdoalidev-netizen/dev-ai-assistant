package com.example.domain.model

/**
 * Models.kt
 * نماذج البيانات الأساسية (Domain Models) الخاصة بتطبيق Dev.
 * تُستخدم هذه النماذج عبر طبقات الـ UI والـ ViewModel والـ Repository لتمثيل البيانات بشكل نقي.
 */

data class Conversation(
    val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val personaId: String = "general"
)

data class GroundingSource(
    val title: String,
    val url: String
)

data class Message(
    val id: String,
    val conversationId: String,
    val sender: MessageSender,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val imageBase64: String? = null,
    val imageUri: String? = null,
    val attachmentName: String? = null,
    val rating: Int = 0, // 1 for like, -1 for dislike, 0 for none
    val isFavorite: Boolean = false,
    val groundingSources: List<GroundingSource> = emptyList()
)

enum class MessageSender {
    USER,
    ASSISTANT
}

data class Persona(
    val id: String,
    val name: String,
    val nameAr: String,
    val descriptionAr: String,
    val systemPrompt: String,
    val iconKey: String,
    val accentHex: Long,
    val isCustom: Boolean = false
)

data class GeneratedImageItem(
    val id: String,
    val originalPrompt: String,
    val enhancedPrompt: String,
    val style: String,
    val aspectRatio: String,
    val filePath: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MemoryItem(
    val id: String,
    val topic: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DeviceActionRequest(
    val actionType: DeviceActionType,
    val title: String,
    val description: String,
    val params: Map<String, String> = emptyMap()
)

enum class DeviceActionType {
    SET_ALARM,
    SET_TIMER,
    ADD_CALENDAR_EVENT,
    SEND_EMAIL,
    OPEN_URL,
    SHARE_TEXT
}
