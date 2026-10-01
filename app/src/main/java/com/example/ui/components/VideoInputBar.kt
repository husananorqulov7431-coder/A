package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoInfo
import com.example.network.PresetVideos
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.SubtitleCyan
import com.example.ui.theme.YouTubeRed

@Composable
fun VideoInputBar(
    urlInput: String,
    onUrlChange: (String) -> Unit,
    onSubmitUrl: () -> Unit,
    onSelectPreset: (VideoInfo) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main URL Input Field with Paste
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = urlInput,
                onValueChange = onUrlChange,
                placeholder = {
                    Text(
                        "YouTube video havolasi (link) ni kiriting...",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video",
                        tint = YouTubeRed,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (urlInput.isNotEmpty()) {
                            IconButton(onClick = { onUrlChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Tozalash",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                val text = clipboardManager.getText()?.text
                                if (!text.isNullOrBlank()) {
                                    onUrlChange(text)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Joylashtirish",
                                    tint = SubtitleCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YouTubeRed,
                    unfocusedBorderColor = Slate800,
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("youtube_url_input")
            )

            // Submit Button
            Button(
                onClick = onSubmitUrl,
                enabled = urlInput.isNotBlank() && !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YouTubeRed,
                    contentColor = Color.White,
                    disabledContainerColor = Slate850,
                    disabledContentColor = Color.Gray
                ),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("start_subtitles_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Boshlash", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Preset Chips Row for quick testing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Namunalar:",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 2.dp)
            )

            PresetVideos.sampleVideos.forEach { preset ->
                val shortTitle = when {
                    preset.title.contains("Sun'iy intellekt") -> "🤖 Sun'iy Intellekt"
                    preset.title.contains("YouTube va") -> "📺 YouTube Tarixi"
                    preset.title.contains("Global media") -> "🌐 Global Media"
                    else -> "🚀 Koinot & Fan"
                }

                FilterChip(
                    selected = false,
                    onClick = { onSelectPreset(preset) },
                    label = { Text(shortTitle, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Slate900,
                        labelColor = Color(0xFFCBD5E1)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Slate800,
                        enabled = true,
                        selected = false
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}
