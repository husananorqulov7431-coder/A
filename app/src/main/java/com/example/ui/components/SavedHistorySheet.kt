package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.db.SavedVideoEntity
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.SubtitleCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.YouTubeRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedHistoryView(
    savedVideos: List<SavedVideoEntity>,
    onSelectVideo: (SavedVideoEntity) -> Unit,
    onDeleteVideo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = SubtitleCyan,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Saqlangan Videolar Tarixi (${savedVideos.size})",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (savedVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hozircha saqlangan videolar yo'q.\nVideoni subtitrlashtirganingizda u avtomatik bu yerda saqlanadi.",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(
                    items = savedVideos,
                    key = { it.videoId }
                ) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate900)
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                            .clickable { onSelectVideo(item) }
                            .padding(10.dp)
                            .testTag("saved_video_${item.videoId}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Thumbnail with duration badge
                        Box(
                            modifier = Modifier
                                .size(width = 90.dp, height = 60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Slate850)
                        ) {
                            AsyncImage(
                                model = item.thumbnailUrl,
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Icon(
                                imageVector = Icons.Default.PlayCircleOutline,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(26.dp)
                            )
                        }

                        // Title & Info
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = dateFormat.format(Date(item.createdAt)),
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                                if (item.telegramSent) {
                                    Text(
                                        text = "• Telegramga yuborilgan",
                                        color = SuccessGreen,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Delete button
                        IconButton(
                            onClick = { onDeleteVideo(item.videoId) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "O'chirish",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
