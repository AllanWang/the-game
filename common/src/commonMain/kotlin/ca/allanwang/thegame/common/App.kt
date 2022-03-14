package ca.allanwang.thegame.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    var p by remember { mutableStateOf(0) }

    Column {
        Button(onClick = {
            text = "Hello, ${getPlatformName()}"
            p = 1 - p
        }) {
            Text(text)
        }

        Spacer(Modifier.height(50.dp))

        ProgressBar(
            modifier = Modifier.fillMaxWidth(0.5f).height(10.dp),
            value = p,
            max = 5,
            speed = 0.1f
        )
    }
}

/**
 * Animate value starting at [value], and ending at [max] with speed [speed] (delta / second).
 *
 * Inspired by animateValueAsState, though note that we aren't trying to animate to a new value.
 * Rather, we animate from the new value to another new max.
 *
 * Resulting progress is between 0f and 1f
 */
@Composable
fun animateIntProgressState(
    value: Int,
    max: Int,
    speed: Float
): State<Float> {
    val start: Float = value.toFloat() / max

    val animatable = remember { Animatable(start) }
    val flow = remember { MutableStateFlow(start) }

    SideEffect {
        flow.value = start
    }

    LaunchedEffect(flow) {
        flow.collect { start ->
            launch {

                val target: Float = if (speed == 0f) start else 1f

                // Already at target
                if (animatable.value == target && animatable.targetValue == target) return@launch

                // Direct set
                animatable.animateTo(
                    start, animationSpec = tween(
                        durationMillis = 0, easing = LinearEasing
                    )
                )

                // No speed and therefore no animation
                if (start == target) return@launch

                val duration: Float = (target - start) * max / speed * 1000
                animatable.animateTo(
                    target,
                    animationSpec = tween(
                        durationMillis = duration.toInt(),
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    return animatable.asState()
}

/**
 * Progress bar indicator.
 *
 * Progress will start at [value] and go to [max] with speed [speed].
 * Recomposition is only needed when value or max changes.
 */
@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    value: Int,
    max: Int,
    speed: Float
) {
    val progress by animateIntProgressState(value = value, max = max, speed = speed)

    println(progress)

    Column {
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawRect(
                size = Size(width = canvasWidth, height = canvasHeight),
                color = Color.Red
            )

            drawRect(
                size = Size(
                    width = canvasWidth * progress,
                    height = canvasHeight
                ),
                color = Color.Blue
            )
        }
        Text("%.2f".format(progress))
    }
}