package ca.allanwang.thegame.common.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import ca.allanwang.thegame.common.data.Item
import ca.allanwang.thegame.common.data.Progressable
import kotlin.math.min

@Composable
fun Item(
    modifier: Modifier = Modifier,
    item: Item
) {
    val progress by animateFloatAsState(
        item.storage.value / item.storage.max,
        animationSpec = tween(durationMillis = 300)
    )
    val progressText =
        "%.2f / %.2f".format(item.storage.value, item.storage.max)
    Column(modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconIndicator(1f) // TODO
            Text(
                modifier = Modifier.padding(horizontal = 4.dp).width(60.dp),
                text = item.constants.name
            )
            StorageProgressBar(
                modifier = Modifier.padding(horizontal = 4.dp).weight(1f),
                progress = progress,
                text = progressText,
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