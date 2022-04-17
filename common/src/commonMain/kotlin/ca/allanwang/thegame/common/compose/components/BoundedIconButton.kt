package ca.allanwang.thegame.common.compose.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * Replica of IconButton but with rounded bounded ripples.
 */
@Composable
fun BoundedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val contentAlpha =
        if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
    CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
        val shape = RoundedCornerShape(8.dp)
        Box(
            modifier = modifier
                .border(
                    1.dp,
                    color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current * 0.5f),
                    shape = shape
                )
                .clip(shape)
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = rememberRipple()
                ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}