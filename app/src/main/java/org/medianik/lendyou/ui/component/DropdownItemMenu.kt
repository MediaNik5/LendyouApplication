package org.medianik.lendyou.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.ui.theme.LendyouTheme

@Composable
fun <T> DropdownMenuInput(
    selectedIndex: MutableState<Int>,
    items: List<T>,
    @StringRes placeholder: Int,
    value: T.() -> String,
    onClick: () -> Unit = {}
) {
    val expanded = remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxWidth()
//            .wrapContentSize(Alignment.TopEnd)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        val index = selectedIndex.value
        ItemText(if (index != -1) items[index] else null, value, placeholder, expanded)
        DropdownItemMenu(expanded, items, value, selectedIndex, onClick)
    }
}

@Composable
private fun <T> ItemText(
    item: T?,
    value: T.() -> String,
    @StringRes placeholder: Int,
    expanded: MutableState<Boolean>,
) {
    Text(
        text = item?.value() ?: stringResource(placeholder),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { expanded.value = true }
            .background(
                getBackgroundColorForElevation(
                    color = LendyouTheme.colors.uiBackground,
                    elevation = 25.dp
                )
            )
            .width(200.dp)
            .padding(8.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun <T> DropdownItemMenu(
    expanded: MutableState<Boolean>,
    items: List<T>,
    value: T.() -> String,
    selectedIndex: MutableState<Int>,
    onClick: () -> Unit = {}
) {
    Box {
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    selectedIndex.value = index
                    expanded.value = false
                    onClick()
                }) {
                    Text(
                        item.value(),
                        color = LendyouTheme.colors.textSecondary,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

