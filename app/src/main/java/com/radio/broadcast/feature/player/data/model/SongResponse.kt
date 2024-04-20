package com.radio.broadcast.feature.player.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SongResponse(
    val artist: String,
    val title: String,
    val art: String,
)
