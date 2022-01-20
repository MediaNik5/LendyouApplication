package org.medianik.lendyou.ui.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.window.Dialog
import org.medianik.lendyou.R
import org.medianik.lendyou.ui.theme.LendyouTheme


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColumnScope.ConfirmButton(selected: Int, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = selected != -1,
        modifier = Modifier
            .align(Alignment.End)
            .padding(top = 50.dp)
    ) {
        val confirmScreen = remember { mutableStateOf(false) }
        Button(
            onClick = {
                confirmScreen.value = true
            },
            enabled = !confirmScreen.value,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = LendyouTheme.colors.brandSecondary,
                contentColor = LendyouTheme.colors.textSecondary,
                disabledContentColor = LendyouTheme.colors.uiFloated,
                disabledBackgroundColor = LendyouTheme.colors.uiBackground
            )
        ) {
            Text(stringResource(R.string.confirm))
        }
        WarningScreen(visible = confirmScreen, onClick = onClick)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WarningScreen(
    visible: MutableState<Boolean>,
    enter: EnterTransition = slideInVertically() + expandVertically(
        expandFrom = Alignment.Bottom
    ),
    exit: ExitTransition = slideOutVertically() + shrinkVertically() + fadeOut(),
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible.value,
        enter = enter,
        exit = exit,
    ) {
        Dialog(onDismissRequest = { visible.value = false }) {
            LendyouSurface(
                Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = LendyouTheme.colors.uiBorder
            ) {
                Column(
                    Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(R.string.are_you_sure),
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                    Row(horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = { visible.value = false },
                            modifier = Modifier.padding(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = LendyouTheme.colors.error,
                                contentColor = LendyouTheme.colors.textSecondary,
                            )
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                        Button(
                            onClick = { visible.value = true; onClick() },
                            modifier = Modifier.padding(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                LendyouTheme.colors.brandSecondary,
                                LendyouTheme.colors.textSecondary
                            )
                        ) {
                            Text(stringResource(R.string.confirm))
                        }
                    }
                }
            }
        }
    }
}