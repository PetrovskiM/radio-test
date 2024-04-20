package com.radio.broadcast.feature.player.domain

import com.radio.broadcast.feature.player.domain.model.Song
import com.radio.broadcast.feature.player.data.model.NowPlayingWrapperResponse

fun NowPlayingWrapperResponse.toDomainModel() = with(nowPlayingResponse.song) {
    Song(
        artist = artist,
        title = title,
        art = art,
    )
}