package com.radio.broadcast.core.common.ui

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

@Composable
fun GlowCard(
    glowColor: Color,
    containerColor: Color = Color.White,
    glowRadius: Float,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.drawBehind {
        val size = this.size
        drawContext.canvas.nativeCanvas.apply {
            drawRoundRect(
                0f,
                0f,
                size.width,
                size.height,
                Int.MAX_VALUE.toFloat(),
                Int.MAX_VALUE.toFloat(),
                Paint().apply {
                    color = containerColor.toArgb()
                    setShadowLayer(
                        glowRadius,
                        0f,
                        0f,
                        glowColor.toArgb(),
                    )
                }
            )
        }
    }
    ) {
        content()
    }
}
