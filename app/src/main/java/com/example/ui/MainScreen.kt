package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.PipelineStatus
import com.example.player.YouTubePlayerController
import com.example.player.YouTubeWebViewPlayer
import com.example.ui.components.BufferProgressBar
import com.example.ui.components.LiveSubtitleOverlay
import com.example.ui.components.SavedHistoryView
import com.example.ui.components.SummaryView
import com.example.ui.components.TelegramSection
import com.example.ui.components.TranscriptListView
import com.example.ui.components.VideoInputBar
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.SubtitleCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentVideo by viewModel.currentVideo.collectAsStateWithLifecycle()
    val processingState by viewModel.processingState.collectAsStateWithLifecycle()
    val transcripts by viewModel.transcripts.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val currentTimeSec by viewModel.currentTimeSec.collectAsStateWithLifecycle()
    val durationSec by viewModel.durationSec.collectAsStateWithLifecycle()
    val activeCue by viewModel.activeTranscript.collectAsStateWithLifecycle()
    val telegramConfig by viewModel.telegramConfig.collectAsStateWithLifecycle()
    val showBilingual by viewModel.showBilingual.collectAsStateWithLifecycle()
    val fontSizeMultiplier by viewModel.fontSizeMultiplier.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val savedVideos by viewModel.savedVideos.collectAsStateWithLifecycle()

    var urlInput by remember { mutableStateOf("") }
    val playerController = remember { YouTubePlayerController() }
    var isPlaying by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Slate950,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(YouTubeRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ClosedCaption,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "UzTube Sub",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SubtitleCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "AI JONLI",
                                        color = SubtitleCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Real vaqtda o'zbekcha subtitrlar",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // Bilingual toggle button
                    IconButton(
                        onClick = { viewModel.toggleBilingual() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (showBilingual) SubtitleCyan.copy(alpha = 0.2f) else Slate850)
                            .testTag("toggle_bilingual_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Ikki tilli rejim",
                            tint = if (showBilingual) SubtitleCyan else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Font size cycle button
                    IconButton(
                        onClick = {
                            val next = when (fontSizeMultiplier) {
                                1.0f -> 1.25f
                                1.25f -> 1.45f
                                else -> 1.0f
                            }
                            viewModel.setFontSizeMultiplier(next)
                        },
                        modifier = Modifier
                            .padding(start = 4.dp, end = 6.dp)
                            .clip(CircleShape)
                            .background(Slate850)
                            .testTag("font_size_cycle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Shrift o'lchami",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Slate950
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // URL Input & Presets Bar
            VideoInputBar(
                urlInput = urlInput,
                onUrlChange = { urlInput = it },
                onSubmitUrl = {
                    if (urlInput.isNotBlank()) {
                        viewModel.startPipeline(urlInput)
                        urlInput = ""
                    }
                },
                onSelectPreset = { preset ->
                    viewModel.startPipeline(preset.videoId, preset.title)
                },
                isLoading = processingState.isProcessing
            )

            // Video Player Container (Fixed Aspect Ratio 16:9)
            val video = currentVideo
            if (video != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                        .testTag("video_player_box")
                ) {
                    if (processingState.canStartPlayback) {
                        // YouTube Embedded Player with real-time bridge
                        YouTubeWebViewPlayer(
                            videoId = video.videoId,
                            controller = playerController,
                            onTimeUpdate = { cur, dur ->
                                viewModel.updatePlaybackTime(cur, dur)
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating Live Subtitle Overlay on video
                        LiveSubtitleOverlay(
                            activeItem = activeCue,
                            showBilingual = showBilingual,
                            fontSizeMultiplier = fontSizeMultiplier,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    } else {
                        // Progressive Buffer Loading Poster (Gemini AI Music style)
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = video.thumbnailUrl,
                                contentDescription = video.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.75f))
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = YouTubeRed,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = processingState.statusMessage,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Text(
                                    text = "1-Daqiqa tayyor bo'lishi bilan video ko'rinishni boshlaydi ⚡",
                                    color = SubtitleCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Subtitle Quick Controller Row under the player
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Rewind 5s
                        IconButton(
                            onClick = {
                                val target = (currentTimeSec - 5f).coerceAtLeast(0f)
                                playerController.seekTo(target)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastRewind,
                                contentDescription = "5s orqaga",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Play/Pause
                        IconButton(
                            onClick = {
                                if (isPlaying) playerController.pause() else playerController.play()
                                isPlaying = !isPlaying
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Forward 5s
                        IconButton(
                            onClick = {
                                val target = (currentTimeSec + 5f).coerceAtMost(durationSec)
                                playerController.seekTo(target)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "5s oldinga",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Visual AI Buffer Progress Bar
            BufferProgressBar(
                processingState = processingState,
                currentTimeSec = currentTimeSec,
                durationSec = durationSec,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            // Primary Scrollable Navigation Tabs
            val tabs = listOf(
                "Subtitr & Oqim",
                "To'liq Transkript",
                "AI Konspekt",
                "Telegram & APK",
                "Tarix"
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Slate950,
                contentColor = Color.White,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = YouTubeRed,
                            height = 3.dp
                        )
                    }
                },
                divider = { Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Slate800)) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { viewModel.setTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) YouTubeRed else Color(0xFF94A3B8)
                            )
                        },
                        modifier = Modifier.testTag("tab_$index")
                    )
                }
            }

            // Tab Content Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_content_transition"
                ) { targetIndex ->
                    when (targetIndex) {
                        0 -> {
                            // Player & Live Subtitle View with Active Transcript Cards
                            TranscriptListView(
                                transcripts = transcripts,
                                currentTimeSec = currentTimeSec,
                                onSeekTo = { sec -> playerController.seekTo(sec) },
                                onCopySuccess = { msg ->
                                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            )
                        }
                        1 -> {
                            // Full Transcript Screen
                            TranscriptListView(
                                transcripts = transcripts,
                                currentTimeSec = currentTimeSec,
                                onSeekTo = { sec ->
                                    playerController.seekTo(sec)
                                    viewModel.setTab(0)
                                },
                                onCopySuccess = { msg ->
                                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            )
                        }
                        2 -> {
                            // AI Konspekt (Summary & Key Points)
                            SummaryView(
                                summary = summary,
                                currentVideo = currentVideo,
                                onCopy = { msg ->
                                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            )
                        }
                        3 -> {
                            // Telegram & APK Export Section
                            TelegramSection(
                                telegramConfig = telegramConfig,
                                currentVideo = currentVideo,
                                transcriptsCount = transcripts.size,
                                onSendToTelegram = { token, chatId ->
                                    viewModel.sendToTelegram(token, chatId)
                                }
                            )
                        }
                        4 -> {
                            // Saved Videos History
                            SavedHistoryView(
                                savedVideos = savedVideos,
                                onSelectVideo = { entity ->
                                    viewModel.selectSavedVideo(entity)
                                },
                                onDeleteVideo = { videoId ->
                                    viewModel.deleteSavedVideo(videoId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
