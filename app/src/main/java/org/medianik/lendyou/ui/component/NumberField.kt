package org.medianik.lendyou.ui.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

private val numberFieldTextStyle: TextStyle by lazy { TextStyle.Default.copy(textAlign = TextAlign.End) }

@Composable
fun NumberField(
    modifier: Modifier = Modifier,
    input: MutableState<String>,
    isInteger: Boolean = false,
    label: @Composable () -> Unit
) {
    TextField(
        value = input.value,
        onValueChange = { newValue ->
            input.value = parseValue(newValue, isInteger)
        },
        label = label,
        singleLine = true,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = TransformSum,
        textStyle = numberFieldTextStyle
    )
}

private val lastDigits = Regex("([^.]*\\.[^.]{2}).*")

private fun parseValue(newValue: String, isInteger: Boolean): String {
    var onlyDigitsAndDot = newValue.filter { char -> char.isDigit() || char == '.' }
    val firstDotIndex = onlyDigitsAndDot.indexOf('.')
    if (firstDotIndex != -1) {
        if (isInteger)
            return onlyDigitsAndDot.substring(0, firstDotIndex)
        val lastDotIndex = onlyDigitsAndDot.lastIndexOf('.')
        if (firstDotIndex != lastDotIndex)
            onlyDigitsAndDot = onlyDigitsAndDot.removeRange(lastDotIndex..lastDotIndex)
        return onlyDigitsAndDot.removeLastDigitsAfterDotAndTwoDigits()
    }
    return onlyDigitsAndDot
}

private fun String.removeLastDigitsAfterDotAndTwoDigits(): String =
    this.replace(lastDigits, "$1")