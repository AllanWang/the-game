package ca.allanwang.thegame.common.data

import ca.allanwang.thegame.common.data.actions.tick
import ca.allanwang.thegame.common.data.actions.workerUpdate
import com.google.common.flogger.FluentLogger

private val logger = FluentLogger.forEnclosingClass()

sealed interface Action {
    object Reset : Action
    data class Tick(val tick: Long) : Action
    data class WorkerUpdate(val key: Key, val delta: Int) : Action
}

fun State.reduce(action: Action): State = when (action) {
    is Action.Tick -> tick(action)
    is Action.Reset -> State()
    is Action.WorkerUpdate -> workerUpdate(action)
    else -> {
        logger.atWarning().log("Unhandled mutation %s", action)
        this
    }
}
