package ca.allanwang.thegame.common.compose

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.HorizontalRule
import androidx.compose.runtime.Composable

@Composable
fun RateIndicator(speed: Float) {
    val icon = when {
        speed > 0 -> Icons.Rounded.ArrowDropUp
        speed < 0 -> Icons.Rounded.ArrowDropDown
        else -> Icons.Rounded.HorizontalRule
    }
    val desc = when {
        speed > 0 -> "Increasing"
        speed < 0 -> "Decreasing"
        else -> "Unchanging"
    }
    Icon(icon, contentDescription = desc)
}
