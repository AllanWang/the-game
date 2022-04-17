/*
 * Copyright (c) 2020 Pedro Loureiro
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("FunctionName")

package ca.allanwang.thegame.common.flow

import ca.allanwang.thegame.common.data.Action.Tick
import com.google.common.flogger.FluentLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/*
 * MVI Implementation using coroutine flows.
 *
 * Adapted from https://github.com/pedroql/mvflow
 * https://pedroql.github.io/mvflow/
 */

/**
 * Takes in current state and action, and returns flow of mutations.
 */
typealias Handler<State, Action, Mutation> = (State, Action) -> Flow<Mutation>

/**
 * Takes in state and mutation, and returns new state after changes.
 */
typealias Reducer<State, Mutation> = (State, Mutation) -> State

interface MVFlow<State, Action> {
    fun takeView(
        viewCoroutineScope: CoroutineScope,
        view: View<State, Action>,
        initialActions: List<Action> = emptyList(),
    )

    fun addActions(actions: Flow<Action>)

    fun addAction(action: Action) = addActions(flowOf(action))

    interface View<State, Action> {
        fun render(state: State)

        fun actions(): Flow<Action>
    }
}

fun <State, Action, Mutation> MVFlow(
    initialState: State,
    handler: Handler<State, Action, Mutation>,
    reducer: Reducer<State, Mutation>,
    mvflowCoroutineScope: CoroutineScope,
): MVFlow<State, Action> =
    MVFlowImpl(
        initialState,
        handler,
        reducer,
        mvflowCoroutineScope,
    )

private class MVFlowImpl<State, Action, Mutation>(
    initialState: State,
    private val handler: Handler<State, Action, Mutation>,
    private val reducer: Reducer<State, Mutation>,
    private val mvflowCoroutineScope: CoroutineScope,
) : MVFlow<State, Action> {
    private val state = MutableStateFlow(initialState)
    private val mutex = Mutex()

    override fun takeView(
        viewCoroutineScope: CoroutineScope,
        view: MVFlow.View<State, Action>,
        initialActions: List<Action>,
    ) {
        viewCoroutineScope.launch {
            sendStatesToView(view)
            handleViewActions(view, initialActions)
        }
    }

    private fun CoroutineScope.sendStatesToView(
        view: MVFlow.View<State, Action>
    ) {
        state
            .onStart {
                logger.atFine().log("Start mvflow")
            }
            .onEach {
                logger.atFinest().log("New state: %s", it)
                view.render(it)
            }.launchIn(this)
    }

    private fun CoroutineScope.handleViewActions(
        view: MVFlow.View<State, Action>,
        initialActions: List<Action>,
    ) {
        launch {
            view.actions()
                .onStart {
                    emitAll(initialActions.asFlow())
                }.collectIntoHandler(this)
        }
    }

    private suspend fun Flow<Action>.collectIntoHandler(callerCoroutineScope: CoroutineScope) {
        onEach { action ->
            callerCoroutineScope.launch {
                if (action is Tick)
                    logger.atFiner().log("Action %s", action)
                else
                    logger.atInfo().log("Action %s", action)
                handler(state.value, action)
                    .onEach { mutation ->
                        mutex.withLock {
                            logger.atFine().log("- Mutation %s", mutation)
                            state.value = reducer.invoke(state.value, mutation)
                        }
                    }
                    .launchIn(mvflowCoroutineScope)
            }
        }
            .launchIn(callerCoroutineScope)
    }

    override fun addActions(actions: Flow<Action>) {
        mvflowCoroutineScope.launch {
            actions.collectIntoHandler(this)
        }
    }

    companion object {
        private val logger = FluentLogger.forEnclosingClass()
    }
}