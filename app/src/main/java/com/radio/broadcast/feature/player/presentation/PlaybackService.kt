package com.radio.broadcast.feature.player.presentation

import android.content.Intent
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.radio.broadcast.di.FirebaseUrls
import com.radio.broadcast.di.MediaMetadataObserver
import com.radio.broadcast.di.MediaPlaybackObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    @Inject
    @MediaMetadataObserver
    lateinit var metadataObserver: MutableStateFlow<String>

    @Inject
    @MediaPlaybackObserver
    lateinit var playbackObserver: MutableStateFlow<Boolean>

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
            .also {
                it.prepare()
                it.setMediaItem(MediaItem.fromUri(FirebaseUrls.radioUrl))
                it.playWhenReady = true
            }
        mediaSession = MediaSession.Builder(this, player).build()
        mediaSession!!.player.addListener(object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                //Note: Artwork is always null, everything else is null except title which is
                //Artist and song name combined. We initially show this, then we the socket comes
                //With appropriately formatted data with a delay, we change to socket data
                coroutineScope.launch {
                    metadataObserver.emit(mediaMetadata.title.toString())
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                coroutineScope.launch {
                    playbackObserver.emit(isPlaying)
                }
            }
        })
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady == true) {
            player.pause()
        }
        stopSelf()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        if (coroutineScope.isActive) {
            try {
                coroutineScope.cancel()
            } catch (e: Throwable) {
                Log.d("PlaybackService", "Failed to cancel coroutine scope: $e")
            }
        }
        super.onDestroy()
    }
}