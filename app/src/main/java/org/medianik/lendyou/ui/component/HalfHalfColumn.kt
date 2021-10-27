package org.medianik.lendyou.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HalfHalfColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    contentTop: @Composable ColumnScope.() -> Unit,
    contentBottom: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxHeight()
            .then(modifier),
        horizontalAlignment = horizontalAlignment
    ) {
        Column(
            Modifier
                .fillMaxHeight(0.5f)
                .then(modifier),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = horizontalAlignment,
            content = contentTop
        )
        Column(
            Modifier
                .fillMaxHeight(0.5f)
                .then(modifier),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = horizontalAlignment,
            content = contentBottom
        )
    }
}