package com.example.network

import android.util.Log
import com.example.model.TranscriptItem
import com.example.model.VideoInfo
import com.example.model.VideoSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TelegramBotService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun sendTranscriptToTelegram(
        botToken: String,
        chatId: String,
        video: VideoInfo,
        transcripts: List<TranscriptItem>,
        summary: VideoSummary?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (botToken.isBlank() || chatId.isBlank()) {
                return@withContext Result.failure(Exception("Bot token yoki Telegram Chat ID bo'sh bo'lishi mumkin emas"))
            }

            // Clean token if user included 'bot' prefix or whitespace
            val cleanToken = botToken.trim().removePrefix("bot").trim()

            // 1. Prepare Header Message
            val headerSb = StringBuilder()
            headerSb.append("🎬 <b>UzTube Sub: Video O'zbekcha Transkripti</b>\n\n")
            headerSb.append("📌 <b>Nomi:</b> ").append(escapeHtml(video.title)).append("\n")
            headerSb.append("🔗 <b>Havola:</b> ").append(video.watchUrl).append("\n")
            headerSb.append("⏱ <b>Davomiyligi:</b> ").append(video.formattedDuration).append("\n\n")

            if (summary != null && summary.summaryUz.isNotBlank()) {
                headerSb.append("💡 <b>AI Xulosa & Konspekt:</b>\n")
                headerSb.append(escapeHtml(summary.summaryUz)).append("\n\n")

                if (summary.bulletPointsUz.isNotEmpty()) {
                    headerSb.append("✨ <b>Asosiy fikrlar:</b>\n")
                    summary.bulletPointsUz.forEach { bp ->
                        headerSb.append("• ").append(escapeHtml(bp)).append("\n")
                    }
                    headerSb.append("\n")
                }
            }

            headerSb.append("📝 <i>Quyida videoning vaqtga moslangan jonli o'zbekcha subtitrlari:</i>")

            // Send header
            val headerResult = sendMessage(cleanToken, chatId, headerSb.toString())
            if (headerResult.isFailure) {
                return@withContext headerResult
            }

            // 2. Prepare Transcripts in batches (Telegram has 4096 char limit per message)
            val batchSb = StringBuilder()
            batchSb.append("📋 <b>Vaqt kodlari va Subtitrlar:</b>\n\n")

            var totalSentParts = 1
            for (item in transcripts) {
                val line = "⏱ <code>[${item.formattedStartTime}]</code> <b>${escapeHtml(item.textUz)}</b>\n"
                if (batchSb.length + line.length > 3800) {
                    sendMessage(cleanToken, chatId, batchSb.toString())
                    batchSb.clear()
                    totalSentParts++
                }
                batchSb.append(line)
            }

            if (batchSb.isNotBlank()) {
                val subResult = sendMessage(cleanToken, chatId, batchSb.toString())
                if (subResult.isFailure) {
                    return@withContext subResult
                }
            }

            Result.success("Muvaffaqiyatli yuborildi! Telegramga ($chatId) transkript va xulosa yetkazildi.")
        } catch (e: Exception) {
            Log.e("TelegramBotService", "Error sending to telegram", e)
            Result.failure(Exception("Telegramga yuborishda xatolik: ${e.localizedMessage}"))
        }
    }

    private fun sendMessage(token: String, chatId: String, text: String): Result<String> {
        val url = "https://api.telegram.org/bot$token/sendMessage"
        val jsonPayload = JSONObject().apply {
            put("chat_id", chatId)
            put("text", text)
            put("parse_mode", "HTML")
            put("disable_web_page_preview", false)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonPayload.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                Result.success(responseBody)
            } else {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody)
                    errJson.optString("description", "Status: ${response.code}")
                } catch (_: Exception) {
                    "Server javobi: ${response.code}"
                }
                Result.failure(Exception("Telegram API xatosi: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
    }
}
