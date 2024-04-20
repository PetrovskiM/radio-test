package com.radio.broadcast.di

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.radio.broadcast.BuildConfig
import com.radio.broadcast.core.common.di.Dispatcher
import com.radio.broadcast.core.common.di.RadioDispatcher
import com.radio.broadcast.core.common.di.RadioDispatcher.IO
import com.radio.broadcast.feature.player.data.AzuraApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    fun provideOkHttp() = OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addNetworkInterceptor(loggingInterceptor)
            }
        }
        .build()

    @Provides
    @Singleton
    @MediaMetadataObserver
    fun provideMutableMediaMetadataObserver(): MutableStateFlow<String> = MutableStateFlow("")

    @Provides
    @Singleton
    @MediaMetadataObserver
    fun provideMediaMetadataObserver(@MediaMetadataObserver mediaMetadataMutableStateFlow: MutableStateFlow<String>): StateFlow<String> =
        mediaMetadataMutableStateFlow.asStateFlow()

    @Provides
    @Singleton
    @MediaPlaybackObserver
    fun provideMutableMediaPlaybackObserver(): MutableStateFlow<Boolean> = MutableStateFlow(true)

    @Provides
    @Singleton
    @MediaPlaybackObserver
    fun provideMediaPlaybackObserver(@MediaPlaybackObserver mediaPlaybackMutableStateFlow: MutableStateFlow<Boolean>): StateFlow<Boolean> =
        mediaPlaybackMutableStateFlow.asStateFlow()

    @Provides
    fun providesRetrofit(
        httpClient: OkHttpClient,
        json: Json,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(getBaseUrl())
        .client(httpClient)
        .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
        .build()

    @Provides
    fun provideAzuraApi(retrofit: Retrofit): AzuraApi = retrofit.create()

    @Provides
    @Dispatcher(IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private fun getBaseUrl(): String = FirebaseUrls.nowPlayingUrl
}