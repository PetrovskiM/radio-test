package com.radio.broadcast.core.common.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.radio.broadcast.R

sealed class TopLevelDestination(
    val route: String,
    @DrawableRes val selectedRes: Int,
    @DrawableRes val unselectedRes: Int,
    @StringRes val labelRes: Int,
) {
    data object Radio : TopLevelDestination(
        route = "profile",
        labelRes = R.string.radio,
        selectedRes = R.drawable.ic_radio_selected,
        unselectedRes = R.drawable.ic_radio_unselected,
    )

    data object Schedule : TopLevelDestination(
        route = "schedule",
        labelRes = R.string.schedule,
        selectedRes = R.drawable.ic_schedule,
        unselectedRes = R.drawable.ic_schedule,
    )
}

val topLevelDestinations = immutableListOf(
    TopLevelDestination.Radio,
    TopLevelDestination.Schedule,
)