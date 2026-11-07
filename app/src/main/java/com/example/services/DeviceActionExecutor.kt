package com.example.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import com.example.domain.model.DeviceActionRequest
import com.example.domain.model.DeviceActionType

/**
 * DeviceActionExecutor.kt
 * تنفيذ أوامر النظام على جهاز المستخدم (منبه، مؤقت، تقويم، بريد، روابط، مشاركة).
 *
 * قاعدة الأمان الصارمة:
 * لا يتم تنفيذ أي أمر خفية، بل يُعرض للمستخدم ملخص الإجراء وزر "تأكيد" واضح قبل إطلاق أي Intent.
 */
class DeviceActionExecutor(private val context: Context) {

    fun executeAction(request: DeviceActionRequest): Boolean {
        return try {
            val intent = when (request.actionType) {
                DeviceActionType.SET_ALARM -> createAlarmIntent(request.params)
                DeviceActionType.SET_TIMER -> createTimerIntent(request.params)
                DeviceActionType.ADD_CALENDAR_EVENT -> createCalendarIntent(request.params)
                DeviceActionType.SEND_EMAIL -> createEmailIntent(request.params)
                DeviceActionType.OPEN_URL -> createOpenUrlIntent(request.params)
                DeviceActionType.SHARE_TEXT -> createShareTextIntent(request.params)
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun createAlarmIntent(params: Map<String, String>): Intent {
        val hour = params["hour"]?.toIntOrNull() ?: 7
        val minutes = params["minutes"]?.toIntOrNull() ?: 0
        val message = params["message"] ?: "منبه Dev الذكي"
        return Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minutes)
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        }
    }

    private fun createTimerIntent(params: Map<String, String>): Intent {
        val seconds = params["seconds"]?.toIntOrNull() ?: 300
        val message = params["message"] ?: "مؤقت Dev"
        return Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        }
    }

    private fun createCalendarIntent(params: Map<String, String>): Intent {
        val title = params["title"] ?: "حدث Dev الجديد"
        val description = params["description"] ?: ""
        return Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
        }
    }

    private fun createEmailIntent(params: Map<String, String>): Intent {
        val recipient = params["to"] ?: ""
        val subject = params["subject"] ?: "رسالة من Dev"
        val body = params["body"] ?: ""
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipient")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
    }

    private fun createOpenUrlIntent(params: Map<String, String>): Intent {
        val url = params["url"] ?: "https://www.google.com"
        val validUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
        return Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
    }

    private fun createShareTextIntent(params: Map<String, String>): Intent {
        val text = params["text"] ?: ""
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
    }
}
