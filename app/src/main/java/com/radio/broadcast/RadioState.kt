package com.radio.broadcast

import com.radio.broadcast.core.common.ui.ImmutableList
import com.radio.broadcast.core.common.ui.emptyImmutableList

data class MainState(
    val isLoading: Boolean = true,
    val schedule: ImmutableList<String> = emptyImmutableList(),
)