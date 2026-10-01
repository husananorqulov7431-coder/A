package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TranscriptItem
import com.example.ui.theme.Slate950
import com.example.ui.theme.SubtitleCyan
import com.example.ui.theme.YouTubeRed

@Composable
fun LiveSubtitleOverlay(
    activeItem: TranscriptItem?,
    showBilingual: Boolean,
    fontSizeMultiplier: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = activeItem != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (activeItem != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Slate950.copy(alpha = 0.85f),
                                    Color.Black.copy(alpha = 0.94f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = SubtitleCyan.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("live_subtitle_box"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Header tag with Live badge and timestamp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(YouTubeRed)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "UZ SUBTITR",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Sinxron ovoz",
                                tint = SubtitleCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Text(
                            text = activeItem.formattedTimestamp,
                            color = SubtitleCyan.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Main Uzbek Subtitle Text
                    Text(
                        text = activeItem.textUz,
                        color = Color.White,
                        fontSize = (17 * fontSizeMultiplier).sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = (22 * fontSizeMultiplier).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .testTag("subtitle_text_uz")
                    )

                    // Optional Original Text (Bilingual mode)
                    if (showBilingual && activeItem.textOrig.isNotBlank()) {
                        Text(
                            text = activeItem.textOrig,
                            color = Color(0xFF94A3B8),
                            fontSize = (13 * fontSizeMultiplier).sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("subtitle_text_orig")
                        )
                    }
                }
            }
        }
    }
}
