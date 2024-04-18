package com.example.radiotest.core.common.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.example.radiotest.R

@Composable
fun buildImageRequest(url: String?, @DrawableRes fallback: Int = R.drawable.generic_song) =
    ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(CROSSFADE_DURATION)
        .fallback(fallback)
        .placeholder(fallback)
        .error(fallback)
        .build()

private const val CROSSFADE_DURATION = 1000