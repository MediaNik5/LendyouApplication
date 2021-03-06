package org.medianik.lendyou.ui.component

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.ui.theme.LendyouTheme

private const val DividerAlpha = 0.12f

@Composable
fun LendyouDivider(
    modifier: Modifier = Modifier,
    color: Color = LendyouTheme.colors.uiBorder.copy(alpha = DividerAlpha),
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp
) {
    Divider(
        modifier = modifier,
        color = color,
        thickness = thickness,
        startIndent = startIndent
    )
}