package ca.allanwang.thegame.common.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ca.allanwang.thegame.common.data.Progressable
import ca.allanwang.thegame.common.data.throttle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun throttle(
    value: Float,
    timeoutMillis: Long,
    alwaysShow: Set<Float>
): State<Float> {
    val state = remember { mutableStateOf(value) }
    val flow = remember { MutableStateFlow(value) }
    SideEffect {
        flow.value = value
    }

    LaunchedEffect(flow) {
        flow.throttle(timeoutMillis, alwaysShow = alwaysShow).collect {
            state.value = it
        }
    }

    return state
}

/**
 * Animate value starting at [value], and ending at [max] with speed [speed] (delta / second).
 *
 * Inspired by animateValueAsState, though note that we aren't trying to animate to a new value.
 * Rather, we animate from the new value to another new max.
 *
 * If the supplied [value] does not match the current value and we are animating to a static value (speed 0),
 * we will add our own animation. Otherwise, we will immediately jump and start our intended animation.on.
 *
 * Resulting progress is between 0f and 1f
 */
@Composable
fun animateProgressableState(
    progressable: Progressable,
): State<Float> {
    val _start: Float = progressable.value / progressable.max

    val animatable = remember { Animatable(_start) }
    val flow = remember { MutableStateFlow(progressable) }

    SideEffect {
        flow.value = progressable
    }

    LaunchedEffect(flow) {
        flow.collect { progressable ->
            launch {
                val start: Float = progressable.value / progressable.max
                val speedMillis = progressable.speed / progressable.max / 1_000
                Icons.Filled.ArrowDropDown
                // Go to expected start value and stay there
                if (speedMillis == 0f) {
                    // Already at start
                    if (animatable.value == start) return@launch

                    val durationToStart =
                        (abs(animatable.value - start) * 200).toInt()

                    animatable.animateTo(
                        start, animationSpec = tween(
                            durationMillis = durationToStart,
                            easing = LinearEasing
                        )
                    )
                    return@launch
                }

                animatable.animateTo(
                    start, animationSpec = tween(
                        durationMillis = 0,
                        easing = LinearEasing
                    )
                )

                val durationToEnd = ((1f - start) / speedMillis).toInt()

                animatable.animateTo(
                    1f, animationSpec = tween(
                        durationMillis = durationToEnd,
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    return animatable.asState()
}