package ca.allanwang.thegame.common.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.allanwang.thegame.common.data.Item

@Composable
fun Workers(
    modifier: Modifier = Modifier,
    count: Item.Workers,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row {
        Text(text = "${count.count} / ${count.maxCount ?: "∞"}")
        IconButton(onClick = onDecrease) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Decrease Worker Count"
            )
        }

        IconButton(onClick = onIncrease) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Increase Worker Count"
            )
        }
    }
}