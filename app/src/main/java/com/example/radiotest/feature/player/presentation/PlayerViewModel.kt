package com.example.radiotest.feature.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotest.core.common.data.WebSocketObserver
import com.example.radiotest.di.MediaMetadataObserver
import com.example.radiotest.di.MediaPlaybackObserver
import com.example.radiotest.feature.player.presentation.contract.PlayerViewAction
import com.example.radiotest.feature.player.presentation.contract.PlayerViewAction.TogglePlayState
import com.example.radiotest.feature.player.presentation.contract.RadioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val webSocketObserver: WebSocketObserver,
    @MediaMetadataObserver private val mediaMetadataObserver: StateFlow<String>,
    @MediaPlaybackObserver private val mediaPlaybackObserver: StateFlow<Boolean>,
) : ViewModel() {

    private val _state = MutableStateFlow(RadioState())
    val state = _state.asStateFlow()

    init {
        observeMediaMetadataChanges()
        observeMediaPlaybackChanges()
        observeSocket()
    }

    fun handleViewAction(viewAction: PlayerViewAction) {
        when (viewAction) {
            TogglePlayState -> _state.update { state -> state.copy(isPlaying = !state.isPlaying) }
        }
    }

    private fun observeSocket() {
        viewModelScope.launch {
            webSocketObserver.observeSongs().collect { currentSong ->
                _state.update { state ->
                    state.copy(
                        imageUrl = currentSong.artUrl.orEmpty(),
                        artistName = currentSong.artist.orEmpty(),
                        songName = currentSong.title.orEmpty(),
                    )
                }
            }
        }
    }

    private fun observeMediaMetadataChanges() {
        viewModelScope.launch {
            mediaMetadataObserver.collect { songData ->
                _state.update { state ->
                    state.copy(
                        artistName = songData,
                    )
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

    override fun onCleared() {
        super.onCleared()
        webSocketObserver.close()
    }
}

