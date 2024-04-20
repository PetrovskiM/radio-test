package com.radio.broadcast

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.radio.broadcast.core.common.ui.theme.AppTheme
import com.radio.broadcast.di.FirebaseUrls
import com.radio.broadcast.feature.player.presentation.PlaybackService
import com.radio.broadcast.feature.player.presentation.PlayerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var mediaController: MediaController

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            var isLoadingFirebaseData by remember {
                mutableStateOf(true)
            }
            fetchFirebaseData(onDataFetched = { isLoadingFirebaseData = false })
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoadingFirebaseData) {
                        PlayerScreen(
                            mediaControllerPause = { mediaController.pause() },
                            mediaControllerPlay = { mediaController.play() },
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        }
                    }
                }
            }
        }
    }

    private fun fetchFirebaseData(onDataFetched: () -> Unit) {
        val database = Firebase.database.getReference()
        database
            .get()
            .addOnSuccessListener { data ->
                data?.let {
                    val urls = data.child("urls").children
                    val programs = data.child("programa").children
                    FirebaseUrls.radioUrl = urls.first().value.toString()
                    FirebaseUrls.nowPlayingUrl = urls.last()
                        .value
                        .toString()
                        .split("nowplaying")
                        .first()
                    prepareMediaPlayer()
                    onDataFetched()
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
