package com.radio.broadcast.feature.player.domain

import com.radio.broadcast.core.common.di.Dispatcher
import com.radio.broadcast.core.common.di.RadioDispatcher
import com.radio.broadcast.core.common.di.RadioDispatcher.IO
import com.radio.broadcast.feature.player.domain.model.Song
import com.radio.broadcast.feature.player.data.AzuraApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayerRepository @Inject constructor(
    private val azuraApi: AzuraApi,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun getNowPlayingData(): Song? = withContext(dispatcher) {
        val response = azuraApi.getNowPlayingData()
        return@withContext if (response.isSuccessful && response.body() != null) {
            response.body()?.toDomainModel()
        } else {
            null
        }
    }
}