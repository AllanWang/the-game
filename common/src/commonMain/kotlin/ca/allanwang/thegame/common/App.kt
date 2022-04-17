package ca.allanwang.thegame.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.allanwang.thegame.common.compose.Item
import ca.allanwang.thegame.common.data.Action
import ca.allanwang.thegame.common.data.State
import ca.allanwang.thegame.common.flow.GameFlow
import ca.allanwang.thegame.common.flow.GameView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive

// TODO move to proper state holder
object StateHolder {
    private val initialState = State()
    var state by mutableStateOf(initialState)
    private val actionFlow = MutableSharedFlow<Action>()
    private val tickFlow = callbackFlow {
        var tick = initialState.tick
        while (isActive) {
            trySend(Action.Tick(tick++))
            delay(1_000)
        }
    }
    val gameFlow = GameFlow(initialState = initialState)

    init {
        gameFlow.takeView(
            viewCoroutineScope = CoroutineScope(Dispatchers.Main),
            view = GameView(
                stateConsumer = { state = it },
                emitter = actionFlow.asSharedFlow()
            )
        )
        gameFlow.addActions(tickFlow)
    }
}

@UseExperimental(ExperimentalMaterialApi::class)
@Composable
fun App() {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        Main(
            state = StateHolder.state,
            action = { StateHolder.gameFlow.addAction(it) })
    }
}

@Composable
fun Main(
    state: State,
    action: (Action) -> Unit
) {
    Column(Modifier.padding(4.dp)) {
        Text(text = state.tick.toString())
        Button(onClick = { StateHolder.gameFlow.addAction(Action.Reset) }) {
            Text("Reset")
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Item(
                item = state.wood,
                workersAvailable = state.workers.count > 0,
                action = action
            )
            Item(
                item = state.boards,
                workersAvailable = state.workers.count > 0,
                action = action
            )
        }
    }
}