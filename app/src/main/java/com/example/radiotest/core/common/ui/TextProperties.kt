package com.example.radiotest.core.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class TextProperties(
    val fontSize: TextUnit,
    val fontWeight: FontWeight,
    val lineHeight: TextUnit,
)

@Composable
fun getTextProperties(rText: RText): TextProperties {
    return when (rText) {
        RText.HeadlineMedium -> TextProperties(
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 36.sp,
        )
        RText.HeadlineSmall -> TextProperties(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 32.sp,
        )

        RText.BodyMedium -> TextProperties(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
        )
    }
}