package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AuroraBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.MarkdownText
import com.example.viewmodel.DevViewModel

/**
 * ToolsScreen.kt
 * شاشة الأدوات الذكية الفاخرة:
 * تحتوي على بطاقات لـ 8 أدوات تخصصية (ترجمة، OCR، تلخيص، كتابة، برمجة، برومبت، تعليم، ورياضيات)
 * مع واجهة تنفيذ تفاعلية ونتائج منظمة بتنسيق Markdown.
 */

data class ToolDefinition(
    val id: String,
    val titleRes: Int,
    val descRes: Int,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun ToolsScreen(
    viewModel: DevViewModel,
    modifier: Modifier = Modifier
) {
    val activeToolId by viewModel.activeToolId.collectAsState()

    val toolList = remember {
        listOf(
            ToolDefinition("app_builder", R.string.tool_app_builder, R.string.tool_app_builder_desc, Icons.Default.PhoneAndroid, Color(0xFF10B981)),
            ToolDefinition("translator", R.string.tool_translator, R.string.tool_translator_desc, Icons.Default.Translate, Color(0xFF7C3AED)),
            ToolDefinition("math", R.string.tool_math, R.string.tool_math_desc, Icons.Default.Calculate, Color(0xFF3B82F6)),
            ToolDefinition("code", R.string.tool_code, R.string.tool_code_desc, Icons.Default.Code, Color(0xFF06B6D4)),
            ToolDefinition("ocr", R.string.tool_ocr, R.string.tool_ocr_desc, Icons.Default.DocumentScanner, Color(0xFF22D3EE)),
            ToolDefinition("summarizer", R.string.tool_summarizer, R.string.tool_summarizer_desc, Icons.Default.Description, Color(0xFFEC4899)),
            ToolDefinition("writer", R.string.tool_writer, R.string.tool_writer_desc, Icons.Default.EditNote, Color(0xFFF97316)),
            ToolDefinition("prompt", R.string.tool_prompt, R.string.tool_prompt_desc, Icons.Default.Psychology, Color(0xFF8B5CF6)),
            ToolDefinition("tutor", R.string.tool_tutor, R.string.tool_tutor_desc, Icons.Default.School, Color(0xFFF59E0B))
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("tools_screen")
    ) {
        AuroraBackground()

        if (activeToolId == null) {
            // Main Tools Grid / List
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.tools_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 14.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(toolList, key = { it.id }) { tool ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openTool(tool.id) }
                                .testTag("tool_card_${tool.id}"),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(tool.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = tool.icon,
                                        contentDescription = null,
                                        tint = tool.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(id = tool.titleRes),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stringResource(id = tool.descRes),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                        lineHeight = 16.sp
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Open",
                                    tint = tool.color.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp)) // Floating nav space
            }
        } else {
            // Tool Execution Sheet
            val currentTool = toolList.find { it.id == activeToolId } ?: toolList.first()
            ToolExecutionSheet(tool = currentTool, viewModel = viewModel)
        }
    }
}

@Composable
fun ToolExecutionSheet(
    tool: ToolDefinition,
    viewModel: DevViewModel
) {
    val input by viewModel.toolInput.collectAsState()
    val extra by viewModel.toolExtra.collectAsState()
    val output by viewModel.toolOutput.collectAsState()
    val isRunning by viewModel.isToolRunning.collectAsState()
    val error by viewModel.toolError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.closeTool() },
                modifier = Modifier.testTag("tool_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(id = tool.titleRes),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Input Box
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "المدخلات أو النص المستهدف:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA78BFA)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = input,
                            onValueChange = { viewModel.setToolInput(it) },
                            placeholder = { Text("اكتب أو الصق النص هنا…", fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tool_text_input"),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = tool.color,
                                unfocusedBorderColor = Color(0x44A78BFA)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Extra option parameter (e.g. language, tone, level)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (tool.id == "translator") "اللغة الهدف (50+ لغة):" else if (tool.id == "math") "نوع المسألة الرياضية:" else "الخيار / المعامل:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = extra,
                                onValueChange = { viewModel.setToolExtra(it) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("tool_extra_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = tool.color,
                                    unfocusedBorderColor = Color(0x44A78BFA)
                                )
                            )
                        }

                        // Quick selection chips for Translator and Math
                        if (tool.id == "translator") {
                            Spacer(modifier = Modifier.height(8.dp))
                            val languages = listOf("الإنجليزية", "الفرنسية", "الألمانية", "الإسبانية", "التركية", "الروسية", "الصينية", "اليابانية", "الكورية", "الإيطالية", "اللهجة المصرية", "اللهجة الخليجية", "اللهجة الشامية", "الفارسية", "الأردية", "الهندية", "البرتغالية")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                languages.forEach { lang ->
                                    val isSelected = extra == lang
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setToolExtra(lang) },
                                        label = { Text(lang, fontSize = 11.sp) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = tool.color,
                                            containerColor = Color(0x221F1C42)
                                        )
                                    )
                                }
                            }
                        } else if (tool.id == "math") {
                            Spacer(modifier = Modifier.height(8.dp))
                            val mathTypes = listOf("معادلات جبرية", "تفاضل وتكامل (Calculus)", "هندسة ومثلثات", "إحصاء واحتمالات", "مصفوفات وجبر خطي", "نظرية الأعداد", "مسائل لفظية")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                mathTypes.forEach { type ->
                                    val isSelected = extra == type
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setToolExtra(type) },
                                        label = { Text(type, fontSize = 11.sp) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = tool.color,
                                            containerColor = Color(0x221F1C42)
                                        )
                                    )
                                }
                            }
                        } else if (tool.id == "app_builder") {
                            Spacer(modifier = Modifier.height(8.dp))
                            val appTypes = listOf(
                                "تطبيق متجر إلكتروني معقد (E-Commerce)",
                                "شبكة تواصل اجتماعي ورسائل (Social Network)",
                                "تطبيق حجز وتوصيل طلبات (Delivery App)",
                                "تطبيق لياقة وصحة ذكي (Health & Fitness)",
                                "تطبيق إدارة مهام ومشاريع (Project Management)",
                                "منصة دورات وتعليم تفاعلي (LMS & Courses)",
                                "تطبيق متكامل مع كود وAPK"
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                appTypes.forEach { type ->
                                    val isSelected = extra == type
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setToolExtra(type) },
                                        label = { Text(type, fontSize = 11.sp) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = tool.color,
                                            containerColor = Color(0x221F1C42)
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.runTool() },
                            enabled = input.isNotBlank() && !isRunning,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = tool.color),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("tool_run_button")
                        ) {
                            if (isRunning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جارِ المعالجة بـ Gemini…", color = Color.White)
                            } else {
                                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تنفيذ الأداة الآن", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        if (error != null) {
                            Text(
                                text = error.orEmpty(),
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Output Card
            if (output.isNotEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "النتيجة:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF22D3EE)
                                )

                                IconButton(
                                    onClick = { viewModel.copyToClipboard(output) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            MarkdownText(
                                markdown = output,
                                textColor = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
