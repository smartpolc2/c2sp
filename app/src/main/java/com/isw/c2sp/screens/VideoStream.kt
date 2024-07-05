package com.isw.c2sp.screens

import android.content.res.Configuration
import android.util.Log
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING


data class StreamConfig(
    val streamName: String,
    val ip: String,
    val port: Int,
    val path: String,
    val username: String? = null,
    val password: String? = null,
    val forceRtpTcp: Boolean
) {
    override fun toString(): String {
        return if (username == "" && password == "") {
            "rtsp://$ip:$port/$path"
        } else {
            "rtsp://$username:$password@$ip:$port/$path"
        }
    }
}

@Composable
@OptIn(UnstableApi::class)
fun VideoPlayer(config: StreamConfig){
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp

    val ratio =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 16 / 9f else 1f

    val surface = SurfaceView(context)

    val mediaSource =
        RtspMediaSource.Factory()
            .setForceUseRtpTcp(config.forceRtpTcp)
            .createMediaSource(MediaItem.fromUri(config.toString()))

    val renderer =
        DefaultRenderersFactory(context).apply {
            setEnableDecoderFallback(true)
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        }

    val listener =
        object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(
                    context,
                    "Stream ${config.ip}: ${error.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
                super.onPlayerError(error)
            }
        }

    val exoPlayer = remember {
        ExoPlayer.Builder(context, renderer).build().apply {
            setMediaSource(mediaSource)
            setVideoSurfaceView(surface)
            setVideoSurfaceHolder(surface.holder)
            addAnalyticsListener(EventLogger())
            addListener(listener)
            prepare()
            playWhenReady = true
        }
    }


    val defaultPlayerView = remember {
        PlayerView(context).apply {
            player = exoPlayer
            useController = false
            keepScreenOn = true
            layoutParams = FrameLayout.LayoutParams(screenWidth, screenHeight)

            setShowPreviousButton(false)
            setShowNextButton(false)
            setShowSubtitleButton(false)
            setShowFastForwardButton(false)
            setShowRewindButton(false)
            setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING)
        }
    }

    DisposableEffect(Unit) { onDispose { exoPlayer.release() } }

    AndroidView(
        modifier =
        Modifier.fillMaxSize(1f)
            .onKeyEvent {
                Log.d("VideoPlayer", "onKeyEvent: $it")
                true
            },
        factory = { defaultPlayerView }
    )
}

const val EXAMPLE_VIDEO_URI = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

@Composable
@OptIn(UnstableApi::class)
fun VidePlayerSimple(config:StreamConfig){


    val context = LocalContext.current

    val exoPlayer = ExoPlayer.Builder(context).build()

    /*
    // Create a MediaSource
    val mediaSource = remember(EXAMPLE_VIDEO_URI) {
        MediaItem.fromUri(EXAMPLE_VIDEO_URI)
    }

     */

    val mediaSource =
        RtspMediaSource.Factory()
            .setForceUseRtpTcp(config.forceRtpTcp)
            .createMediaSource(MediaItem.fromUri(config.toString()))


    // Set MediaSource to ExoPlayer
    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaSource(mediaSource)
        //exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    // Manage lifecycle events
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Use AndroidView to embed an Android View (PlayerView) into Compose
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Set your desired height
    )


}