package ca.allanwang.thegame.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ca.allanwang.thegame.common.compose.Item
import ca.allanwang.thegame.common.data.Progressable

@Composable
fun App() {
    val testIndex = remember { mutableStateOf(0) }

    Column {

        Button(onClick = {
            testIndex.value = (testIndex.value + 1).rem(2)
        }) {
            Text("Test ${testIndex.value}")
        }

        val progressable = when (testIndex.value) {
            0 -> Progressable(
                value = 2f,
                max = 10f,
                speed = 0f
            )
            1 -> Progressable(
                value = 1f,
                max = 10f,
                speed = 1f
            )
            else -> throw IllegalArgumentException("Bad index ${testIndex.value}")
        }

        Item(
            modifier = Modifier.fillMaxWidth(0.5f),
            name = "Wood",
            progressable = progressable
        )
    }
}
