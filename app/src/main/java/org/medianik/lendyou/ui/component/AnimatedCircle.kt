package org.medianik.lendyou.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private const val DividerLengthInDegrees = 1.8f
/**
 * A donut chart that animates when loaded.
 */
@Composable
fun AnimatedCircle(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val currentState = rememberSaveable(
        saver = mutableTransitionStateSaver(),
        init = {

            MutableTransitionState(AnimatedCircleProgress.START)
                .apply { targetState = AnimatedCircleProgress.END }
        }
    )
    val stroke = with(LocalDensity.current) { Stroke(5.dp.toPx()) }
    val transition = updateTransition(currentState, label = "Transition")
    val angleOffset by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        },
        label = "AngleOffset"
    ) { progress ->
        if(progress == AnimatedCircleProgress.START){
            0f
        }else{
            360f
        }
    }
    val shift by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = CubicBezierEasing(0f, 0.75f, 0.35f, 0.85f)
            )
        },
        label = "Shift"
    ) { progress ->
        if(progress == AnimatedCircleProgress.START){
            0f
        }else{
            30f
        }
    }
    Canvas(modifier = modifier, onDraw = {
        val innerRadius = (size.minDimension - stroke.width)/2
        val halfSize = size / 2f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(2*innerRadius, 2*innerRadius)
        var startAngle = shift - 90f
        proportions.forEachIndexed{ index, proportion ->
            val currentAngle = proportion*angleOffset
            drawArc(
                color = colors[index],
                startAngle = startAngle + DividerLengthInDegrees / 2f,
                sweepAngle = currentAngle - DividerLengthInDegrees,
                topLeft = topLeft,
                size = size,
                useCenter = false,
                style = stroke
            )
            startAngle+=currentAngle
        }
    })
}

private fun mutableTransitionStateSaver() = Saver<MutableTransitionState<AnimatedCircleProgress>, AnimatedCircleProgress>(
    save = { state -> state.currentState },
    restore = { value ->
        MutableTransitionState(value)
            .apply { targetState = AnimatedCircleProgress.END }
    }
)

private enum class AnimatedCircleProgress { START, END }