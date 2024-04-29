package com.radio.broadcast.feature.schedule

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.radio.broadcast.R
import com.radio.broadcast.core.common.ui.ImmutableList
import com.radio.broadcast.core.common.ui.RText
import com.radio.broadcast.core.common.ui.RText.BodyMedium
import com.radio.broadcast.core.common.ui.theme.Dimens
import com.radio.broadcast.core.common.ui.theme.Dimens.Space
import com.radio.broadcast.core.common.ui.theme.Dimens.SpaceXSmall

@Composable
fun ScheduleScreen(schedule: ImmutableList<String>) {
    val browserIntent = Intent(Intent.ACTION_VIEW).apply {
        setData(Uri.parse(WEB_URL))
    }
    val context = LocalContext.current
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
                .padding(Space)
                .weight(1f),
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
                    BodyMedium(text = it)
                    Spacer(modifier = Modifier.height(Space))
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Space),
            onClick = { context.startActivity(browserIntent) }) {
            BodyMedium(text = R.string.schedule_visit)
        }
        Spacer(modifier = Modifier.height(Space))
    }
}

private const val WEB_URL = "https://hristijanskoradio.mk/"