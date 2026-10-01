package com.example.model

data class TranscriptItem(
    val id: String,
    val startSeconds: Float,
    val endSeconds: Float,
    val textUz: String,
    val textOrig: String = "",
    val chunkIndex: Int = 0
) {
    val formattedTimestamp: String
        get() {
            val startMin = (startSeconds / 60).toInt()
            val startSec = (startSeconds % 60).toInt()
            val endMin = (endSeconds / 60).toInt()
            val endSec = (endSeconds % 60).toInt()
            return String.format("%02d:%02d - %02d:%02d", startMin, startSec, endMin, endSec)
        }

    val formattedStartTime: String
        get() {
            val min = (startSeconds / 60).toInt()
            val sec = (startSeconds % 60).toInt()
            return String.format("%02d:%02d", min, sec)
        }
}

data class VideoSummary(
    val mainTopicUz: String = "",
    val summaryUz: String = "",
    val bulletPointsUz: List<String> = emptyList(),
    val keyTerms: Map<String, String> = emptyMap()
)
