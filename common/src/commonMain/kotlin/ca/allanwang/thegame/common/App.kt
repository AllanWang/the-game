package ca.allanwang.thegame.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.allanwang.thegame.common.compose.Item
import ca.allanwang.thegame.common.data.Action
import ca.allanwang.thegame.common.data.Progressable
import ca.allanwang.thegame.common.data.State
import ca.allanwang.thegame.common.flow.GameFlow
import ca.allanwang.thegame.common.flow.GameView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
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

@Composable
fun App(state: State = StateHolder.state) {

    Column(Modifier.padding(4.dp)) {
        Text(text = state.tick.toString())
        Button(onClick = { StateHolder.gameFlow.addAction(Action.Reset) }) {
            Text("Reset")
        }

        Column(modifier = Modifier.fillMaxWidth(0.5f)) {
            Item(item = state.wood)
            Item(item = state.boards)
        }
    }
}
