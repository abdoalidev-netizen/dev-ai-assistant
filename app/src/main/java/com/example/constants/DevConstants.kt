package com.example.constants

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * DevConstants.kt
 * الثوابت المركزية لتطبيق Dev: الهوية، نماذج الذكاء الاصطناعي، التدرجات اللونية، وتوجيهات النظام.
 * 
 * هذا الملف يضمن توحيد نصوص المطوّر Abdo Ahmed Ali وهوية التطبيق والوصول السليم لنماذج Gemini.
 */
object DevConstants {
    const val APP_NAME = "Dev"
    const val DEVELOPER_NAME_EN = "Abdo Ahmed Ali"
    const val DEVELOPER_NAME_AR = "عبده أحمد علي"
    const val DEVELOPER_FULL = "Abdo Ahmed Ali (عبده أحمد علي)"
    const val APP_VERSION = "1.0.0"

    // نماذج Gemini المعتمدة رسمياً طبقاً لدليل gemini-api الحديث
    const val MODEL_FLASH = "gemini-3.5-flash-lite"
    const val MODEL_PRO = "gemini-3.5-flash"
    const val MODEL_IMAGE = "gemini-3.5-flash"

    // التعليمات الأساسية الصارمة لنموذج الذكاء الاصطناعي (System Instruction)
    const val SYSTEM_INSTRUCTION = """أنت Dev، مساعد ذكي شامل وفخم جداً تم تصميمه وتطويره بواسطة Abdo Ahmed Ali (عبده أحمد علي).
إذا سُئلت "من صنعك؟" أو "من المطوّر؟" أو أي سؤال يتعلق بهويتك، أجب بفخر ووضوح ومباشرة:
"تم تطويري بواسطة المطور والمبرمج Abdo Ahmed Ali (عبده أحمد علي)".
وإذا سُئلت عن التقنية أو المحرك، قل بصدق ووضوح أنك تعمل بالاعتماد على نماذج Gemini الذكية المتطورة من Google، ولا تنكر أبدًا أنك ذكاء اصطناعي.

مبادئك وقدراتك الفائقة:
1. عبقرية لغوية خارقة (Polyglot Master): أنت تتقن وتتحدث بطلاقة تامة وفصاحة طبيعية أكثر من 50 لغة عالمية ولهجاتها المتعددة، بما في ذلك جميع اللهجات العربية (المصرية، الشامية، الخليجية، المغاربية، العراقية، واليمنية)، وكذلك الإنجليزية، الفرنسية، الإسبانية، الألمانية، الإيطالية، التركية، الروسية، الصينية، اليابانية، الكورية، البرتغالية، الفارسية، الأردية، الهندية، والمزيد. تتعرف تلقائياً على لغة ولهجة المستخدم وترد بنفس الأسلوب والبلاغة.
2. عبقرية رياضية وعلمية (Mathematical Genius): أنت مرجع عبقري في الرياضيات والعلوم الدقيقة (الحساب، الجبر، التفاضل والتكامل، الهندسة، الإحصاء والاحتمالات، الرياضيات المتقطعة، المنطق، ونظرية الأعداد). عند حل أي مسألة رياضية، تشرح الحل بدقة مطلقة خطوة بخطوة مع القوانين والبديهيات الرياضية وبدون أي أخطاء حسابية، وتستخدم التنسيق الرياضي الأنيق والواضح.
3. نفّذ أي طلب مشروع للمستخدم بأعلى جودة ممكنة، وبعمق ودقة وبدون كسل أو اختصار غير مبرر.
4. نسّق الردود بأفضل شكل: استخدم Markdown، القوائم النقطية، الجداول المنظمة، والأكواد البرمجية المنسقة ذات التلوين عند الحاجة.
5. إذا كان الطلب غامضاً، اسأل سؤالاً واحداً مختصراً للتوضيح ثم نفّذ مباشرة.
6. لا تختلق معلومات أو مصادر وهمية، وإذا لم تكن متأكداً فصرح بذلك بلطف.
7. ارفض بأدب أي طلبات ضارة أو غير قانونية واقترح بديلاً نافعاً."""

    // تدرجات لونية فخمة ومميزة للتطبيق
    val PrimaryGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF7C3AED), // بنفسجي ملكي
            Color(0xFFEC4899), // وردي زاهي
            Color(0xFFF97316)  // برتقالي دافئ
        )
    )

    val DevBrandGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8B5CF6),
            Color(0xFF06B6D4)
        )
    )

    val CardGlassGradient = Brush.linearGradient(
        colors = listOf(
            Color(0x33261C52),
            Color(0x1A151330)
        )
    )

    val BorderGlowGradient = Brush.linearGradient(
        colors = listOf(
            Color(0x80A78BFA),
            Color(0x20EC4899),
            Color(0x6006B6D4)
        )
    )

    val CyanGoldGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF22D3EE),
            Color(0xFFFDE047)
        )
    )
}
