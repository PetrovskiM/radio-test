package com.radio.broadcast.feature.player.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.radio.broadcast.R
import com.radio.broadcast.core.common.ui.GlowCard
import com.radio.broadcast.core.common.ui.RText.BodyMedium
import com.radio.broadcast.core.common.ui.RText.HeadlineMedium
import com.radio.broadcast.core.common.ui.RText.HeadlineSmall
import com.radio.broadcast.core.common.ui.buildImageRequest
import com.radio.broadcast.core.common.ui.theme.Dimens.Space
import com.radio.broadcast.core.common.ui.theme.Dimens.SpaceXXLarge
import com.radio.broadcast.feature.player.presentation.contract.PlayerViewAction
import com.radio.broadcast.feature.player.presentation.contract.RadioState

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    mediaControllerPlay: () -> Unit,
    mediaControllerPause: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PlayerScreenContent(
        state = state,
        togglePlayingState = {
            viewModel.handleViewAction(PlayerViewAction.TogglePlayState)
            if (state.isPlaying) {
                mediaControllerPause()
            } else {
                mediaControllerPlay()
            }
        }
    )
}

@Composable
fun PlayerScreenContent(
    state: RadioState,
    togglePlayingState: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val imageSize = remember {
        (configuration.screenWidthDp / IMAGE_SIZE_FRACTION).dp
    }
    val glow = MaterialTheme.colorScheme.primary.copy(alpha = GLOW_ALPHA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = CenterHorizontally
    ) {
        HeadlineMedium(
            modifier = Modifier.padding(top = SpaceXXLarge),
            text = R.string.player_now_playing,
        )
        Spacer(modifier = Modifier.height(TOP_SPACER.dp))
        GlowCard(
            glowColor = glow,
            glowRadius = GLOW_RADIUS,
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(size = imageSize)
                    .clip(shape = CircleShape)
                    .border(
                        width = BORDER_WIDTH.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                            ),
                        ),
                        shape = CircleShape
                    ),
                model = buildImageRequest(state.imageUrl),
                contentScale = Fit,
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.weight(1.5f))
        AnimatedContent(
            targetState = state,
            label = "animatedMediaDetails",
            contentKey = { Pair(state.artistName, state.songName) },
            transitionSpec = {
                scaleIn(animationSpec = tween(400)) togetherWith scaleOut(animationSpec = tween(400))
            },
        ) { state ->
            MediaDetails(
                artistName = state.artistName,
                songName = state.songName
            )
        }
        Spacer(modifier = Modifier.height(SpaceXXLarge))
        MediaControls(
            onTogglePlayState = togglePlayingState,
            isPlaying = state.isPlaying,
        )
        Spacer(modifier = Modifier.weight(1f))
    }

}

@Composable
private fun MediaDetails(
    artistName: String,
    songName: String,
) {
    Column(
        modifier = Modifier.padding(horizontal = Space),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally,
    ) {
        HeadlineSmall(
            text = artistName,
            textAlign = TextAlign.Center,
        )
        BodyMedium(
            text = songName,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MediaControls(
    onTogglePlayState: () -> Unit,
    isPlaying: Boolean,
) {
    Button(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape),
        onClick = onTogglePlayState,
    ) {
        Icon(
            modifier = Modifier.size(SpaceXXLarge),
            painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
            contentDescription = null,
        )
    }
}

private const val IMAGE_SIZE_FRACTION = 1.5
private const val GLOW_RADIUS = 120f
private const val GLOW_ALPHA = 0.6f
private const val TOP_SPACER = 100
private const val BORDER_WIDTH = 6
