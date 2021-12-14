package org.medianik.lendyou.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints

@Composable
fun EndRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = Layout(
    modifier = modifier,
    content = content,
    measurePolicy = { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints)
        }
        val height = placeables.maxOfOrNull { it.height } ?: 0
        placeEndColumn(constraints, height, placeables)
    }
)

private fun MeasureScope.placeEndColumn(
    constraints: Constraints,
    height: Int,
    placeables: List<Placeable>
) = layout(constraints.maxWidth, height) {
    var x = 0
    for (index in 0 until placeables.size - 1) {
        with(placeables[index]) {
            placeRelative(x = x, y = height - this.height)
            x += width
        }
    }
    placeables.last().run {
        placeRelative(
            x = constraints.maxWidth - width,
            y = height - this.height
        )
    }
}
