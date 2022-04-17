package ca.allanwang.thegame.common.data

import com.google.common.flogger.FluentLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

sealed interface Mutation {
    object Reset : Mutation
    data class Tick(val tick: Long) : Mutation
}

private val logger = FluentLogger.forEnclosingClass()

fun State.mutations(action: Action): Flow<Mutation> = when (action) {
    is Action.Tick -> {
        flowOf(
            Mutation.Tick(action.seconds),
        )
    }
    is Action.Reset -> {
        flowOf(
            Mutation.Reset
        )
    }
    else -> {
        logger.atWarning().log("Unhandled action %s", action)
        emptyFlow()
    }
}

fun State.reduce(mutation: Mutation): State = when (mutation) {
    is Mutation.Tick -> tick(mutation.tick)
    is Mutation.Reset -> State()
    else -> {
        logger.atWarning().log("Unhandled mutation %s", mutation)
        this
    }
}
