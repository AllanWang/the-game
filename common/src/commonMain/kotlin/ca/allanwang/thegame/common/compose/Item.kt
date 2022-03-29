package ca.allanwang.thegame.common.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.HorizontalRule
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
    progressable: Progressable,
    numberFormat: String = "%.2f",
) {
    val storageProgress by animateProgressableState(progressable)

    val progressText by throttle(
        storageProgress * progressable.max,
        1_000 / 5,
        alwaysShow = setOf(progressable.value, progressable.max)
    )

    val fullProgressText: String = remember(storageProgress, progressText) {
        "$numberFormat / $numberFormat".format(
            progressText,
            progressable.max
        )
    }

    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconIndicator(if (storageProgress == 1f) 0f else progressable.speed)
            Text(modifier = Modifier.padding(horizontal = 4.dp), text = name)
            StorageProgressBar(
                modifier = Modifier.padding(horizontal = 4.dp).weight(1f),
                progress = storageProgress,
                text = fullProgressText,
            )
        }
    }
}

@Composable
private fun IconIndicator(speed: Float) {
    val icon = when {
        speed > 0 -> Icons.Rounded.ArrowDropUp
        speed < 0 -> Icons.Rounded.ArrowDropDown
        else -> Icons.Rounded.HorizontalRule
    }
    val desc = when {
        speed > 0 -> "Increasing"
        speed < 0 -> "Decreasing"
        else -> "Unchanging"
    }
    Icon(icon, contentDescription = desc)
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