package org.medianik.lendyou.ui.debts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.debt.DebtInfo
import org.medianik.lendyou.model.person.Lender
import org.medianik.lendyou.ui.component.*
import org.medianik.lendyou.ui.theme.LendyouTheme
import java.time.LocalDateTime
import java.time.ZoneOffset

private val FabPadding = 10.dp

@Composable
fun NewDebtScreen(
    navigateBack: () -> Unit,
    onPendingDebtsRequested: () -> Unit,
) {
    val lenders = remember {
        Repos.getInstance().getLenders()
    }
    Box {
        NewDebt(navigateBack, lenders)
        PendingDebtsButton(onPendingDebtsRequested)
    }
}

private val TextPadding = PaddingValues(horizontal = 100.dp, vertical = 8.dp)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NewDebt(
    navigateBack: () -> Unit,
    lenders: List<Lender>,
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
            val inputSum = remember { mutableStateOf("1000") }
            val selectedLender = remember { mutableStateOf(-1) }
            NumberField(Modifier.fillMaxWidth(), inputSum) { Text("Sum") }

            DropdownMenuInput(
                selectedLender,
                items = lenders,
                placeholder = R.string.lender_placeholder,
                value = { this.name /*Lender's name*/ }
            )
            ConfirmButton(selectedLender.value) {
                Repos.getInstance().askForDebt(
                    DebtInfo(
                        inputSum.value.toBigDecimal(),
                        lenders[selectedLender.value].id,
                        Repos.getInstance().thisPerson(),
                        LocalDateTime.now(ZoneOffset.UTC)
                    )
                )
                navigateBack()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ColumnScope.ConfirmButton(selectedLender: Int, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = selectedLender != -1,
        modifier = Modifier
            .align(Alignment.End)
            .padding(top = 50.dp)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                LendyouTheme.colors.brandSecondary,
                LendyouTheme.colors.textSecondary
            )
        ) {
            Text(text = "Confirm")
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