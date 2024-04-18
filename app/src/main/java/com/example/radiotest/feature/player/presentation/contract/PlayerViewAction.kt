package com.example.radiotest.feature.player.presentation.contract

sealed interface PlayerViewAction {
    data object TogglePlayState : PlayerViewAction
}