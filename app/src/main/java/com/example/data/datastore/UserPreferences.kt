package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.constants.DevConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * UserPreferences.kt
 * طبقة إدارة إعدادات وتفضيلات المستخدم باستخدام Jetpack DataStore Preferences.
 * تضمن حفظ واسترجاع حالة التطبيق (المظهر، اللغة، حجم الخط، النموذج المختار، إلخ) بسلاسة وأمان.
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dev_user_preferences")

data class DevPreferences(
    val userName: String = "",
    val userEmail: String = "",
    val userPhotoUrl: String = "",
    val isLoggedIn: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val selectedModel: String = DevConstants.MODEL_FLASH,
    val themeMode: String = "dark", // "dark", "light", "system"
    val language: String = "ar", // "ar", "en"
    val aiLanguage: String = "auto", // "auto", "ar", "en", "fr", "es", "de", "tr", "ru", "zh", "ja"
    val fontScale: Float = 1.0f,
    val ttsSpeed: Float = 1.0f,
    val hapticEnabled: Boolean = true,
    val longTermMemoryEnabled: Boolean = true,
    val searchGroundingEnabled: Boolean = false,
    val responseStyle: String = "detailed", // "detailed", "short"
    val customApiKey: String = "",
    val proxyServerUrl: String = ""
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val SELECTED_MODEL = stringPreferencesKey("selected_model")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val AI_LANGUAGE = stringPreferencesKey("ai_language")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val TTS_SPEED = floatPreferencesKey("tts_speed")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val MEMORY_ENABLED = booleanPreferencesKey("memory_enabled")
        val SEARCH_GROUNDING = booleanPreferencesKey("search_grounding")
        val RESPONSE_STYLE = stringPreferencesKey("response_style")
        val CUSTOM_API_KEY = stringPreferencesKey("custom_api_key")
        val PROXY_SERVER_URL = stringPreferencesKey("proxy_server_url")
    }

    val preferencesFlow: Flow<DevPreferences> = context.dataStore.data.map { prefs ->
        DevPreferences(
            userName = prefs[PreferencesKeys.USER_NAME] ?: "",
            userEmail = prefs[PreferencesKeys.USER_EMAIL] ?: "",
            userPhotoUrl = prefs[PreferencesKeys.USER_PHOTO_URL] ?: "",
            isLoggedIn = prefs[PreferencesKeys.IS_LOGGED_IN] ?: false,
            onboardingCompleted = prefs[PreferencesKeys.ONBOARDING_COMPLETED] ?: false,
            selectedModel = when (val saved = prefs[PreferencesKeys.SELECTED_MODEL]) {
                DevConstants.MODEL_FLASH, DevConstants.MODEL_PRO -> saved
                else -> DevConstants.MODEL_FLASH
            },
            themeMode = prefs[PreferencesKeys.THEME_MODE] ?: "dark",
            language = prefs[PreferencesKeys.LANGUAGE] ?: "ar",
            aiLanguage = prefs[PreferencesKeys.AI_LANGUAGE] ?: "auto",
            fontScale = prefs[PreferencesKeys.FONT_SCALE] ?: 1.0f,
            ttsSpeed = prefs[PreferencesKeys.TTS_SPEED] ?: 1.0f,
            hapticEnabled = prefs[PreferencesKeys.HAPTIC_ENABLED] ?: true,
            longTermMemoryEnabled = prefs[PreferencesKeys.MEMORY_ENABLED] ?: true,
            searchGroundingEnabled = prefs[PreferencesKeys.SEARCH_GROUNDING] ?: false,
            responseStyle = prefs[PreferencesKeys.RESPONSE_STYLE] ?: "detailed",
            customApiKey = prefs[PreferencesKeys.CUSTOM_API_KEY] ?: "",
            proxyServerUrl = prefs[PreferencesKeys.PROXY_SERVER_URL] ?: ""
        )
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[PreferencesKeys.USER_NAME] = name }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setSelectedModel(model: String) {
        context.dataStore.edit { it[PreferencesKeys.SELECTED_MODEL] = model }
    }

    suspend fun setThemeMode(theme: String) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = theme }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[PreferencesKeys.LANGUAGE] = lang }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { it[PreferencesKeys.FONT_SCALE] = scale }
    }

    suspend fun setTtsSpeed(speed: Float) {
        context.dataStore.edit { it[PreferencesKeys.TTS_SPEED] = speed }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTIC_ENABLED] = enabled }
    }

    suspend fun setMemoryEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.MEMORY_ENABLED] = enabled }
    }

    suspend fun setSearchGrounding(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SEARCH_GROUNDING] = enabled }
    }

    suspend fun setResponseStyle(style: String) {
        context.dataStore.edit { it[PreferencesKeys.RESPONSE_STYLE] = style }
    }

    suspend fun setCustomApiKey(key: String) {
        context.dataStore.edit { it[PreferencesKeys.CUSTOM_API_KEY] = key }
    }

    suspend fun setProxyServerUrl(url: String) {
        context.dataStore.edit { it[PreferencesKeys.PROXY_SERVER_URL] = url }
    }

    suspend fun setAiLanguage(aiLang: String) {
        context.dataStore.edit { it[PreferencesKeys.AI_LANGUAGE] = aiLang }
    }

    suspend fun loginUser(name: String, email: String, photoUrl: String = "") {
        context.dataStore.edit {
            it[PreferencesKeys.USER_NAME] = name
            it[PreferencesKeys.USER_EMAIL] = email
            it[PreferencesKeys.USER_PHOTO_URL] = photoUrl
            it[PreferencesKeys.IS_LOGGED_IN] = true
            it[PreferencesKeys.ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun logoutUser() {
        context.dataStore.edit {
            it[PreferencesKeys.IS_LOGGED_IN] = false
            it[PreferencesKeys.USER_NAME] = ""
            it[PreferencesKeys.USER_EMAIL] = ""
            it[PreferencesKeys.USER_PHOTO_URL] = ""
        }
    }
}
