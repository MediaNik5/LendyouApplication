package org.medianik.lendyou.ui.debts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign.Companion.End
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.ui.component.LendyouFloatingActionButton
import org.medianik.lendyou.ui.component.LendyouSurface
import org.medianik.lendyou.ui.component.TransformSum
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

private val lastDigits = Regex("([^.]*\\.[^.]{2}).*")
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
            var text by remember { mutableStateOf("1000") }
            TextField(
                value = text,
                onValueChange = { newValue -> text = parseValue(newValue) },
                label = { Text("Sum", style = MaterialTheme.typography.overline) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = TransformSum,
                textStyle = TextStyle.Default.copy(textAlign = End)
            )
        }
    }
}

private fun parseValue(newValue: String): String {
    var onlyDigitsAndDot = newValue.filter { char -> char.isDigit() || char == '.' }
    val firstDotIndex = onlyDigitsAndDot.indexOf('.')
    val lastDotIndex = onlyDigitsAndDot.lastIndexOf('.')
    if (firstDotIndex != lastDotIndex)
        onlyDigitsAndDot = onlyDigitsAndDot.removeRange(lastDotIndex..lastDotIndex)

    // delete all digits after dot and two numbers after it
    return onlyDigitsAndDot.replace(lastDigits, "$1")
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