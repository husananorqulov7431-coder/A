package com.example

import com.example.model.TranscriptItem
import com.example.model.VideoInfo
import com.example.network.PresetVideos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testYouTubeIdExtraction() {
        val url1 = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        val id1 = PresetVideos.extractYouTubeId(url1)
        assertEquals("dQw4w9WgXcQ", id1)

        val url2 = "https://youtu.be/jNQXAC9IVRw?si=xyz123"
        val id2 = PresetVideos.extractYouTubeId(url2)
        assertEquals("jNQXAC9IVRw", id2)

        val url3 = "dQw4w9WgXcQ"
        val id3 = PresetVideos.extractYouTubeId(url3)
        assertEquals("dQw4w9WgXcQ", id3)
    }

    @Test
    fun testTranscriptTimestampFormatting() {
        val item = TranscriptItem(
            id = "test_1",
            startSeconds = 65f,
            endSeconds = 72f,
            textUz = "Sinov matni"
        )
        assertEquals("01:05", item.formattedStartTime)
        assertEquals("01:05 - 01:12", item.formattedTimestamp)
    }

    @Test
    fun testVideoInfoFormat() {
        val video = VideoInfo(
            videoId = "dQw4w9WgXcQ",
            title = "Test Video",
            durationSeconds = 185f
        )
        assertEquals("03:05", video.formattedDuration)
        assertNotNull(video.embedUrl)
    }
}
