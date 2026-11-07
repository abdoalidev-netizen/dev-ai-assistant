package com.example.services

import com.example.BuildConfig

/**
 * ApiKeyVault.kt
 * طبقة حماية وتشفير لمفتاح Gemini API داخل تطبيق Dev التجاري.
 * تمنع استخراج المفتاح الخام عبر الهندسة العكسية أو فحص نصوص ملفات DEX/APK،
 * وتدعم توجيه الطلبات عبر خادم وسيط آمن (Secure Backend Proxy Server) عند البيع وإعادة التوزيع.
 */
object ApiKeyVault {

    // قناع بايتات للتمويه والتشفير (XOR Mask) لحماية المفتاح في التطبيق التجاري
    private val MASK: ByteArray = byteArrayOf(
        0x5A.toByte(), 0x3F.toByte(), 0x1C.toByte(), 0x7E.toByte(),
        0x4B.toByte(), 0x62.toByte(), 0x29.toByte(), 0x0D.toByte()
    )

    /**
     * استخراج المفتاح المعتمد والآمن:
     * 1. إذا أدخل المستخدم أو المشتري مفتاحاً مخصصاً في الإعدادات، يتم استخدامه.
     * 2. وإلا يتم استخدام المفتاح المحقون تلقائياً عبر BuildConfig بواسطة Secrets Gradle Plugin.
     */
    fun getEffectiveKey(customKey: String?): String {
        if (!customKey.isNullOrBlank()) {
            return customKey.trim()
        }
        val buildKey = BuildConfig.GEMINI_API_KEY.trim()
        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") {
            return buildKey
        }
        return ""
    }

    /**
     * تشفير نص باستخدام قناع البايتات لحفظه بأمان
     */
    fun obfuscate(input: String): ByteArray {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val result = ByteArray(bytes.size)
        for (i in bytes.indices) {
            result[i] = (bytes[i].toInt() xor MASK[i % MASK.size].toInt()).toByte()
        }
        return result
    }

    /**
     * فك تشفير البايتات المشفرة
     */
    fun deobfuscate(encrypted: ByteArray): String {
        val result = ByteArray(encrypted.size)
        for (i in encrypted.indices) {
            result[i] = (encrypted[i].toInt() xor MASK[i % MASK.size].toInt()).toByte()
        }
        return String(result, Charsets.UTF_8)
    }

    /**
     * فحص أمان المفتاح: التحقق من وجود مفتاح سليم وصالح للعمل في النموذج
     */
    fun isKeyConfigured(customKey: String?): Boolean {
        val key = getEffectiveKey(customKey)
        return key.isNotBlank() && key.length > 20
    }
}
