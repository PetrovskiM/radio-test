package com.radio.broadcast

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.common.util.concurrent.MoreExecutors
import com.radio.broadcast.core.common.ui.BottomBar
import com.radio.broadcast.core.common.ui.ImmutableList
import com.radio.broadcast.core.common.ui.LoadingIndicator
import com.radio.broadcast.core.common.ui.TopLevelDestination
import com.radio.broadcast.core.common.ui.navigateToBottomBarDestination
import com.radio.broadcast.core.common.ui.theme.AppTheme
import com.radio.broadcast.core.common.ui.topLevelDestinations
import com.radio.broadcast.feature.player.presentation.PlaybackService
import com.radio.broadcast.feature.player.presentation.PlayerScreen
import com.radio.broadcast.feature.schedule.ScheduleScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var mediaController: MediaController
    private val viewModel by viewModels<MainViewModel>()

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        subscribeToSideEffects()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (!state.isLoading) {
                            BottomBar(
                                screens = topLevelDestinations,
                                currentDestination = currentDestination,
                                navigateToTopLevelDestination = navController::navigateToBottomBarDestination,
                            )
                        }
                    }
                ) { paddingValues ->
                    if (!state.isLoading) {
                        NavHost(
                            modifier = Modifier
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues)
                                .fillMaxSize(),
                            navController = navController,
                            startDestination = TopLevelDestination.Radio.route,
                        ) {
                            composable(route = TopLevelDestination.Radio.route) {
                                PlayerScreen(
                                    mediaControllerPause = { mediaController.pause() },
                                    mediaControllerPlay = { mediaController.play() },
                                )
                            }
                            composable(route = TopLevelDestination.Schedule.route) {
                                ScheduleScreen(schedule = state.schedule)
                            }
                        }
                    } else {
                        LoadingIndicator()
                    }
                }
            }
        }
    }

    private fun subscribeToSideEffects() {
        lifecycleScope.launch {
            viewModel.sideEffects.collect { shouldPrepareMediaPlayer ->
                if (shouldPrepareMediaPlayer) {
                    prepareMediaPlayer()
                }
            }
        }
    }

    private fun prepareMediaPlayer() {
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken)
            .buildAsync()
        controllerFuture.addListener(
            /* listener = */ { mediaController = controllerFuture.get() },
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
