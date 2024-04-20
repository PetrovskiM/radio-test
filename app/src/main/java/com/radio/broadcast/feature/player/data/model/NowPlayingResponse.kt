package com.radio.broadcast.feature.player.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NowPlayingWrapperResponse(@SerialName("now_playing") val nowPlayingResponse: NowPlayingResponse)

@Serializable
data class NowPlayingResponse(@SerialName("song") val song: SongResponse)
