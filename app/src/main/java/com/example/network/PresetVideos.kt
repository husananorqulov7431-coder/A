package com.example.network

import com.example.model.TranscriptItem
import com.example.model.VideoInfo
import java.util.regex.Pattern

object PresetVideos {

    fun extractYouTubeId(urlOrId: String): String? {
        val trimmed = urlOrId.trim()
        if (trimmed.length == 11 && !trimmed.contains("/") && !trimmed.contains("?")) {
            return trimmed
        }

        // Patterns for youtube.com/watch?v=ID, youtu.be/ID, youtube.com/embed/ID, youtube.com/shorts/ID
        val pattern = "^(?:https?:\\/\\/)?(?:www\\.|m\\.)?(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=|shorts\\/))([\\w-]{11})(?:\\S+)?$"
        val compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
        val matcher = compiledPattern.matcher(trimmed)
        if (matcher.find()) {
            return matcher.group(1)
        }

        // Fallback simple query extractor
        if (trimmed.contains("v=")) {
            val part = trimmed.substringAfter("v=").substringBefore("&").substringBefore("?")
            if (part.length == 11) return part
        }
        if (trimmed.contains("youtu.be/")) {
            val part = trimmed.substringAfter("youtu.be/").substringBefore("?").substringBefore("/")
            if (part.length == 11) return part
        }

        return null
    }

    val sampleVideos = listOf(
        VideoInfo(
            videoId = "dQw4w9WgXcQ", // Classic music/demo video ID, reliable embed
            title = "Sun'iy intellekt va Gemini texnologiyalari: Kelajak qanday bo'ladi?",
            channelTitle = "Google AI & Tech Talks",
            durationSeconds = 240f
        ),
        VideoInfo(
            videoId = "jNQXAC9IVRw", // "Me at the zoo" - First YouTube video, always playable
            title = "YouTube va raqamli media evolyutsiyasi: Tarixdan bugungi kungacha",
            channelTitle = "Digital History & Tech",
            durationSeconds = 180f
        ),
        VideoInfo(
            videoId = "9bZkp7q19f0", // Psy Gangnam Style - highly available video
            title = "Global media va video oqimlarning real vaqtda ishlash sirlari",
            channelTitle = "Global Media Insights",
            durationSeconds = 250f
        ),
        VideoInfo(
            videoId = "L_LUpnjgPso",
            title = "Koinot va Kvant fizikasi: Yangi ilmiy kashfiyotlar",
            channelTitle = "Science & Space UZ",
            durationSeconds = 300f
        )
    )

    fun getInitialPreloadedCues(videoId: String): List<TranscriptItem> {
        return listOf(
            TranscriptItem(
                id = "pre_0_1",
                startSeconds = 0f,
                endSeconds = 9.5f,
                textUz = "Assalomu alaykum! UzTube Sub jonli transkripsiya tizimiga xush kelibsiz.",
                textOrig = "Welcome everyone to our live synchronized subtitle platform.",
                chunkIndex = 0
            ),
            TranscriptItem(
                id = "pre_0_2",
                startSeconds = 10f,
                endSeconds = 19.5f,
                textUz = "Dastlabki 1 daqiqalik qism to'liq o'zbek tiliga tarjima qilindi va video ko'rsatilmoqda.",
                textOrig = "The initial one minute buffer has been processed and video starts streaming.",
                chunkIndex = 0
            ),
            TranscriptItem(
                id = "pre_0_3",
                startSeconds = 20f,
                endSeconds = 29.5f,
                textUz = "Video yuklanmasdan, to'g'ridan-to'g'ri oqimli rejimda real vaqtda tomosha qilinadi.",
                textOrig = "The video streams directly without prior downloading, in true real-time mode.",
                chunkIndex = 0
            ),
            TranscriptItem(
                id = "pre_0_4",
                startSeconds = 30f,
                endSeconds = 39.5f,
                textUz = "Ayni paytda orqa fonda keyingi daqiqalar Gemini AI orqali uzluksiz tarjima qilinmoqda.",
                textOrig = "Meanwhile, subsequent minutes are continuously processed by Gemini in the background.",
                chunkIndex = 0
            ),
            TranscriptItem(
                id = "pre_0_5",
                startSeconds = 40f,
                endSeconds = 49.5f,
                textUz = "Har bir gap vaqt kodlari bilan qat'iy sinxronlangan holda ekranda aks etadi.",
                textOrig = "Every sentence is strictly synchronized with its exact timestamp on screen.",
                chunkIndex = 0
            ),
            TranscriptItem(
                id = "pre_0_6",
                startSeconds = 50f,
                endSeconds = 59.9f,
                textUz = "Xohlagan daqiqaga o'tish uchun pastdagi transkript ro'yxatidagi gapga bosing!",
                textOrig = "Click any line in the transcript below to jump immediately to that exact moment!",
                chunkIndex = 0
            )
        )
    }
}
