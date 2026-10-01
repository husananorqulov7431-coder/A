package com.example.player

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

class YouTubeBridge(
    private val onTimeUpdated: (Float, Float) -> Unit,
    private val onPlayerReadyCallback: () -> Unit,
    private val onPlayerStateChanged: (Int) -> Unit
) {
    @JavascriptInterface
    fun onTimeUpdate(currentTimeSec: Float, durationSec: Float) {
        onTimeUpdated(currentTimeSec, durationSec)
    }

    @JavascriptInterface
    fun onPlayerReady() {
        onPlayerReadyCallback()
    }

    @JavascriptInterface
    fun onStateChange(state: Int) {
        onPlayerStateChanged(state)
    }
}

class YouTubePlayerController {
    private var webViewRef: WebView? = null

    fun attach(webView: WebView) {
        webViewRef = webView
    }

    fun detach() {
        webViewRef = null
    }

    fun seekTo(seconds: Float) {
        webViewRef?.post {
            webViewRef?.evaluateJavascript("seekTo($seconds);", null)
        }
    }

    fun play() {
        webViewRef?.post {
            webViewRef?.evaluateJavascript("playVideo();", null)
        }
    }

    fun pause() {
        webViewRef?.post {
            webViewRef?.evaluateJavascript("pauseVideo();", null)
        }
    }

    fun loadVideo(videoId: String) {
        webViewRef?.post {
            webViewRef?.evaluateJavascript("loadVideoById('$videoId');", null)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeWebViewPlayer(
    videoId: String,
    controller: YouTubePlayerController,
    onTimeUpdate: (Float, Float) -> Unit,
    onPlayerReady: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val bridge = remember(onTimeUpdate, onPlayerReady) {
        YouTubeBridge(
            onTimeUpdated = { time, dur -> onTimeUpdate(time, dur) },
            onPlayerReadyCallback = onPlayerReady,
            onPlayerStateChanged = { _ -> }
        )
    }

    val webView = remember(context) {
        WebView(context).apply {
            setBackgroundColor(android.graphics.Color.BLACK)
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                cacheMode = WebSettings.LOAD_DEFAULT
                loadWithOverviewMode = true
                useWideViewPort = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
            addJavascriptInterface(bridge, "AndroidBridge")
        }
    }

    DisposableEffect(webView) {
        controller.attach(webView)
        onDispose {
            controller.detach()
            webView.stopLoading()
            webView.destroy()
        }
    }

    LaunchedEffect(videoId) {
        val html = buildYouTubeHtml(videoId)
        webView.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun buildYouTubeHtml(videoId: String): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
          <style>
            * { margin: 0; padding: 0; box-sizing: border-box; background: #000000; }
            html, body { width: 100%; height: 100%; overflow: hidden; background: #000000; }
            #player { width: 100%; height: 100%; position: absolute; top: 0; left: 0; }
          </style>
        </head>
        <body>
          <div id="player"></div>
          <script>
            var tag = document.createElement('script');
            tag.src = "https://www.youtube.com/iframe_api";
            var firstScriptTag = document.getElementsByTagName('script')[0];
            firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

            var player;
            function onYouTubeIframeAPIReady() {
              player = new YT.Player('player', {
                height: '100%',
                width: '100%',
                videoId: '$videoId',
                playerVars: {
                  'autoplay': 1,
                  'playsinline': 1,
                  'controls': 1,
                  'rel': 0,
                  'fs': 1,
                  'modestbranding': 1,
                  'origin': 'https://www.youtube.com'
                },
                events: {
                  'onReady': onPlayerReady,
                  'onStateChange': onPlayerStateChange
                }
              });
            }

            function onPlayerReady(event) {
              if (window.AndroidBridge && window.AndroidBridge.onPlayerReady) {
                window.AndroidBridge.onPlayerReady();
              }
              setInterval(function() {
                if (player && typeof player.getCurrentTime === 'function') {
                  var cur = player.getCurrentTime();
                  var dur = (typeof player.getDuration === 'function') ? player.getDuration() : 0;
                  if (window.AndroidBridge && window.AndroidBridge.onTimeUpdate) {
                    window.AndroidBridge.onTimeUpdate(cur, dur);
                  }
                }
              }, 250);
            }

            function onPlayerStateChange(event) {
              if (window.AndroidBridge && window.AndroidBridge.onStateChange) {
                window.AndroidBridge.onStateChange(event.data);
              }
            }

            function seekTo(sec) {
              if (player && typeof player.seekTo === 'function') {
                player.seekTo(sec, true);
              }
            }

            function playVideo() {
              if (player && typeof player.playVideo === 'function') {
                player.playVideo();
              }
            }

            function pauseVideo() {
              if (player && typeof player.pauseVideo === 'function') {
                player.pauseVideo();
              }
            }

            function loadVideoById(id) {
              if (player && typeof player.loadVideoById === 'function') {
                player.loadVideoById(id);
              }
            }
          </script>
        </body>
        </html>
    """.trimIndent()
}
