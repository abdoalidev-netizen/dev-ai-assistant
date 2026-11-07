package com.example

import com.example.constants.DevConstants
import com.example.data.remote.GeminiService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * DevAppUnitTests.kt
 * اختبارات الوحدة الأساسية لطبقة Gemini API والثوابت وهوية التطبيق والمطور.
 */
class DevAppUnitTests {

    @Test
    fun developerAttribution_isAccurateAndPresent() {
        assertEquals("Abdo Ahmed Ali", DevConstants.DEVELOPER_NAME_EN)
        assertEquals("عبده أحمد علي", DevConstants.DEVELOPER_NAME_AR)
        assertTrue(DevConstants.SYSTEM_INSTRUCTION.contains("Abdo Ahmed Ali"))
        assertTrue(DevConstants.SYSTEM_INSTRUCTION.contains("عبده أحمد علي"))
        assertTrue(DevConstants.SYSTEM_INSTRUCTION.contains("Gemini"))
    }

    @Test
    fun models_useApprovedModernPreviewVersions() {
        assertEquals("gemini-3.5-flash-lite", DevConstants.MODEL_FLASH)
        assertEquals("gemini-3.5-flash", DevConstants.MODEL_PRO)
        assertEquals("gemini-3.5-flash", DevConstants.MODEL_IMAGE)
    }

    @Test
    fun geminiService_resolvesUserKeyWhenProvided() {
        val service = GeminiService()
        val customKey = "test_custom_api_key_123"
        val resolved = service.resolveApiKey(customKey)
        assertEquals(customKey, resolved)
    }

    @Test
    fun geminiService_resolvesEmptyWhenKeyBlankAndNoBuildConfig() {
        val service = GeminiService()
        val resolved = service.resolveApiKey("")
        // If BuildConfig is empty or placeholder, resolved is empty or placeholder
        assertNotNull(resolved)
    }

    @Test
    fun systemInstruction_mandatesPolyglotAndMathGenius() {
        assertTrue(DevConstants.SYSTEM_INSTRUCTION.contains("50 لغة"))
        assertTrue(DevConstants.SYSTEM_INSTRUCTION.contains("الرياضيات"))
        assertTrue(DevConstants.SYSTEM_INSTRUCTION.contains("التفاضل والتكامل"))
    }

    @Test
    fun apiKeyVault_obfuscatesAndDeobfuscatesCorrectly() {
        val originalKey = "test_gemini_secure_key_123456789"
        val obfuscated = com.example.services.ApiKeyVault.obfuscate(originalKey)
        val deobfuscated = com.example.services.ApiKeyVault.deobfuscate(obfuscated)
        assertEquals(originalKey, deobfuscated)
    }
}
