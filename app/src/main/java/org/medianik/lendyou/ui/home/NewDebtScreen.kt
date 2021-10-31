package org.medianik.lendyou.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.ui.component.LendyouFloatingActionButton
import org.medianik.lendyou.ui.component.LendyouSurface
import org.medianik.lendyou.ui.component.getBackgroundColorForElevation
import org.medianik.lendyou.ui.theme.LendyouTheme


private val FabPadding = 10.dp

@Composable
fun NewDebtScreen(
    onPendingDebtsRequested: () -> Unit,
) {
    Box {
        NewDebt()
        PendingDebtsButton(onPendingDebtsRequested)
    }
}

@Composable
fun NewDebt(
    modifier: Modifier = Modifier
) {
    LendyouSurface(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier
                .padding(12.dp)
                .padding(top = 100.dp)
        ) {
            var text by remember { mutableStateOf("Hello") }
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Label") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BoxScope.PendingDebtsButton(onPendingDebtsRequested: () -> Unit) {
    LendyouFloatingActionButton(
        onClick = onPendingDebtsRequested,
        modifier = Modifier
            .padding(FabPadding)
            .align(Alignment.TopEnd),
        backgroundColor = getBackgroundColorForElevation(
            color = LendyouTheme.colors.uiBackground,
            elevation = 12.dp
        )
    ) {
        Icon(Icons.Outlined.Schedule, contentDescription = null)
    }
}