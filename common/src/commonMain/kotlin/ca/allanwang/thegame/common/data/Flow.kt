package ca.allanwang.thegame.common.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * Emit first value, then wait at least [timeoutMillis] before emitting subsequent values.
 *
 * 'Opposite' of debounce.
 */
fun <T> Flow<T>.throttle(timeoutMillis: Long, alwaysShow: Set<T>) = flow {
    var windowStartTime = System.currentTimeMillis()
    var emitted = false
    collect { value ->
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - windowStartTime
        if (value in alwaysShow || delta >= timeoutMillis) {
            windowStartTime = currentTime
            emitted = false
        }
        if (!emitted) {
            emit(value)
            emitted = true
        }
    }
}