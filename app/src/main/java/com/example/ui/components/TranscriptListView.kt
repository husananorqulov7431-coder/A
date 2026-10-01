package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.TranscriptItem
import com.example.ui.theme.ActiveCueBg
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.SubtitleCyan
import com.example.ui.theme.YouTubeRed

@Composable
fun TranscriptListView(
    transcripts: List<TranscriptItem>,
    currentTimeSec: Float,
    onSeekTo: (Float) -> Unit,
    onCopySuccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Filter by search query
    val filteredList = remember(transcripts, searchQuery) {
        if (searchQuery.isBlank()) transcripts
        else transcripts.filter {
            it.textUz.contains(searchQuery, ignoreCase = true) ||
            it.textOrig.contains(searchQuery, ignoreCase = true)
        }
    }

    // Find current active index
    val activeIndex = remember(transcripts, currentTimeSec) {
        transcripts.indexOfFirst { currentTimeSec >= it.startSeconds && currentTimeSec < it.endSeconds }
    }

    // Auto-scroll when active cue changes and not actively searching
    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0 && searchQuery.isBlank()) {
            listState.animateScrollToItem(activeIndex.coerceAtLeast(0))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Search & Quick Copy Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Transkriptdan qidirish...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Qidirish",
                        tint = SubtitleCyan,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Tozalash",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SubtitleCyan,
                    unfocusedBorderColor = Slate800,
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("transcript_search_field")
            )

            // Copy all button
            Surface(
                onClick = {
                    val fullText = transcripts.joinToString("\n\n") {
                        "[${it.formattedTimestamp}]\nUZ: ${it.textUz}\nORIG: ${it.textOrig}"
                    }
                    clipboardManager.setText(AnnotatedString(fullText))
                    onCopySuccess("To'liq transkript nusxalandi!")
                },
                shape = RoundedCornerShape(12.dp),
                color = Slate850,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                modifier = Modifier.height(50.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Nusxalash",
                        tint = SubtitleCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Nusxa", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (transcripts.isEmpty()) "Hozircha subtitrlar yuklanmagan.\nVideo kiritilgach, 1-daqiqa tayyor bo'lishi bilan bu yerda chiqadi."
                    else "Qidiruv bo'yicha hech narsa topilmadi",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(
                    items = filteredList,
                    key = { _, item -> item.id }
                ) { _, item ->
                    val isActive = currentTimeSec >= item.startSeconds && currentTimeSec < item.endSeconds
                    val cardBg by animateColorAsState(
                        targetValue = if (isActive) ActiveCueBg else Slate900,
                        label = "cardBg"
                    )
                    val borderColor by animateColorAsState(
                        targetValue = if (isActive) YouTubeRed else Slate800,
                        label = "borderColor"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(cardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                            .clickable { onSeekTo(item.startSeconds) }
                            .padding(10.dp)
                            .testTag("transcript_item_${item.startSeconds.toInt()}"),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Play button & timestamp column
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) YouTubeRed else Slate850),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleFilled,
                                    contentDescription = "O'tish",
                                    tint = if (isActive) Color.White else SubtitleCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = item.formattedStartTime,
                                color = if (isActive) YouTubeRed else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Texts Column
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = item.textUz,
                                color = if (isActive) Color.White else Color(0xFFF1F5F9),
                                fontSize = 14.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                lineHeight = 19.sp
                            )
                            if (item.textOrig.isNotBlank()) {
                                Text(
                                    text = item.textOrig,
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
