package com.radio.broadcast.feature.player.presentation.contract

data class RadioState(
    val artistName: String = "",
    val songName: String = "",
    val imageUrl: String? = null,
    val isPlaying: Boolean = true,
)
