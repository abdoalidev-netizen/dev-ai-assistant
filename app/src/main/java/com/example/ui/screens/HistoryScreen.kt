package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.Conversation
import com.example.ui.components.AuroraBackground
import com.example.ui.components.GlassCard
import com.example.viewmodel.DevViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * HistoryScreen.kt
 * سجل المحادثات المحفوظة محلياً في قاعدة بيانات Room.
 * يدعم البحث والتصفية، التثبيت، الإضافة للمفضلة، إعادة التسمية، والحذف الآمن.
 */
@Composable
fun HistoryScreen(
    viewModel: DevViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("all") } // "all", "pinned", "favorites"
    var showClearDialog by remember { mutableStateOf(false) }
    var conversationToRename by remember { mutableStateOf<Conversation?>(null) }
    var renameText by remember { mutableStateOf("") }

    val filteredList = conversations.filter { conv ->
        val matchesSearch = conv.title.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (filterMode) {
            "pinned" -> conv.isPinned
            "favorites" -> conv.isFavorite
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen")
    ) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.history_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (conversations.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.testTag("history_clear_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear History",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(stringResource(id = R.string.search_conversations), fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = Color(0xFFA78BFA), modifier = Modifier.size(18.dp))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input"),
                shape = RoundedCornerShape(18.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF7C3AED),
                    unfocusedBorderColor = Color(0x33A78BFA)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filters row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterMode == "all",
                    onClick = { filterMode = "all" },
                    label = { Text("الكل", fontSize = 12.sp) },
                    shape = RoundedCornerShape(14.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF7C3AED),
                        containerColor = Color(0x221F1C42)
                    )
                )

                FilterChip(
                    selected = filterMode == "pinned",
                    onClick = { filterMode = "pinned" },
                    label = { Text("المثبتة", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.PushPin, null, modifier = Modifier.size(14.dp)) },
                    shape = RoundedCornerShape(14.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF22D3EE),
                        containerColor = Color(0x221F1C42)
                    )
                )

                FilterChip(
                    selected = filterMode == "favorites",
                    onClick = { filterMode = "favorites" },
                    label = { Text("المفضلة", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Bookmark, null, modifier = Modifier.size(14.dp)) },
                    shape = RoundedCornerShape(14.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFDE047),
                        containerColor = Color(0x221F1C42)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Conversations List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_conversations),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList, key = { it.id }) { conv ->
                        val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(conv.updatedAt))

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectConversation(conv.id) }
                                .testTag("conversation_item_${conv.id}"),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = if (conv.isPinned) Color(0xFF22D3EE) else Color(0xFFA78BFA),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = conv.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = dateStr,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }

                                // Actions: Pin, Favorite, Rename, Delete
                                Row {
                                    IconButton(
                                        onClick = { viewModel.togglePinConversation(conv.id, conv.isPinned) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (conv.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                            contentDescription = "Pin",
                                            tint = if (conv.isPinned) Color(0xFF22D3EE) else Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.toggleFavoriteConversation(conv.id, conv.isFavorite) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (conv.isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                            contentDescription = "Favorite",
                                            tint = if (conv.isFavorite) Color(0xFFFDE047) else Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            conversationToRename = conv
                                            renameText = conv.title
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Rename",
                                            tint = Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteConversation(conv.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Rename Dialog
        if (conversationToRename != null) {
            AlertDialog(
                onDismissRequest = { conversationToRename = null },
                title = { Text(stringResource(id = R.string.rename_chat)) },
                text = {
                    OutlinedTextField(
                        value = renameText,
                        onValueChange = { renameText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (renameText.isNotBlank()) {
                                viewModel.renameConversation(conversationToRename!!.id, renameText.trim())
                            }
                            conversationToRename = null
                        }
                    ) {
                        Text("حفظ")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { conversationToRename = null }) {
                        Text("إلغاء")
                    }
                }
            )
        }

        // Clear All Dialog
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text(stringResource(id = R.string.clear_history)) },
                text = { Text(stringResource(id = R.string.clear_history_confirm)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllConversations()
                            showClearDialog = false
                        }
                    ) {
                        Text("نعم، احذف الكل", color = Color(0xFFEF4444))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}
