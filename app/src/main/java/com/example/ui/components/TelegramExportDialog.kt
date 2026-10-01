package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TelegramConfig
import com.example.model.TranscriptItem
import com.example.model.VideoInfo
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.SubtitleCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.YouTubeRed

@Composable
fun TelegramSection(
    telegramConfig: TelegramConfig,
    currentVideo: VideoInfo?,
    transcriptsCount: Int,
    onSendToTelegram: (botToken: String, chatId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var botToken by remember { mutableStateOf(telegramConfig.botToken) }
    var chatId by remember { mutableStateOf(telegramConfig.chatId) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // APK & Export Info Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Slate900)
                .border(1.dp, SubtitleCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "APK Faylni Yuklab Olish",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Google AI Studio build & export",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "Ushbu ilovaning to'liq tayyor APK faylini AI Studio yuqori o'ng menyusi orqali 'Export Project (ZIP)' yoki 'Download APK' bo'limidan to'g'ridan-to'g'ri telefoningizga yuklab olishingiz mumkin.",
                color = Color(0xFFE2E8F0),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        // Telegram Bot Integration Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Slate900)
                .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SubtitleCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = SubtitleCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Telegram Botga Transkript Yuborish",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: 6130389200 ga to'liq hisobot uzatish",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "Videoning barcha o'zbekcha subtitrlari, vaqt kodlari va AI xulosasi quyidagi bot orqali Telegram profilingizga yuboriladi:",
                color = Color(0xFFCBD5E1),
                fontSize = 13.sp
            )

            // Chat ID Input (pre-filled with user's ID)
            OutlinedTextField(
                value = chatId,
                onValueChange = { chatId = it },
                label = { Text("Telegram Chat ID", fontSize = 12.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SubtitleCyan,
                    unfocusedBorderColor = Slate800,
                    focusedContainerColor = Slate850,
                    unfocusedContainerColor = Slate850,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("telegram_chat_id_input")
            )

            // Bot Token Input (pre-filled with user's bot token)
            OutlinedTextField(
                value = botToken,
                onValueChange = { botToken = it },
                label = { Text("Telegram Bot Token", fontSize = 12.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SubtitleCyan,
                    unfocusedBorderColor = Slate800,
                    focusedContainerColor = Slate850,
                    unfocusedContainerColor = Slate850,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("telegram_bot_token_input")
            )

            // Send Action Button
            Button(
                onClick = { onSendToTelegram(botToken, chatId) },
                enabled = !telegramConfig.isSending && currentVideo != null && transcriptsCount > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = YouTubeRed,
                    contentColor = Color.White,
                    disabledContainerColor = Slate850,
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("send_telegram_button")
            ) {
                if (telegramConfig.isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Telegramga yuborilmoqda...", fontSize = 14.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = if (transcriptsCount > 0) "Telegramga Yuborish ($transcriptsCount ta subtitr)"
                        else "Avval video transkriptini yuklang",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Status message
            if (!telegramConfig.lastStatusMessage.isNullOrBlank()) {
                val isSuccess = telegramConfig.sendSuccess == true
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSuccess) SuccessGreen.copy(alpha = 0.15f) else YouTubeRed.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            if (isSuccess) SuccessGreen else YouTubeRed,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = if (isSuccess) SuccessGreen else YouTubeRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = telegramConfig.lastStatusMessage,
                        color = if (isSuccess) SuccessGreen else YouTubeRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
