package com.radio.broadcast.feature.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radio.broadcast.di.MediaMetadataObserver
import com.radio.broadcast.di.MediaPlaybackObserver
import com.radio.broadcast.feature.player.domain.PlayerRepository
import com.radio.broadcast.feature.player.presentation.contract.PlayerViewAction
import com.radio.broadcast.feature.player.presentation.contract.PlayerViewAction.TogglePlayState
import com.radio.broadcast.feature.player.presentation.contract.RadioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    @MediaMetadataObserver private val mediaMetadataObserver: StateFlow<String>,
    @MediaPlaybackObserver private val mediaPlaybackObserver: StateFlow<Boolean>,
) : ViewModel() {

    private val _state = MutableStateFlow(RadioState())
    val state = _state.asStateFlow()

    init {
        observeMediaMetadataChanges()
        observeMediaPlaybackChanges()
    }

    fun handleViewAction(viewAction: PlayerViewAction) {
        when (viewAction) {
            TogglePlayState -> _state.update { state -> state.copy(isPlaying = !state.isPlaying) }
        }
    }

    private fun observeMediaMetadataChanges() {
        viewModelScope.launch {
            mediaMetadataObserver.collect { artistSongMeta ->
                val songData = playerRepository.getNowPlayingData()

                _state.update { state ->
                    if (songData != null) {
                        state.copy(
                            artistName = artistSongMeta,
                            imageUrl = songData.art,
                        )
                    } else {
                        state.copy(
                            artistName = artistSongMeta,
                            imageUrl = null,
                        )
                    }
                }
            }
        }
    }

    private fun observeMediaPlaybackChanges() {
        viewModelScope.launch {
            mediaPlaybackObserver.collect { isPlaying ->
                _state.update { state ->
                    state.copy(
                        isPlaying = isPlaying,
                    )
                }
            }
        }
    }
}

