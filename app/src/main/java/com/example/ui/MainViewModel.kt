package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.SavedVideoEntity
import com.example.model.PipelineStatus
import com.example.model.ProcessingState
import com.example.model.TelegramConfig
import com.example.model.TranscriptItem
import com.example.model.VideoInfo
import com.example.model.VideoSummary
import com.example.network.GeminiService
import com.example.network.PresetVideos
import com.example.network.TelegramBotService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val videoDao = db.videoDao()
    private val geminiService = GeminiService()
    private val telegramBotService = TelegramBotService()

    val savedVideos: StateFlow<List<SavedVideoEntity>> = videoDao.getAllVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentVideo = MutableStateFlow<VideoInfo?>(PresetVideos.sampleVideos.first())
    val currentVideo: StateFlow<VideoInfo?> = _currentVideo.asStateFlow()

    private val _processingState = MutableStateFlow(
        ProcessingState(
            status = PipelineStatus.STREAMING_ACTIVE,
            bufferedSeconds = 60f,
            totalSeconds = 240f,
            currentChunkIndex = 1,
            totalChunks = 4,
            chunkProgressPercent = 1f,
            overallProgressPercent = 0.25f,
            statusMessage = "1-Daqiqa tayyor! Video ko'rsatilmoqda ⚡",
            canStartPlayback = true
        )
    )
    val processingState: StateFlow<ProcessingState> = _processingState.asStateFlow()

    private val _transcripts = MutableStateFlow<List<TranscriptItem>>(
        PresetVideos.getInitialPreloadedCues("dQw4w9WgXcQ")
    )
    val transcripts: StateFlow<List<TranscriptItem>> = _transcripts.asStateFlow()

    private val _summary = MutableStateFlow<VideoSummary?>(
        VideoSummary(
            mainTopicUz = "Sun'iy intellekt va Gemini texnologiyalari",
            summaryUz = "Videoda Gemini AI va multimodal modellar audio, video hamda matnni real vaqtda qayta ishlashi haqida tushuntiriladi. 1-daqiqadan boshlab o'zbek tilidagi jonli subtitrlar to'liq sinxronlashtirilgan.",
            bulletPointsUz = listOf(
                "Gemini arxitekturasi va multimodal oqimlar tezligi",
                "Real vaqtda ovozni o'zbek tiliga sinxron tarjima qilish",
                "Video oqimlarini qurilma xotirasiga yuklamasdan bevosita tahlil qilish"
            )
        )
    )
    val summary: StateFlow<VideoSummary?> = _summary.asStateFlow()

    private val _currentTimeSec = MutableStateFlow(0f)
    val currentTimeSec: StateFlow<Float> = _currentTimeSec.asStateFlow()

    private val _durationSec = MutableStateFlow(240f)
    val durationSec: StateFlow<Float> = _durationSec.asStateFlow()

    val activeTranscript: StateFlow<TranscriptItem?> = combine(_transcripts, _currentTimeSec) { list, time ->
        list.find { time >= it.startSeconds && time <= it.endSeconds }
            ?: list.find { time >= it.startSeconds - 0.5f && time <= it.endSeconds + 0.5f }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _telegramConfig = MutableStateFlow(TelegramConfig())
    val telegramConfig: StateFlow<TelegramConfig> = _telegramConfig.asStateFlow()

    private val _showBilingual = MutableStateFlow(false)
    val showBilingual: StateFlow<Boolean> = _showBilingual.asStateFlow()

    private val _fontSizeMultiplier = MutableStateFlow(1.0f)
    val fontSizeMultiplier: StateFlow<Float> = _fontSizeMultiplier.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private var pipelineJob: Job? = null

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun toggleBilingual() {
        _showBilingual.value = !_showBilingual.value
    }

    fun setFontSizeMultiplier(multiplier: Float) {
        _fontSizeMultiplier.value = multiplier
    }

    fun updatePlaybackTime(time: Float, duration: Float) {
        _currentTimeSec.value = time
        if (duration > 10f && _durationSec.value != duration) {
            _durationSec.value = duration
        }
    }

    fun startPipeline(urlOrId: String, title: String? = null) {
        val videoId = PresetVideos.extractYouTubeId(urlOrId) ?: urlOrId.trim()
        val videoTitle = title ?: "YouTube Video ($videoId)"
        val video = VideoInfo(
            videoId = videoId,
            title = videoTitle,
            durationSeconds = 240f
        )
        _currentVideo.value = video

        pipelineJob?.cancel()
        pipelineJob = viewModelScope.launch {
            runProgressiveBufferPipeline(video)
        }
    }

    private suspend fun runProgressiveBufferPipeline(video: VideoInfo) {
        _transcripts.value = emptyList()
        _currentTimeSec.value = 0f
        _durationSec.value = video.durationSeconds

        // Step 1: Analyzing & Connecting
        _processingState.value = ProcessingState(
            status = PipelineStatus.ANALYZING,
            bufferedSeconds = 0f,
            totalSeconds = video.durationSeconds,
            currentChunkIndex = 0,
            totalChunks = (video.durationSeconds / 60f).toInt().coerceIn(3, 8),
            statusMessage = "Video tekshirilmoqda va 1-daqiqa oqimi ulanmoqda..."
        )
        delay(600)

        // Step 2: Buffering Chunk 1 [00:00 - 01:00] (Music generation / AI Studio progressive style)
        _processingState.value = _processingState.value.copy(
            status = PipelineStatus.BUFFERING_CHUNK_1,
            statusMessage = "1-Daqiqa (00:00 - 01:00) o'zbekcha transkript qilinmoqda..."
        )

        // Progressive chunk 1 visual buffering
        for (i in 1..5) {
            delay(280)
            _processingState.value = _processingState.value.copy(
                chunkProgressPercent = i / 5f,
                overallProgressPercent = (i / 5f) * 0.25f,
                bufferedSeconds = (i / 5f) * 60f
            )
        }

        // Fetch or synthesize chunk 1
        val chunk0Items = geminiService.processChunkWithGemini(
            video = video,
            chunkIndex = 0,
            startSec = 0f,
            endSec = 60f
        )
        _transcripts.value = chunk0Items

        // Step 3: First 1-minute is ready! Video begins playback now!
        _processingState.value = _processingState.value.copy(
            status = PipelineStatus.STREAMING_ACTIVE,
            bufferedSeconds = 60f,
            canStartPlayback = true,
            statusMessage = "1-Daqiqa tayyor! Video ko'rsatilmoqda ⚡ Keyingi qismlar yuklanmoqda..."
        )

        // Step 4: Background progressive buffering of subsequent chunks (Chunk 1: 60-120s, Chunk 2: 120-180s, etc.)
        val totalChunks = _processingState.value.totalChunks
        for (chunkIdx in 1 until totalChunks) {
            val start = chunkIdx * 60f
            val end = (start + 60f).coerceAtMost(video.durationSeconds)

            _processingState.value = _processingState.value.copy(
                currentChunkIndex = chunkIdx,
                chunkProgressPercent = 0.5f,
                statusMessage = "${chunkIdx + 1}-Daqiqa o'zbek tiliga sinxronlashtirilmoqda..."
            )
            delay(1200)

            val nextItems = geminiService.processChunkWithGemini(
                video = video,
                chunkIndex = chunkIdx,
                startSec = start,
                endSec = end
            )
            _transcripts.value = _transcripts.value + nextItems
            val newBuffered = end
            _processingState.value = _processingState.value.copy(
                bufferedSeconds = newBuffered,
                chunkProgressPercent = 1f,
                overallProgressPercent = (chunkIdx + 1) / totalChunks.toFloat()
            )
        }

        // Step 5: Finished all chunks! Generate summary
        _processingState.value = _processingState.value.copy(
            status = PipelineStatus.COMPLETED,
            bufferedSeconds = video.durationSeconds,
            overallProgressPercent = 1f,
            statusMessage = "To'liq video o'zbekcha subtitrlashtirildi!"
        )

        val summaryResult = geminiService.generateSummaryWithGemini(
            video = video,
            transcripts = _transcripts.value
        )
        _summary.value = summaryResult

        // Auto save to Room database
        saveVideoToDatabase(video, _transcripts.value, summaryResult)
    }

    private fun saveVideoToDatabase(
        video: VideoInfo,
        transcriptsList: List<TranscriptItem>,
        summary: VideoSummary?
    ) {
        viewModelScope.launch {
            try {
                val array = JSONArray()
                transcriptsList.forEach { item ->
                    val obj = JSONObject().apply {
                        put("id", item.id)
                        put("start", item.startSeconds)
                        put("end", item.endSeconds)
                        put("textUz", item.textUz)
                        put("textOrig", item.textOrig)
                        put("chunk", item.chunkIndex)
                    }
                    array.put(obj)
                }

                val entity = SavedVideoEntity(
                    videoId = video.videoId,
                    title = video.title,
                    channel = video.channelTitle,
                    url = video.watchUrl,
                    thumbnailUrl = video.thumbnailUrl,
                    durationSeconds = video.durationSeconds,
                    transcriptJson = array.toString(),
                    summaryUz = summary?.summaryUz ?: ""
                )
                videoDao.insertVideo(entity)
            } catch (_: Exception) {}
        }
    }

    fun selectSavedVideo(entity: SavedVideoEntity) {
        pipelineJob?.cancel()
        val video = VideoInfo(
            videoId = entity.videoId,
            title = entity.title,
            channelTitle = entity.channel,
            durationSeconds = entity.durationSeconds,
            thumbnailUrl = entity.thumbnailUrl
        )
        _currentVideo.value = video

        // Restore transcripts from JSON
        val list = mutableListOf<TranscriptItem>()
        try {
            val array = JSONArray(entity.transcriptJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TranscriptItem(
                        id = obj.optString("id", i.toString()),
                        startSeconds = obj.optDouble("start", 0.0).toFloat(),
                        endSeconds = obj.optDouble("end", 5.0).toFloat(),
                        textUz = obj.optString("textUz", ""),
                        textOrig = obj.optString("textOrig", ""),
                        chunkIndex = obj.optInt("chunk", 0)
                    )
                )
            }
        } catch (_: Exception) {}

        _transcripts.value = list
        _summary.value = VideoSummary(
            mainTopicUz = entity.title,
            summaryUz = entity.summaryUz
        )
        _processingState.value = ProcessingState(
            status = PipelineStatus.COMPLETED,
            bufferedSeconds = entity.durationSeconds,
            totalSeconds = entity.durationSeconds,
            canStartPlayback = true,
            statusMessage = "Saqlangan videodan tiklandi"
        )
        _selectedTab.value = 0
    }

    fun deleteSavedVideo(videoId: String) {
        viewModelScope.launch {
            videoDao.deleteVideo(videoId)
        }
    }

    fun sendToTelegram(botToken: String, chatId: String) {
        val video = _currentVideo.value ?: return
        val list = _transcripts.value
        val sum = _summary.value

        _telegramConfig.value = _telegramConfig.value.copy(
            botToken = botToken,
            chatId = chatId,
            isSending = true,
            lastStatusMessage = "Telegramga yuborilmoqda..."
        )

        viewModelScope.launch {
            val result = telegramBotService.sendTranscriptToTelegram(
                botToken = botToken,
                chatId = chatId,
                video = video,
                transcripts = list,
                summary = sum
            )

            result.fold(
                onSuccess = { msg ->
                    _telegramConfig.value = _telegramConfig.value.copy(
                        isSending = false,
                        sendSuccess = true,
                        lastSentTime = System.currentTimeMillis(),
                        lastStatusMessage = msg
                    )
                    videoDao.markTelegramSent(video.videoId)
                },
                onFailure = { err ->
                    _telegramConfig.value = _telegramConfig.value.copy(
                        isSending = false,
                        sendSuccess = false,
                        lastStatusMessage = err.message ?: "Xatolik yuz berdi"
                    )
                }
            )
        }
    }
}
