package com.radio.broadcast.feature.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.radio.broadcast.R
import com.radio.broadcast.core.common.ui.ImmutableList
import com.radio.broadcast.core.common.ui.RText
import com.radio.broadcast.core.common.ui.theme.Dimens
import com.radio.broadcast.core.common.ui.theme.Dimens.Space
import com.radio.broadcast.core.common.ui.theme.Dimens.SpaceXSmall

@Composable
fun ScheduleScreen(schedule: ImmutableList<String>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RText.HeadlineMedium(
            modifier = Modifier.padding(top = Dimens.SpaceXXLarge),
            text = R.string.schedule,
        )
        Spacer(modifier = Modifier.height(Space))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space),
            elevation = CardDefaults.cardElevation(
                defaultElevation = SpaceXSmall,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Space)
                    .verticalScroll(rememberScrollState())
            ) {
                schedule.forEach {
                    RText.BodyMedium(text = it)
                    Spacer(modifier = Modifier.height(Space))
                }
            }
        }
    }
}
