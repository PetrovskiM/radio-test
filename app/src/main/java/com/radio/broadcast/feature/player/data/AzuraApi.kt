package com.radio.broadcast.feature.player.data

import com.radio.broadcast.feature.player.data.model.NowPlayingWrapperResponse
import retrofit2.Response
import retrofit2.http.GET

interface AzuraApi {

    @GET("nowplaying/hristijansko_radio")
    suspend fun getNowPlayingData(): Response<NowPlayingWrapperResponse>
}