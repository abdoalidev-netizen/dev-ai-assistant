package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * MarkdownText.kt
 * مفسّر وعارض نصوص متقدم (Markdown Renderer) يدعم:
 * العناوين، القوائم النقطية، كتل الأكواد البرمجية مع زر النسخ وزر الشرح، الجداول، والخطوط العريضة والمائلة.
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onCodeExplain: ((String) -> Unit)? = null
) {
    val clipboardManager = LocalClipboardManager.current
    val blocks = parseMarkdownBlocks(markdown)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> {
                    Text(
                        text = block.text,
                        color = when (block.level) {
                            1 -> Color(0xFFC084FC) // Light violet
                            2 -> Color(0xFFF472B6) // Pink
                            else -> textColor
                        },
                        style = when (block.level) {
                            1 -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                            2 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                            else -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                        },
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                is MarkdownBlock.Paragraph -> {
                    val annotated = parseInlineFormatting(block.text, textColor)
                    Text(
                        text = annotated,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor
                    )
                }

                is MarkdownBlock.BulletItem -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp, end = 8.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6))
                        )
                        val annotated = parseInlineFormatting(block.text, textColor)
                        Text(
                            text = annotated,
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is MarkdownBlock.CodeBlock -> {
                    val codeShape = RoundedCornerShape(16.dp)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = codeShape,
                        color = Color(0xFF0F0D24),
                        shadowElevation = 4.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33A78BFA))
                    ) {
                        Column {
                            // Header bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E193C))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = block.language.ifEmpty { "code" },
                                    color = Color(0xFF22D3EE),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Explain button
                                    if (onCodeExplain != null) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onCodeExplain(block.code) }
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Lightbulb,
                                                contentDescription = "Explain Code",
                                                tint = Color(0xFFFDE047),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "شرح",
                                                color = Color(0xFFFDE047),
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    // Copy button
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                clipboardManager.setText(AnnotatedString(block.code))
                                            }
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.ContentCopy,
                                            contentDescription = "Copy code",
                                            tint = Color(0xFFE2E8F0),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "نسخ",
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            // Code content
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = block.code,
                                    color = Color(0xFFE2E8F0),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }

                is MarkdownBlock.Table -> {
                    // Simple structured table view
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x22FFFFFF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33A78BFA))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            block.rows.forEachIndexed { rowIndex, row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    row.forEach { cell ->
                                        Text(
                                            text = cell.trim(),
                                            color = if (rowIndex == 0) Color(0xFF22D3EE) else textColor,
                                            fontWeight = if (rowIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp,
                                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                                if (rowIndex == 0) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        color = Color(0x44A78BFA)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class Paragraph(val text: String) : MarkdownBlock()
    data class BulletItem(val text: String) : MarkdownBlock()
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock()
    data class Table(val rows: List<List<String>>) : MarkdownBlock()
}

private fun parseMarkdownBlocks(rawText: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = rawText.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        // 1. Code Block start (```)
        if (line.trim().startsWith("```")) {
            val language = line.trim().removePrefix("```").trim()
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].trim().startsWith("```")) {
                codeLines.add(lines[i])
                i++
            }
            blocks.add(MarkdownBlock.CodeBlock(language, codeLines.joinToString("\n")))
            i++
            continue
        }

        // 2. Headings
        val trimmed = line.trim()
        if (trimmed.startsWith("# ")) {
            blocks.add(MarkdownBlock.Heading(1, trimmed.removePrefix("# ").trim()))
            i++
            continue
        } else if (trimmed.startsWith("## ")) {
            blocks.add(MarkdownBlock.Heading(2, trimmed.removePrefix("## ").trim()))
            i++
            continue
        } else if (trimmed.startsWith("### ")) {
            blocks.add(MarkdownBlock.Heading(3, trimmed.removePrefix("### ").trim()))
            i++
            continue
        }

        // 3. Bullet items
        if (trimmed.startsWith("* ") || trimmed.startsWith("- ")) {
            blocks.add(MarkdownBlock.BulletItem(trimmed.substring(2).trim()))
            i++
            continue
        }

        // 4. Tables (pipe separated)
        if (trimmed.startsWith("|") && trimmed.endsWith("|")) {
            val tableRows = mutableListOf<List<String>>()
            while (i < lines.size && lines[i].trim().startsWith("|") && lines[i].trim().endsWith("|")) {
                val rowLine = lines[i].trim()
                if (!rowLine.contains("---")) {
                    val cells = rowLine.split("|").filter { it.isNotBlank() }
                    tableRows.add(cells)
                }
                i++
            }
            if (tableRows.isNotEmpty()) {
                blocks.add(MarkdownBlock.Table(tableRows))
            }
            continue
        }

        // 5. Standard paragraph
        if (trimmed.isNotEmpty()) {
            blocks.add(MarkdownBlock.Paragraph(trimmed))
        }

        i++
    }

    return blocks
}

private fun parseInlineFormatting(text: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val boldRegex = Regex("""\*\*(.*?)\*\*""")
        val codeRegex = Regex("""`(.*?)`""")

        val matches = (boldRegex.findAll(text).map { Pair(it.range, "bold" to it.groupValues[1]) } +
                codeRegex.findAll(text).map { Pair(it.range, "code" to it.groupValues[1]) })
            .sortedBy { it.first.first }
            .toList()

        for ((range, typePair) in matches) {
            if (range.first > cursor) {
                append(text.substring(cursor, range.first))
            }

            val (type, content) = typePair
            if (type == "bold") {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultColor))
                append(content)
                pop()
            } else if (type == "code") {
                pushStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color(0x33A78BFA),
                        color = Color(0xFF22D3EE),
                        fontSize = 13.sp
                    )
                )
                append(" $content ")
                pop()
            }

            cursor = range.last + 1
        }

        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }
}
