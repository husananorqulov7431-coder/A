package com.example.model

enum class PipelineStatus {
    IDLE,
    ANALYZING,
    BUFFERING_CHUNK_1, // Initial 1-minute buffer in progress
    STREAMING_ACTIVE,  // First 1-min ready, video playing, background buffering next chunks
    COMPLETED,         // All chunks translated and synced
    ERROR
}

data class ProcessingState(
    val status: PipelineStatus = PipelineStatus.IDLE,
    val bufferedSeconds: Float = 0f,
    val totalSeconds: Float = 300f,
    val currentChunkIndex: Int = 0,
    val totalChunks: Int = 5,
    val chunkProgressPercent: Float = 0f,
    val overallProgressPercent: Float = 0f,
    val statusMessage: String = "Video havolasini kiriting yoki namunalardan birini tanlang",
    val errorMessage: String? = null,
    val canStartPlayback: Boolean = false
) {
    val isProcessing: Boolean
        get() = status == PipelineStatus.ANALYZING ||
                status == PipelineStatus.BUFFERING_CHUNK_1 ||
                (status == PipelineStatus.STREAMING_ACTIVE && currentChunkIndex < totalChunks)

    val formattedBufferTime: String
        get() {
            val min = (bufferedSeconds / 60).toInt()
            val sec = (bufferedSeconds % 60).toInt()
            return String.format("%02d:%02d", min, sec)
        }
}
