package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entities.kt
 * جداول قاعدة بيانات Room لتخزين المحادثات، الرسائل، الصور المولّدة، والذاكرة طويلة المدى.
 * جميع العمليات محلية 100% لضمان أقصى درجات الخصوصية والسرعة.
 */

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isPinned: Boolean,
    val isFavorite: Boolean,
    val personaId: String
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val sender: String, // "USER" or "ASSISTANT"
    val content: String,
    val timestamp: Long,
    val imageBase64: String?,
    val imageUri: String?,
    val attachmentName: String?,
    val rating: Int,
    val isFavorite: Boolean,
    val sourcesJson: String? // Serialized JSON list of GroundingSource
)

@Entity(tableName = "memory_items")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val topic: String,
    val content: String,
    val timestamp: Long
)

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey val id: String,
    val originalPrompt: String,
    val enhancedPrompt: String,
    val style: String,
    val aspectRatio: String,
    val filePath: String,
    val timestamp: Long
)
