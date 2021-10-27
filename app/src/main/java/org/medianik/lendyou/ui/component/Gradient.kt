package org.medianik.lendyou.ui.component

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

fun Modifier.offsetGradientBackground(
    colors: List<Color>,
) = background(
    Brush.horizontalGradient(
        colors,
        tileMode = TileMode.Clamp,
        startX = 0f,
        endX = Float.POSITIVE_INFINITY,
    )
//    Brush.verticalGradient(
//        colors,
//        startY = -offset,
//        endY = height - offset,
//        tileMode = TileMode.Mirror
//    )
)