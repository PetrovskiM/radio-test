package com.example.radiotest.core.common.ui

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

sealed class RText {
    data object HeadlineMedium : RText()
    data object HeadlineSmall : RText()
    data object BodyMedium : RText()

    @Composable
    operator fun invoke(
        modifier: Modifier = Modifier,
        text: String,
        textAlign: TextAlign? = null,
    ) {
        val properties = getTextProperties(rText = this)
        Text(
            modifier = modifier,
            text = text,
            fontSize = properties.fontSize,
            fontWeight = properties.fontWeight,
            lineHeight = properties.lineHeight,
            textAlign = textAlign
        )
    }

    @Composable
    operator fun invoke(
        modifier: Modifier = Modifier,
        @StringRes text: Int,
        textAlign: TextAlign? = null,
    ) {
        val properties = getTextProperties(rText = this)
        Text(
            modifier = modifier,
            text = stringResource(id = text),
            fontSize = properties.fontSize,
            fontWeight = properties.fontWeight,
            lineHeight = properties.lineHeight,
            textAlign = textAlign
        )
    }
}