package ca.allanwang.thegame.common.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import kotlin.math.min


/**
 * Progress bar indicator.
 *
 * Progress will start at [value] and go to [max] with speed [speed].
 * Recomposition is only needed when value or max changes.
 */
@Composable
fun StorageProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
    text: String?,
) {

    Box(modifier.height(IntrinsicSize.Min)) {
        ProgressBar(progress = progress)

        Text(
            modifier = Modifier.padding(4.dp).align(Alignment.CenterEnd),
            text = text ?: ""
        )
    }
}

@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val cornerRadius = CornerRadius(min(canvasWidth, canvasHeight) / 4)

        val clipPath = Path().apply {
            addRoundRect(
                RoundRect(
                    0f,
                    0f,
                    canvasWidth,
                    canvasHeight,
                    cornerRadius,
                )
            )
        }

        clipPath(clipPath) {
            drawRect(
                size = Size(width = canvasWidth, height = canvasHeight),
                color = Color(0xFFDDDDDD)
            )

            drawRect(
                size = Size(
                    width = canvasWidth * progress,
                    height = canvasHeight
                ),
                color = Color(0xFFBBBBBB)
            )
        }
    }
}