package com.example.radiotest.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    fun provideOkHttp() = OkHttpClient.Builder().build()

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
}