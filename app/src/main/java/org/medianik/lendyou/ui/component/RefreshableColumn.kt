package org.medianik.lendyou.ui.component

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.medianik.lendyou.ui.debts.MaxCircleSize
import org.medianik.lendyou.ui.theme.LendyouTheme
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
inline fun RefreshableColumn(
    modifier: Modifier = Modifier,
    proportions: List<Float> = listOf(0.3f, 0.7f),
    colors: List<Color> = LendyouTheme.colors.gradient2_2,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onRefreshRequested: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    var offset by remember { mutableStateOf(sqrt(0f)) }
    val wasRecentlyRefreshed = AtomicBoolean(false)

    LaunchedEffect(true) {
        while (true) {
            delay(10)
            offset = java.lang.Float.min(sqrt(max(offset.pow(2) - 18, 0f)), 50f)
            if (!wasRecentlyRefreshed.get() && offset == 0f) {
                wasRecentlyRefreshed.set(false)
            }
        }
    }

    Column(
        Modifier
            .then(modifier)
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    val oldValue = offset
                    val a = oldValue.pow(2) + delta
                    if (a > 0)
                        offset = sqrt(max(a, 0f))
                    offset - oldValue
                }
            ),
        horizontalAlignment = horizontalAlignment
    ) {
        Box(
            modifier = Modifier
                .size((java.lang.Float.min(2f * offset, MaxCircleSize)).dp)
                .align(Alignment.CenterHorizontally)
        ) {
            val angleOffset = java.lang.Float.min(13f * offset, 360f)
            if (!wasRecentlyRefreshed.get() && angleOffset == 360f) {
                onRefreshRequested()
                wasRecentlyRefreshed.set(true)
            }
            SegmentedCircle(
                angleOffset = angleOffset,
                proportions = proportions,
                colors = colors,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
        content()
    }
}