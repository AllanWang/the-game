package ca.allanwang.thegame.common.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ca.allanwang.thegame.common.data.Progressable
import kotlin.math.min

@Composable
fun Item(
    modifier: Modifier = Modifier,
    name: String,
    progressable: Progressable
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = Modifier.padding(horizontal = 4.dp), text = name)
            StorageProgressBar(
                modifier = Modifier.padding(horizontal = 4.dp).weight(1f),
                progressable = progressable
            )
        }
    }
}


/**
 * Progress bar indicator.
 *
 * Progress will start at [value] and go to [max] with speed [speed].
 * Recomposition is only needed when value or max changes.
 */
@Composable
fun StorageProgressBar(
    modifier: Modifier = Modifier,
    progressable: Progressable,
    numberFormat: String = "%.2f"
) {

    val storageProgress by animateProgressableState(progressable)

    Box(modifier.height(IntrinsicSize.Min)) {
        ProgressBar(progress = storageProgress)

        val progressText by throttle(
                storageProgress * progressable.max,
                1_000 / 5,
                alwaysShow = setOf(progressable.value, progressable.max)
            )

        Text(
            modifier = Modifier.padding(4.dp).align(Alignment.CenterEnd),
            text = "$numberFormat / $numberFormat".format(
                progressText,
                progressable.max
            )
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

        drawRoundRect(
            size = Size(width = canvasWidth, height = canvasHeight),
            cornerRadius = cornerRadius,
            color = Color(0xFFDDDDDD)
        )

        drawRoundRect(
            size = Size(
                width = canvasWidth * progress,
                height = canvasHeight
            ),
            cornerRadius = cornerRadius,
            color = Color(0xFFBBBBBB)
        )
    }
}