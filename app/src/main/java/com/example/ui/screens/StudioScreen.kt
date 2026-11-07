package com.example.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.R
import com.example.constants.DevConstants
import com.example.domain.model.GeneratedImageItem
import com.example.ui.components.AuroraBackground
import com.example.ui.components.GlassCard
import com.example.viewmodel.DevViewModel
import java.io.File

/**
 * StudioScreen.kt
 * استوديو توليد وتعديل الصور الذكي الفاخر:
 * تحسين البرومبت التلقائي بواسطة Gemini، اختيار الأنماط والنسب، ومعرض الصور المحفوظة محلياً.
 */
@Composable
fun StudioScreen(
    viewModel: DevViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prompt by viewModel.studioPrompt.collectAsState()
    val enhancedPrompt by viewModel.enhancedPrompt.collectAsState()
    val isEnhancing by viewModel.isEnhancing.collectAsState()
    val isGenerating by viewModel.isGeneratingImage.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val selectedRatio by viewModel.selectedAspectRatio.collectAsState()
    val studioError by viewModel.studioError.collectAsState()
    val images by viewModel.generatedImages.collectAsState()
    val fullscreenImage by viewModel.fullscreenImage.collectAsState()

    val styles = remember {
        listOf(
            "واقعي (Photorealistic)",
            "سينمائي (Cinematic)",
            "ثلاثي الأبعاد (3D Render)",
            "أنمي (Anime Art)",
            "رسم زيتي (Oil Painting)",
            "شعار (Minimal Logo)",
            "ملصق إعلاني (Poster Art)",
            "تصوير منتجات (Product Shot)"
        )
    }

    val aspectRatios = remember { listOf("1:1", "9:16", "16:9", "4:5") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("studio_screen")
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color(0xFFEC4899),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.studio_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "مدعوم بنموذج ${DevConstants.MODEL_IMAGE}",
                        fontSize = 11.sp,
                        color = Color(0xFF22D3EE)
                    )
                }
            }

            // Input Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.setStudioPrompt(it) },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.prompt_input_placeholder),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("studio_prompt_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = Color(0x44A78BFA)
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Enhance Prompt Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.enhanceStudioPrompt() },
                            enabled = prompt.isNotBlank() && !isEnhancing,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x337C3AED)
                            ),
                            modifier = Modifier.testTag("studio_enhance_prompt_button")
                        ) {
                            if (isEnhancing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFF22D3EE),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.enhance_prompt),
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        // Generate Button
                        Button(
                            onClick = { viewModel.generateStudioImage() },
                            enabled = (prompt.isNotBlank() || enhancedPrompt.isNotBlank()) && !isGenerating,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                            modifier = Modifier.testTag("studio_generate_button")
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = if (isGenerating) "جارِ التوليد…" else stringResource(id = R.string.generate_image_button),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }

                    // Display Enhanced Prompt if available
                    AnimatedVisibility(visible = enhancedPrompt.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x3306B6D4))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "✨ البرومبت المحسّن بالإنجليزية:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF22D3EE)
                            )
                            Text(
                                text = enhancedPrompt,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    if (studioError != null) {
                        Text(
                            text = studioError.orEmpty(),
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Style Selector Row (Horizontal Scroll)
            Text(
                text = "النمط الفني:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFA78BFA)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                styles.forEach { style ->
                    val isSelected = selectedStyle == style
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setStudioStyle(style) },
                        label = { Text(style, fontSize = 11.sp) },
                        shape = RoundedCornerShape(14.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF7C3AED),
                            containerColor = Color(0x221F1C42)
                        )
                    )
                }
            }

            // Aspect Ratio Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "النسبة والأبعاد:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFA78BFA)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    aspectRatios.forEach { ratio ->
                        val isSelected = selectedRatio == ratio
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setStudioAspectRatio(ratio) },
                            label = { Text(ratio, fontSize = 11.sp) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEC4899),
                                containerColor = Color(0x221F1C42)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gallery of Generated Images
            Text(
                text = stringResource(id = R.string.saved_gallery),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (images.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_images_yet),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(images, key = { it.id }) { item ->
                        val bitmap = remember(item.filePath) {
                            BitmapFactory.decodeFile(item.filePath)
                        }

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable { viewModel.setFullscreenImage(item) },
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = item.originalPrompt,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for floating bottom nav
        }

        // Fullscreen Image Preview Dialog
        if (fullscreenImage != null) {
            val img = fullscreenImage!!
            val bitmap = remember(img.filePath) { BitmapFactory.decodeFile(img.filePath) }

            Dialog(
                onDismissRequest = { viewModel.setFullscreenImage(null) },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xF00B0A1A)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Dialog Header (Close button)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.setFullscreenImage(null) }) {
                                Icon(Icons.Default.Close, null, tint = Color.White)
                            }
                            Text(
                                text = img.style,
                                color = Color(0xFF22D3EE),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Full image preview
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(1.dp, Color(0x66A78BFA), RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }

                        // Prompt and Share/Save actions
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = img.originalPrompt,
                                color = Color.White,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Button(
                                    onClick = {
                                        val file = File(img.filePath)
                                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                        val share = Intent(Intent.ACTION_SEND).apply {
                                            type = "image/png"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(share, "مشاركة الصورة المولّدة"))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("مشاركة")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
