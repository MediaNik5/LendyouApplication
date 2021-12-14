package org.medianik.lendyou.ui.component

import android.util.Log
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
import java.lang.Float.min
import java.util.concurrent.atomic.AtomicBoolean
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

    LaunchedEffect(offset > 0f) {
        while (offset > 0) {
            delay(10)
            offset = min(offset - 3, 50f)
            if (!wasRecentlyRefreshed.get() && offset == 0f) {
                wasRecentlyRefreshed.set(false)
            }
            Log.d("Lendyou", "LaunchedEffect, $offset")
        }
    }

    Column(
        Modifier
            .then(modifier)
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    if (wasRecentlyRefreshed.get())
                        return@rememberScrollableState 0f
                    val oldValue = offset
                    val a = oldValue + delta / 8
                    offset = a
                    Log.d("Lendyou", offset.toString())
                    offset - oldValue
                }
            ),
        horizontalAlignment = horizontalAlignment
    ) {
        Box(
            modifier = Modifier
                .size((min(2f * offset, MaxCircleSize)).dp)
                .align(Alignment.CenterHorizontally)
        ) {
            val angleOffset = min(14f * offset, 360f)
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