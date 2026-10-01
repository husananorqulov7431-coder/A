package com.example.model

data class VideoInfo(
    val videoId: String,
    val title: String,
    val channelTitle: String = "YouTube",
    val durationSeconds: Float = 300f,
    val thumbnailUrl: String = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
) {
    val embedUrl: String
        get() = "https://www.youtube.com/embed/$videoId?enablejsapi=1&autoplay=1&playsinline=1&rel=0&controls=1"

    val watchUrl: String
        get() = "https://www.youtube.com/watch?v=$videoId"

    val formattedDuration: String
        get() {
            val min = (durationSeconds / 60).toInt()
            val sec = (durationSeconds % 60).toInt()
            return String.format("%02d:%02d", min, sec)
        }
}
