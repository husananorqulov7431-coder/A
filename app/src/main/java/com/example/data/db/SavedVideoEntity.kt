package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_videos")
data class SavedVideoEntity(
    @PrimaryKey
    val videoId: String,
    val title: String,
    val channel: String,
    val url: String,
    val thumbnailUrl: String,
    val durationSeconds: Float,
    val transcriptJson: String,
    val summaryUz: String,
    val createdAt: Long = System.currentTimeMillis(),
    val telegramSent: Boolean = false
)
