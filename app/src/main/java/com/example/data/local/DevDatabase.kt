package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * DevDatabase.kt
 * قاعدة البيانات الرئيسية لتطبيق Dev المشيدة بمكتبة Room.
 * تحتوي على جداول المحادثات والرسائل والذاكرة والصور وتدير النسخ والتهيئة.
 */

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        MemoryEntity::class,
        GeneratedImageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DevDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var INSTANCE: DevDatabase? = null

        fun getInstance(context: Context): DevDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DevDatabase::class.java,
                    "dev_luxury_assistant.db"
                ).fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
