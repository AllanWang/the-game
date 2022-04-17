package ca.allanwang.thegame.common.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.allanwang.thegame.common.data.Item

@Composable
fun Item(
    modifier: Modifier = Modifier, item: Item
) {
    val progress by animateFloatAsState(
        item.storage.value / item.storage.max,
        animationSpec = tween(durationMillis = 300)
    )
    val progressText =
        "%.2f / %.2f".format(item.storage.value, item.storage.max)
    Column(modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RateIndicator(1f) // TODO
            Text(
                modifier = Modifier.padding(horizontal = 4.dp).width(60.dp),
                text = item.constants.name
            )
            StorageProgressBar(
                modifier = Modifier.padding(horizontal = 4.dp).weight(1f),
                progress = progress,
                text = progressText,
            )
            Workers(count = item.workers)
        }
    }
}
