import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ca.allanwang.thegame.common.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "The Game",
        state = rememberWindowState(width = 800.dp, height = 600.dp)
    ) {
        MaterialTheme {
            App()
        }
    }
}