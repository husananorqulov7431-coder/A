package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoInfo
import com.example.model.VideoSummary
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.SubtitleCyan
import com.example.ui.theme.YouTubeRed

@Composable
fun SummaryView(
    summary: VideoSummary?,
    currentVideo: VideoInfo?,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (summary == null || summary.summaryUz.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Video 1-daqiqasi tayyor bo'lgach, Gemini AI avtomatik tarzda o'zbekcha xulosa va konspekt shakllantiradi.",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            // Main Summary Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Slate900)
                    .border(1.dp, SubtitleCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SubtitleCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "AI Xulosa (Konspekt)",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val copyText = "📌 ${currentVideo?.title}\n\n💡 Xulosa:\n${summary.summaryUz}\n\n✨ Asosiy jihatlar:\n" +
                                    summary.bulletPointsUz.joinToString("\n") { "• $it" }
                            clipboardManager.setText(AnnotatedString(copyText))
                            onCopy("Konspekt nusxalandi!")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Slate850,
                            contentColor = SubtitleCyan
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("copy_summary_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Nusxalash",
                            modifier = Modifier.size(14.dp)
                        )
                        Text(" Nusxa", fontSize = 11.sp)
                    }
                }

                if (summary.mainTopicUz.isNotBlank()) {
                    Text(
                        text = "Mavzu: ${summary.mainTopicUz}",
                        color = SubtitleCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = summary.summaryUz,
                    color = Color(0xFFF1F5F9),
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }

            // Key Points Card
            if (summary.bulletPointsUz.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate900)
                        .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Asosiy Fikrlar va Tushunchalar",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    summary.bulletPointsUz.forEach { point ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SubtitleCyan,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = point,
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
