package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.constants.DevConstants
import com.example.domain.model.GroundingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * GeminiService.kt
 * طبقة الاتصال الرسمية والموحدة بنماذج Google Gemini عبر REST API و Server-Sent Events (SSE).
 *
 * الميزات:
 * 1. دعم التوليد التدفقي الحي (Streaming) حرفاً بحرف.
 * 2. دعم النماذج المعتمدة: gemini-3.5-flash و gemini-3.1-pro-preview.
 * 3. دعم نموذج الصور gemini-2.5-flash-image وتعديل الصور.
 * 4. دعم البحث المباشر في الويب (Google Search Grounding) واستخراج المصادر.
 * 5. معالجة متقدمة للوسائط المتعددة (Multimodal): نصوص + صور Base64.
 * 6. طبقة معزولة يسهل توجيهها لسيرفر وسيط (Proxy Server) لاحقاً عند الحاجة.
 */
class GeminiService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
) {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * الحصول على مفتاح الـ API الفعال والآمن: إما المفتاح المخصص أو المشفر المحقون
     */
    fun resolveApiKey(userKey: String?): String {
        return com.example.services.ApiKeyVault.getEffectiveKey(userKey)
    }

    /**
     * إرسال طلب تدفقي (Streaming) وتلقي المقاطع لحظياً عبر Kotlin Flow.
     * يدعم نصوص المحادثة السابقة، الصور المرفقة، البحث الحي، والتوجيهات المخصصة.
     */
    fun streamChat(
        modelName: String,
        apiKey: String,
        systemInstruction: String,
        conversationHistory: List<Pair<String, String>>, // sender ("user"/"model") to text
        currentPrompt: String,
        imageBase64: String? = null,
        enableSearchGrounding: Boolean = false,
        responseStyle: String = "detailed"
    ): Flow<StreamChunk> = callbackFlow {
        val requestedModel = when {
            modelName.isNotBlank() && (modelName == DevConstants.MODEL_FLASH || modelName == DevConstants.MODEL_PRO) -> modelName
            else -> DevConstants.MODEL_FLASH
        }
        val candidateModels = listOf(requestedModel, DevConstants.MODEL_FLASH).distinct()

        var currentCall: okhttp3.Call? = null
        var streamedAnyText = false

        for (model in candidateModels) {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:streamGenerateContent?alt=sse&key=$apiKey"

            val requestJson = JSONObject()

            // 1. System Instruction
            val sysInstructionObj = JSONObject().apply {
                val partsArr = JSONArray().apply {
                    var finalSysText = systemInstruction
                    if (responseStyle == "short") {
                        finalSysText += "\nتنبيه مهم: يفضل المستخدم الردود المختصرة والمباشرة والمركزة جداً بدون إطناب."
                    }
                    put(JSONObject().put("text", finalSysText))
                }
                put("parts", partsArr)
            }
            requestJson.put("systemInstruction", sysInstructionObj)

            // 2. Contents History & Current Turn
            val contentsArr = JSONArray()

            for ((sender, text) in conversationHistory) {
                val role = if (sender.equals("user", ignoreCase = true)) "user" else "model"
                val turnObj = JSONObject().apply {
                    put("role", role)
                    val parts = JSONArray().apply {
                        put(JSONObject().put("text", text))
                    }
                    put("parts", parts)
                }
                contentsArr.put(turnObj)
            }

            // Current User Message
            val currentUserObj = JSONObject().apply {
                put("role", "user")
                val parts = JSONArray().apply {
                    if (!imageBase64.isNullOrBlank()) {
                        val inlineDataObj = JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", imageBase64)
                        }
                        put(JSONObject().put("inlineData", inlineDataObj))
                    }
                    put(JSONObject().put("text", currentPrompt))
                }
                put("parts", parts)
            }
            contentsArr.put(currentUserObj)
            requestJson.put("contents", contentsArr)

            // 3. Google Search Grounding Tool
            if (enableSearchGrounding) {
                val toolsArr = JSONArray().apply {
                    val googleSearchTool = JSONObject().apply {
                        put("googleSearch", JSONObject())
                    }
                    put(googleSearchTool)
                }
                requestJson.put("tools", toolsArr)
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val call = client.newCall(request)
            currentCall = call

            try {
                val response: Response = call.execute()
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string().orEmpty()
                    response.close()
                    if (model != candidateModels.last()) {
                        continue
                    }
                    val friendlyError = parseErrorMessage(response.code, errorBody)
                    trySend(StreamChunk.Error(friendlyError))
                    close()
                    return@callbackFlow
                }

                val source = response.body?.source()
                if (source == null) {
                    response.close()
                    if (model != candidateModels.last()) {
                        continue
                    }
                    trySend(StreamChunk.Error("لم يتم استلام أي بيانات من خوادم الذكاء الاصطناعي."))
                    close()
                    return@callbackFlow
                }

                val extractedSources = mutableListOf<GroundingSource>()

                while (!source.exhausted()) {
                    val line = source.readUtf8Line() ?: break
                    val trimmed = line.trim()
                    if (trimmed.startsWith("data:")) {
                        val jsonPayload = trimmed.removePrefix("data:").trim()
                        if (jsonPayload == "[DONE]" || jsonPayload.isEmpty()) continue

                        try {
                            val chunkObj = JSONObject(jsonPayload)
                            val candidates = chunkObj.optJSONArray("candidates")
                            if (candidates != null && candidates.length() > 0) {
                                val candidate = candidates.getJSONObject(0)
                                val content = candidate.optJSONObject("content")
                                if (content != null) {
                                    val parts = content.optJSONArray("parts")
                                    if (parts != null) {
                                        for (i in 0 until parts.length()) {
                                            val part = parts.getJSONObject(i)
                                            val text = part.optString("text")
                                            if (text.isNotEmpty()) {
                                                streamedAnyText = true
                                                trySend(StreamChunk.Text(text))
                                            }
                                        }
                                    }
                                }

                                // Grounding sources extraction
                                val groundingMetadata = candidate.optJSONObject("groundingMetadata")
                                if (groundingMetadata != null) {
                                    val chunks = groundingMetadata.optJSONArray("groundingChunks")
                                    if (chunks != null) {
                                        for (k in 0 until chunks.length()) {
                                            val chunk = chunks.getJSONObject(k)
                                            val web = chunk.optJSONObject("web")
                                            if (web != null) {
                                                val title = web.optString("title", "مصدر ويب")
                                                val uri = web.optString("uri", "")
                                                if (uri.isNotEmpty() && extractedSources.none { it.url == uri }) {
                                                    val src = GroundingSource(title = title, url = uri)
                                                    extractedSources.add(src)
                                                    trySend(StreamChunk.Source(src))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (_: Exception) {
                            // Skip malformed chunks
                        }
                    }
                }
                response.close()
                if (streamedAnyText) {
                    trySend(StreamChunk.Done(extractedSources))
                    close()
                    return@callbackFlow
                } else if (model != candidateModels.last()) {
                    continue
                } else {
                    trySend(StreamChunk.Done(extractedSources))
                    close()
                    return@callbackFlow
                }
            } catch (e: IOException) {
                if (!streamedAnyText && model != candidateModels.last()) {
                    continue
                }
                val errorMsg = e.localizedMessage?.takeIf { it.isNotBlank() } ?: e.message?.takeIf { it.isNotBlank() } ?: "يرجى التأكد من اتصال الإنترنت والمحاولة مجدداً"
                trySend(StreamChunk.Error("انقطع الاتصال: $errorMsg"))
                close()
                return@callbackFlow
            } catch (e: Exception) {
                if (!streamedAnyText && model != candidateModels.last()) {
                    continue
                }
                val errorMsg = e.localizedMessage?.takeIf { it.isNotBlank() } ?: e.message?.takeIf { it.isNotBlank() } ?: "تعذر إكمال المعالجة، يرجى إعادة المحاولة"
                trySend(StreamChunk.Error("حدث خطأ أثناء المعالجة: $errorMsg"))
                close()
                return@callbackFlow
            }
        }

        awaitClose {
            currentCall?.cancel()
        }
    }

    /**
     * استدعاء أحادي سريع (Non-streaming) لتوليد النصوص، تحسين البرومبت، أو تلخيص الملفات
     */
    suspend fun generateContentSingle(
        modelName: String,
        apiKey: String,
        prompt: String,
        systemInstruction: String = DevConstants.SYSTEM_INSTRUCTION
    ): Result<String> = withContext(Dispatchers.IO) {
        val requestedModel = when {
            modelName.isNotBlank() && (modelName == DevConstants.MODEL_FLASH || modelName == DevConstants.MODEL_PRO) -> modelName
            else -> DevConstants.MODEL_FLASH
        }
        val candidateModels = listOf(requestedModel, DevConstants.MODEL_FLASH).distinct()

        for (model in candidateModels) {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

            val requestJson = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val bodyStr = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        if (model != candidateModels.last()) return@use
                        return@withContext Result.failure(Exception(parseErrorMessage(response.code, bodyStr)))
                    }
                    val json = JSONObject(bodyStr)
                    val text = json.optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")

                    if (!text.isNullOrBlank()) {
                        return@withContext Result.success(text)
                    } else if (model == candidateModels.last()) {
                        return@withContext Result.failure(Exception("لم يرجع النموذج أي رد، يرجى المحاولة مجدداً."))
                    }
                }
            } catch (e: Exception) {
                if (model == candidateModels.last()) {
                    val errorMsg = e.localizedMessage?.takeIf { it.isNotBlank() } ?: e.message?.takeIf { it.isNotBlank() } ?: "تعذر الاتصال بالخادم"
                    return@withContext Result.failure(Exception("خطأ في الاتصال: $errorMsg"))
                }
            }
        }
        Result.failure(Exception("تعذر استلام الرد من الذكاء الاصطناعي."))
    }

    /**
     * توليد صورة عبر نموذج Gemini المخصص للصور (gemini-2.5-flash-image)
     */
    suspend fun generateImage(
        apiKey: String,
        prompt: String,
        aspectRatio: String = "1:1"
    ): Result<Pair<String, ByteArray>> = withContext(Dispatchers.IO) {
        val model = DevConstants.MODEL_IMAGE
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseModalities", JSONArray().apply {
                    put("TEXT")
                    put("IMAGE")
                })
                put("imageConfig", JSONObject().apply {
                    put("aspectRatio", aspectRatio)
                    put("imageSize", "1K")
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(requestJson.toString().toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception(parseErrorMessage(response.code, bodyStr)))
                }

                val json = JSONObject(bodyStr)
                val candidates = json.optJSONArray("candidates")
                val parts = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")

                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        val inlineData = part.optJSONObject("inlineData")
                        if (inlineData != null) {
                            val mimeType = inlineData.optString("mimeType", "image/png")
                            val dataBase64 = inlineData.optString("data")
                            if (dataBase64.isNotEmpty()) {
                                val imageBytes = Base64.decode(dataBase64, Base64.DEFAULT)
                                return@withContext Result.success(Pair(mimeType, imageBytes))
                            }
                        }
                    }
                }
                Result.failure(Exception("لم يقم النموذج بتوليد صورة للوصف المدخل. يرجى تجربة وصف آخر."))
            }
        } catch (e: Exception) {
            val err = e.localizedMessage?.takeIf { it.isNotBlank() } ?: e.message?.takeIf { it.isNotBlank() } ?: "تعذر إكمال توليد الصورة"
            Result.failure(Exception("خطأ أثناء توليد الصورة: $err"))
        }
    }

    private fun parseErrorMessage(code: Int, body: String): String {
        return try {
            val json = JSONObject(body)
            val errorObj = json.optJSONObject("error")
            val message = errorObj?.optString("message").orEmpty()
            when (code) {
                400 -> "خطأ في صياغة الطلب (400): ${if (message.isNotBlank()) message else "الرجاء التأكد من صحة المدخلات"}"
                403 -> "مفتاح API غير صالح أو غير مفعّل (403). يرجى التأكد من المفتاح في الإعدادات."
                404 -> "المحرك المطلوب غير متاح حالياً، يرجى اختيار Gemini Flash."
                429 -> "تم تجاوز حد الاستخدام المسموح مؤقتاً (429). خوادم Gemini مشغولة، يرجى المحاولة بعد لحظات."
                500, 502, 503, 504 -> "خوادم Gemini تحت الضغط حالياً (رمز $code). يرجى المحاولة بعد قليل."
                else -> if (message.isNotBlank()) "خطأ من الخادم ($code): $message" else "تعذر إكمال الطلب (رمز الخطأ: $code)"
            }
        } catch (_: Exception) {
            when (code) {
                429 -> "تم تجاوز حد الاستخدام مؤقتاً، يرجى المحاولة بعد لحظات."
                404 -> "المحرك غير متاح حالياً."
                else -> "فشل الطلب مع رمز استجابة ($code)"
            }
        }
    }
}

sealed class StreamChunk {
    data class Text(val content: String) : StreamChunk()
    data class Source(val source: GroundingSource) : StreamChunk()
    data class Done(val sources: List<GroundingSource>) : StreamChunk()
    data class Error(val message: String) : StreamChunk()
}

fun Bitmap.toBase64Jpeg(quality: Int = 85): String {
    val outputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}
