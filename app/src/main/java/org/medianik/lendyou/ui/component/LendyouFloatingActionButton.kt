package org.medianik.lendyou.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.ui.theme.LendyouTheme

@Composable
fun BoxScope.LendyouFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(horizontal = 10.dp, vertical = 40.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    backgroundColor: Color = LendyouTheme.colors.brandSecondary,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = ""
        )
    }
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