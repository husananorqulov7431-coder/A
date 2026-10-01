package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM saved_videos ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<SavedVideoEntity>>

    @Query("SELECT * FROM saved_videos WHERE videoId = :videoId LIMIT 1")
    suspend fun getVideoById(videoId: String): SavedVideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: SavedVideoEntity)

    @Query("DELETE FROM saved_videos WHERE videoId = :videoId")
    suspend fun deleteVideo(videoId: String)

    @Query("UPDATE saved_videos SET telegramSent = 1 WHERE videoId = :videoId")
    suspend fun markTelegramSent(videoId: String)
}
