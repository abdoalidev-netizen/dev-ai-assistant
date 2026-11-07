package com.example.services

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.constants.DevConstants
import com.example.domain.model.Conversation
import com.example.domain.model.Message
import com.example.domain.model.MessageSender
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ExportManager.kt
 * مدير تصدير المحادثات والملفات بتنسيقات متعددة: Markdown, TXT, CSV, PDF Text.
 * يتيح مشاركة وتصدير المحادثات محلياً بدون إرسال البيانات لأي خوادم خارجية.
 */
class ExportManager(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun exportAndShare(
        conversation: Conversation,
        messages: List<Message>,
        format: ExportFormat
    ): Intent? {
        val file = generateExportFile(conversation, messages, format) ?: return null
        val authority = "${context.packageName}.fileprovider"
        val contentUri = FileProvider.getUriForFile(context, authority, file)

        return Intent(Intent.ACTION_SEND).apply {
            type = when (format) {
                ExportFormat.MARKDOWN -> "text/markdown"
                ExportFormat.TXT -> "text/plain"
                ExportFormat.CSV -> "text/csv"
                ExportFormat.PDF -> "application/pdf"
            }
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "تصدير محادثة Dev: ${conversation.title}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun generateExportFile(
        conversation: Conversation,
        messages: List<Message>,
        format: ExportFormat
    ): File? {
        try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val cleanTitle = conversation.title.replace("[^a-zA-Z0-9ء-ي]".toRegex(), "_").take(25)
            val fileName = "Dev_Chat_${cleanTitle}_${System.currentTimeMillis()}.${format.extension}"
            val file = File(exportDir, fileName)

            val content = when (format) {
                ExportFormat.MARKDOWN -> buildMarkdownContent(conversation, messages)
                ExportFormat.TXT -> buildPlainTextContent(conversation, messages)
                ExportFormat.CSV -> buildCsvContent(conversation, messages)
                ExportFormat.PDF -> buildMarkdownContent(conversation, messages) // Text structured for print/pdf
            }

            FileOutputStream(file).use { out ->
                out.write(content.toByteArray(Charsets.UTF_8))
                out.flush()
            }
            return file
        } catch (_: Exception) {
            return null
        }
    }

    private fun buildMarkdownContent(conversation: Conversation, messages: List<Message>): String {
        val sb = StringBuilder()
        sb.append("# ${conversation.title}\n\n")
        sb.append("> تم التصدير عبر تطبيق Dev المطور بواسطة ${DevConstants.DEVELOPER_FULL}\n")
        sb.append("> التاريخ: ${dateFormat.format(Date(conversation.createdAt))}\n\n")
        sb.append("---\n\n")

        for (msg in messages) {
            val senderLabel = if (msg.sender == MessageSender.USER) "👤 **المستخدم**" else "✨ **Dev**"
            val time = dateFormat.format(Date(msg.timestamp))
            sb.append("### $senderLabel _($time)_\n\n")
            sb.append("${msg.content}\n\n")
            if (msg.groundingSources.isNotEmpty()) {
                sb.append("**المصادر:**\n")
                msg.groundingSources.forEach { sb.append("- [${it.title}](${it.url})\n") }
                sb.append("\n")
            }
            sb.append("---\n\n")
        }
        return sb.toString()
    }

    private fun buildPlainTextContent(conversation: Conversation, messages: List<Message>): String {
        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("تطبيق Dev - ${conversation.title}\n")
        sb.append("المطور: ${DevConstants.DEVELOPER_FULL}\n")
        sb.append("التاريخ: ${dateFormat.format(Date(conversation.createdAt))}\n")
        sb.append("========================================\n\n")

        for (msg in messages) {
            val senderLabel = if (msg.sender == MessageSender.USER) "[المستخدم]" else "[Dev]"
            val time = dateFormat.format(Date(msg.timestamp))
            sb.append("$senderLabel ($time):\n")
            sb.append("${msg.content}\n\n")
            sb.append("----------------------------------------\n\n")
        }
        return sb.toString()
    }

    private fun buildCsvContent(conversation: Conversation, messages: List<Message>): String {
        val sb = StringBuilder()
        sb.append("Index,Sender,Time,Content\n")
        messages.forEachIndexed { index, msg ->
            val sender = if (msg.sender == MessageSender.USER) "User" else "Dev"
            val time = dateFormat.format(Date(msg.timestamp))
            val escapedContent = "\"" + msg.content.replace("\"", "\"\"") + "\""
            sb.append("${index + 1},$sender,\"$time\",$escapedContent\n")
        }
        return sb.toString()
    }
}

enum class ExportFormat(val extension: String) {
    MARKDOWN("md"),
    TXT("txt"),
    CSV("csv"),
    PDF("txt")
}
