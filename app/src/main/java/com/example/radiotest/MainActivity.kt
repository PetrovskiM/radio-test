package com.example.radiotest

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.radiotest.core.common.ui.theme.AppTheme
import com.example.radiotest.feature.player.presentation.PlaybackService
import com.example.radiotest.feature.player.presentation.PlayerScreen
import com.example.radiotest.feature.player.presentation.PlayerViewModel
import com.example.radiotest.feature.player.presentation.contract.PlayerViewAction.TogglePlayState
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var mediaController: MediaController
    private val viewModel: PlayerViewModel by viewModels()

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        prepareMediaPlayer()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerScreen(
                        state = state,
                        togglePlayingState = {
                            viewModel.handleViewAction(TogglePlayState)
                            if (state.isPlaying) {
                                mediaController.pause()
                            } else {
                                mediaController.play()
                            }
                        },
                    )
                }
            }
        }
    }

    private fun prepareMediaPlayer() {
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken)
            .buildAsync()
        controllerFuture.addListener(
            /* listener = */{ mediaController = controllerFuture.get() },
            /* executor = */ MoreExecutors.directExecutor(),
        )
    }

    override fun onDestroy() {
        if (::mediaController.isInitialized) {
            mediaController.release()
        }
        super.onDestroy()
    }
}
