package org.medianik.lendyou.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import org.medianik.lendyou.ui.theme.LendyouTheme

@Composable
fun LendyouFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    backgroundColor: Color = LendyouTheme.colors.brandSecondary,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick,
        modifier,
        interactionSource,
        shape,
        backgroundColor,
        contentColor,
        elevation,
        content
    )
}