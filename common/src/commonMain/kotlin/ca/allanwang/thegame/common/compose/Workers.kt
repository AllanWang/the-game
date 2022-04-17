package ca.allanwang.thegame.common.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.allanwang.thegame.common.compose.components.BoundedIconButton
import ca.allanwang.thegame.common.data.Item

@Composable
fun Workers(
    count: Item.Workers,
    workersAvailable: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.Person,
            modifier = Modifier.size(18.dp),
            contentDescription = "Workers",
        )
        Text(
            modifier = Modifier.width(60.dp).padding(horizontal = 4.dp),
            text = "${count.count} / ${count.maxCount ?: "∞"}"
        )
        BoundedIconButton(
            modifier = Modifier.padding(horizontal = 4.dp).size(30.dp),
            onClick = onDecrease,
            enabled = count.count > 0
        ) {
            Icon(
                Icons.Default.Remove,
                modifier = Modifier.size(18.dp),
                contentDescription = "Decrease Worker Count"
            )
        }

        BoundedIconButton(
            modifier = Modifier.padding(horizontal = 4.dp).size(30.dp),
            onClick = onIncrease, enabled = workersAvailable
        ) {
            Icon(
                Icons.Default.Add,
                modifier = Modifier.size(18.dp),
                contentDescription = "Increase Worker Count"
            )
        }
    }
}