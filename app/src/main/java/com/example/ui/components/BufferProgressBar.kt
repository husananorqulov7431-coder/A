package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PipelineStatus
import com.example.model.ProcessingState
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.SubtitleCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.YouTubeRed

@Composable
fun BufferProgressBar(
    processingState: ProcessingState,
    currentTimeSec: Float,
    durationSec: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Slate900)
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Status Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                when (processingState.status) {
                    PipelineStatus.BUFFERING_CHUNK_1 -> {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = YouTubeRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "1-Daqiqa tayyorlanmoqda: ${(processingState.chunkProgressPercent * 100).toInt()}%",
                            color = YouTubeRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    PipelineStatus.STREAMING_ACTIVE -> {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Text(
                            text = "1-Daqiqa tayyor! Video ko'rsatilmoqda ⚡",
                            color = SuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    PipelineStatus.COMPLETED -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SubtitleCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "To'liq video o'zbekcha subtitrlashtirildi",
                            color = SubtitleCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SubtitleCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Jonli AI Transkripsiya oqimi",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Time indicator
            val currentMin = (currentTimeSec / 60).toInt()
            val currentSec = (currentTimeSec % 60).toInt()
            val durMin = (durationSec / 60).toInt()
            val durSec = (durationSec % 60).toInt()
            Text(
                text = String.format("%02d:%02d / %02d:%02d", currentMin, currentSec, durMin, durSec),
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Custom Visual Multi-chunk Canvas Bar (Gemini AI Music Style)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Slate850)
                .testTag("buffer_timeline_canvas")
        ) {
            val canvasW = size.width
            val canvasH = size.height

            val safeDuration = if (durationSec > 10f) durationSec else 300f

            // 1. Draw 1-minute initial threshold marker
            val oneMinX = (60f / safeDuration).coerceIn(0f, 1f) * canvasW
            drawRoundRect(
                color = Color(0x33FF0033),
                topLeft = Offset(0f, 0f),
                size = Size(oneMinX, canvasH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // 2. Draw Transcribed Buffer Range (Chunks ready)
            val bufferedFrac = (processingState.bufferedSeconds / safeDuration).coerceIn(0f, 1f)
            val bufferedWidth = bufferedFrac * canvasW
            if (bufferedWidth > 0f) {
                drawRoundRect(
                    color = SubtitleCyan.copy(alpha = 0.45f),
                    topLeft = Offset(0f, 0f),
                    size = Size(bufferedWidth, canvasH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }

            // 3. Draw Active Synthesizing chunk pulse (Music wave / AI generator effect)
            if (processingState.isProcessing) {
                val chunkStartSec = processingState.currentChunkIndex * 60f
                val chunkEndSec = (chunkStartSec + 60f).coerceAtMost(safeDuration)
                val cStartX = (chunkStartSec / safeDuration).coerceIn(0f, 1f) * canvasW
                val cEndX = (chunkEndSec / safeDuration).coerceIn(0f, 1f) * canvasW
                val cWidth = (cEndX - cStartX).coerceAtLeast(8f)

                drawRoundRect(
                    color = YouTubeRed.copy(alpha = pulseAlpha * 0.7f),
                    topLeft = Offset(cStartX, 0f),
                    size = Size(cWidth, canvasH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }

            // 4. Waveform tick bars along the timeline
            val numTicks = 40
            val tickStep = canvasW / numTicks
            for (i in 0..numTicks) {
                val x = i * tickStep
                val tickH = if (i % 5 == 0) canvasH * 0.7f else canvasH * 0.35f
                val tickY = (canvasH - tickH) / 2f
                val isBeforeBuffer = x <= bufferedWidth
                val tickColor = if (isBeforeBuffer) SubtitleCyan.copy(alpha = 0.8f) else Color(0x33475569)
                drawLine(
                    color = tickColor,
                    start = Offset(x, tickY),
                    end = Offset(x, tickY + tickH),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // 5. One minute indicator milestone line
            drawLine(
                color = YouTubeRed,
                start = Offset(oneMinX, 0f),
                end = Offset(oneMinX, canvasH),
                strokeWidth = 2.dp.toPx()
            )

            // 6. Current playback needle
            val playheadFrac = (currentTimeSec / safeDuration).coerceIn(0f, 1f)
            val playheadX = playheadFrac * canvasW
            drawLine(
                color = Color.White,
                start = Offset(playheadX, 0f),
                end = Offset(playheadX, canvasH),
                strokeWidth = 3.dp.toPx()
            )
            drawCircle(
                color = YouTubeRed,
                radius = 5.dp.toPx(),
                center = Offset(playheadX, canvasH / 2f)
            )
        }

        // Subtitle status descriptor note
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚡ 1-Daqiqa (60s) belgisi: Video ochilish nuqtasi",
                color = Color(0xFF64748B),
                fontSize = 10.sp
            )
            Text(
                text = "Bufer: ${processingState.formattedBufferTime}",
                color = SubtitleCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
