package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SuggestionChips.kt
 * شريط الاقتراحات السريعة القابلة للتمرير فوق خانة الإدخال لتقديم أفكار فورية ومثيرة للاهتمام.
 */

data class SuggestionItem(
    val title: String,
    val prompt: String,
    val icon: ImageVector? = null,
    val emoji: String = "✨"
)

@Composable
fun SuggestionChips(
    suggestions: List<SuggestionItem>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        suggestions.forEach { item ->
            AssistChip(
                onClick = { onSelect(item.prompt) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                leadingIcon = {
                    if (item.icon != null) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(15.dp)
                        )
                    } else {
                        Text(text = item.emoji, fontSize = 12.sp)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0x221F1C42)
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = Color(0x40A78BFA)
                )
            )
        }
    }
}
