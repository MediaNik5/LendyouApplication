package org.medianik.lendyou.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedExpandingCard(
    modifier: Modifier = Modifier,
    initialHeight: Float,
    targetHeight: Float,
    contentColor: Color,
    header: @Composable (fractionOfExpansion: Float) -> Unit,
    expandingContent: @Composable (fractionOfExpansion: Float) -> Unit
){
    val springSpec = SpringSpec<Float>(
        // Determined experimentally
        stiffness = 800f,
        dampingRatio = 0.8f
    )
    val currentHeight = remember { Animatable(initialHeight) }
    LaunchedEffect(key1 = targetHeight){
        currentHeight.animateTo(targetHeight, springSpec)
    }
    LendyouCard(
        modifier = modifier.height(currentHeight.value.toInt().dp),
        contentColor = contentColor,
        content = {
            var fractionOfExpansion = (currentHeight.value - initialHeight) / (targetHeight - initialHeight)
            if(fractionOfExpansion.isNaN())
                fractionOfExpansion = 0f
            Column {
                header(fractionOfExpansion)
                expandingContent(fractionOfExpansion)
            }
        }
    )
}