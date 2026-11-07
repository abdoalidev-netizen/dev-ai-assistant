package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.constants.DevConstants
import com.example.domain.model.Persona
import com.example.services.ExportFormat

/**
 * DevTopAppBar.kt
 * الشريط العلوي لتطبيق Dev.
 * يحتوي على هوية Dev، مبدّل الشخصيات (Persona Selector)، مبدّل نموذج الذكاء الاصطناعي (Flash/Pro)،
 * وزر البحث الحي وزر بدء محادثة جديدة وتصديرها.
 */
@Composable
fun DevTopAppBar(
    title: String,
    activePersona: Persona,
    availablePersonas: List<Persona>,
    onPersonaSelected: (Persona) -> Unit,
    selectedModel: String,
    onModelToggle: (String) -> Unit,
    searchGroundingEnabled: Boolean,
    onSearchGroundingToggle: (Boolean) -> Unit,
    onNewChat: () -> Unit,
    onExportChat: (ExportFormat) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPersonaMenu by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showExportMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Top Main Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Dev Brand & Persona Pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                DevAvatar(size = 38.dp)
                Spacer(modifier = Modifier.width(10.dp))

                // Persona selector pill
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x287C3AED))
                            .border(1.dp, Color(0x55A78BFA), RoundedCornerShape(20.dp))
                            .clickable { showPersonaMenu = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("persona_selector_pill"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activePersona.nameAr,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(activePersona.accentHex)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Mode",
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Persona Dropdown
                    DropdownMenu(
                        expanded = showPersonaMenu,
                        onDismissRequest = { showPersonaMenu = false }
                    ) {
                        availablePersonas.forEach { p ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(p.nameAr, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(p.accentHex))
                                        Text(p.descriptionAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    }
                                },
                                leadingIcon = {
                                    if (p.id == activePersona.id) {
                                        Icon(Icons.Default.Check, null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                    }
                                },
                                onClick = {
                                    showPersonaMenu = false
                                    onPersonaSelected(p)
                                }
                            )
                        }
                    }
                }
            }

            // Right: New Chat (+) and Overflow Menu
            Row(verticalAlignment = Alignment.CenterVertically) {
                // New Chat Button
                IconButton(
                    onClick = onNewChat,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x22A78BFA))
                        .testTag("top_bar_new_chat")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = Color(0xFF22D3EE),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Overflow Menu
                Box {
                    IconButton(
                        onClick = { showOverflowMenu = true },
                        modifier = Modifier.size(38.dp).testTag("top_bar_overflow")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("تصدير المحادثة", fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Outlined.IosShare, null, tint = Color(0xFF22D3EE)) },
                            onClick = {
                                showOverflowMenu = false
                                showExportMenu = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("مسح المحادثة الحالية", fontSize = 13.sp, color = Color(0xFFEF4444)) },
                            leadingIcon = { Icon(Icons.Outlined.DeleteSweep, null, tint = Color(0xFFEF4444)) },
                            onClick = {
                                showOverflowMenu = false
                                onClearChat()
                            }
                        )
                    }

                    // Export Submenu
                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("تصدير كـ Markdown", fontSize = 13.sp) },
                            onClick = { showExportMenu = false; onExportChat(ExportFormat.MARKDOWN) }
                        )
                        DropdownMenuItem(
                            text = { Text("تصدير كـ PDF", fontSize = 13.sp) },
                            onClick = { showExportMenu = false; onExportChat(ExportFormat.PDF) }
                        )
                        DropdownMenuItem(
                            text = { Text("تصدير كـ TXT", fontSize = 13.sp) },
                            onClick = { showExportMenu = false; onExportChat(ExportFormat.TXT) }
                        )
                        DropdownMenuItem(
                            text = { Text("تصدير كـ CSV", fontSize = 13.sp) },
                            onClick = { showExportMenu = false; onExportChat(ExportFormat.CSV) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Secondary Row: Model Switch Chip & Search Grounding Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Model Toggle (Flash ⚡ vs Pro 🧠)
            val isPro = selectedModel == DevConstants.MODEL_PRO
            FilterChip(
                selected = isPro,
                onClick = {
                    val nextModel = if (isPro) DevConstants.MODEL_FLASH else DevConstants.MODEL_PRO
                    onModelToggle(nextModel)
                },
                label = {
                    Text(
                        text = if (isPro) "Gemini Pro (تفكير عميق)" else "Gemini Flash (فائق السرعة)",
                        fontSize = 11.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (isPro) Icons.Default.Psychology else Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (isPro) Color(0xFFEC4899) else Color(0xFFFDE047),
                        modifier = Modifier.size(14.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0x33EC4899),
                    containerColor = Color(0x1F7C3AED)
                )
            )

            // Search Grounding Toggle (🌐)
            FilterChip(
                selected = searchGroundingEnabled,
                onClick = { onSearchGroundingToggle(!searchGroundingEnabled) },
                label = {
                    Text(
                        text = "بحث مباشر",
                        fontSize = 11.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = if (searchGroundingEnabled) Color(0xFF22D3EE) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(13.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0x3306B6D4),
                    containerColor = Color(0x1F1F1C42)
                )
            )
        }
    }
}
