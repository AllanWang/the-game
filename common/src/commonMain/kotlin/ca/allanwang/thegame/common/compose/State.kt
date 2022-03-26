package ca.allanwang.thegame.common.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
 * If the supplied [value] does not match the current value,
 * we will quickly animate to that value or an intermediate before continuing to the expected destination.
 *
 * Resulting progress is between 0f and 1f
 */
@Composable
fun animateProgressableState(
    progressable: Progressable
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

                val durationToStart =
                    (abs(animatable.value - start) * 500).toInt()

                val now = System.currentTimeMillis()

                // Go to expected start value and stay there
                if (speedMillis == 0f) {
                    // Already at start
                    if (animatable.value == start) return@launch
                    animatable.animateTo(
                        start, animationSpec = tween(
                            durationMillis = durationToStart,
                            easing = LinearEasing
                        )
                    )
                    return@launch
                }

                val expectedDuration = ((1f - start) / speedMillis).toLong()

                if (expectedDuration < durationToStart) {
                    // too slow, animate directly
                    animatable.animateTo(
                        start, animationSpec = tween(
                            durationMillis = 0,
                            easing = LinearEasing
                        )
                    )
                    animatable.animateTo(
                        1f, animationSpec = tween(
                            durationMillis = expectedDuration.toInt(),
                            easing = LinearEasing
                        )
                    )
                    return@launch
                }

                // Compute intermediate and then resume expected speed once there
                val intermediate = start + speedMillis * durationToStart

                if (intermediate >= 1f) {
                    // Will get to max directly, animate with our own duration and finish
                    animatable.animateTo(
                        1f, animationSpec = tween(
                            durationMillis = durationToStart,
                            easing = LinearEasing
                        )
                    )
                    return@launch
                }

                // Animate to intermediate, then animate to max using supplied speed
                animatable.animateTo(
                    intermediate, animationSpec = tween(
                        durationMillis = durationToStart,
                        easing = LinearEasing
                    )
                )

                // Animate to max with remaining time
                animatable.animateTo(
                    1f, animationSpec = tween(
                        durationMillis = (expectedDuration + now - System.currentTimeMillis()).toInt(),
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    return animatable.asState()
}