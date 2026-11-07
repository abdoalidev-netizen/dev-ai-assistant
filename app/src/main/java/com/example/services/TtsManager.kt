package com.example.services

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * TtsManager.kt
 * مشغل تحويل النص إلى كلام (Text-to-Speech) لدعم الاستماع لردود Dev الصوتية بالعربية والإنجليزية.
 */
class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setSpeechRate(1.0f)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlaying.value = true
                    _currentUtteranceId.value = utteranceId
                }

                override fun onDone(utteranceId: String?) {
                    _isPlaying.value = false
                    _currentUtteranceId.value = null
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                    _currentUtteranceId.value = null
                }
            })
        }
    }

    fun speak(text: String, utteranceId: String, speechRate: Float = 1.0f) {
        if (!isInitialized || tts == null) return

        stop()

        tts?.setSpeechRate(speechRate)

        // تحديد اللغة بناءً على محتوى النص (عربي أم إنجليزي)
        val hasArabic = text.any { it in '\u0600'..'\u06FF' }
        if (hasArabic) {
            val arLocale = Locale.forLanguageTag("ar")
            val result = tts?.setLanguage(arLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.getDefault()
            }
        } else {
            tts?.language = Locale.US
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        _isPlaying.value = true
        _currentUtteranceId.value = utteranceId
    }

    fun stop() {
        if (tts != null && isInitialized) {
            tts?.stop()
        }
        _isPlaying.value = false
        _currentUtteranceId.value = null
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
