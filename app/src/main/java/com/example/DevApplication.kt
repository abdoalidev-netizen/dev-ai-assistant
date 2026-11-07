package com.example

import android.app.Application
import com.example.data.datastore.UserPreferencesRepository
import com.example.data.local.DevDatabase
import com.example.data.remote.GeminiService
import com.example.data.repository.DevRepository
import com.example.services.DeviceActionExecutor
import com.example.services.ExportManager
import com.example.services.TtsManager

/**
 * DevApplication.kt
 * فئة التطبيق المركزية (Application class).
 * مسؤولة عن تهيئة المكونات العامة وقاعدة بيانات Room وخدمات النظام عند إقلاع التطبيق.
 */
class DevApplication : Application() {

    lateinit var database: DevDatabase
        private set

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var geminiService: GeminiService
        private set

    lateinit var repository: DevRepository
        private set

    lateinit var ttsManager: TtsManager
        private set

    lateinit var exportManager: ExportManager
        private set

    lateinit var deviceActionExecutor: DeviceActionExecutor
        private set

    override fun onCreate() {
        super.onCreate()

        database = DevDatabase.getInstance(this)
        preferencesRepository = UserPreferencesRepository(this)
        geminiService = GeminiService()

        repository = DevRepository(
            context = this,
            conversationDao = database.conversationDao(),
            messageDao = database.messageDao(),
            memoryDao = database.memoryDao(),
            imageDao = database.imageDao(),
            geminiService = geminiService
        )

        ttsManager = TtsManager(this)
        exportManager = ExportManager(this)
        deviceActionExecutor = DeviceActionExecutor(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ttsManager.shutdown()
    }
}
