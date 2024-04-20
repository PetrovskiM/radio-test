package com.radio.broadcast.feature.player.presentation.contract

sealed interface PlayerViewAction {
    data object TogglePlayState : PlayerViewAction
}