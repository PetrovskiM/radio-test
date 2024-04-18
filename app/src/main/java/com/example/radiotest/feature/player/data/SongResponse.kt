package com.example.radiotest.feature.player.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioResponse(
    val pub: DataResponse?,
)

@Serializable
data class DataResponse(
    val data: NpResponse?,
)

@Serializable
data class NpResponse(
    val np: NowPlayingResponse?,
)

@Serializable
data class NowPlayingResponse(
    @SerialName("now_playing") val nowPlaying: SongResponse?,
)

@Serializable
data class SongResponse(
    val song: SongDetailsResponse?,
)

@Serializable
data class SongDetailsResponse(
    val artist: String?,
    val title: String?,
    val art: String?,
)