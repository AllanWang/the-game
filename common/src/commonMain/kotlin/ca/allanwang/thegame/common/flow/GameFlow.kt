@file:Suppress("FunctionName")

package ca.allanwang.thegame.common.flow

import ca.allanwang.thegame.common.data.Action
import ca.allanwang.thegame.common.data.State
import ca.allanwang.thegame.common.data.reduce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

typealias GameFlow = MVFlow<State, Action>

fun GameFlow(
    initialState: State = State(),
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
): GameFlow = MVFlow(
    initialState = initialState,
    reducer = { state, action -> state.reduce(action) },
    mvflowCoroutineScope = scope,
)

typealias GameView = MVFlow.View<State, Action>

fun GameView(
    stateConsumer: (State) -> Unit,
    emitter: Flow<Action>
): GameView = object : MVFlow.View<State, Action> {
    override fun render(state: State) {
        stateConsumer(state)
    }

    override fun actions(): Flow<Action> = emitter

}