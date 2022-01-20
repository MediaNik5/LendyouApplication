package org.medianik.lendyou.ui.debts

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.debt.DebtInfo
import org.medianik.lendyou.ui.component.LendyouCard
import org.medianik.lendyou.ui.component.LendyouSurface
import org.medianik.lendyou.ui.component.NothingHereYet
import org.medianik.lendyou.ui.home.DebtCardHeight
import org.medianik.lendyou.ui.home.DebtCardPadding
import org.medianik.lendyou.ui.home.DebtCardShape
import org.medianik.lendyou.ui.home.LenderAndDebtor
import org.medianik.lendyou.ui.theme.LendyouTheme

const val MaxCircleSize = 100f

@Composable
fun PendingDebts() {
    var changes by remember { mutableStateOf(0) }
    val onChange: () -> Unit = { changes++ }
    Repos.getInstance().subscribeToChanges(onChange)

    val pendingDebts = remember(changes) {
        Repos.getInstance().getPendingDebts()
    }

    LendyouSurface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            PendingDebts(
                pendingDebts,
                onAccept = {
                    Repos.getInstance().createDebt(it, Account("NO"), Account("NO"))
                },
                onDecline = {
                    Repos.getInstance().declineDebtAsDebtor(it)
                }
            )
        }
    }
}

@Composable
fun PendingDebts(
    pendingDebts: List<DebtInfo>,
    onAccept: (DebtInfo) -> Unit,
    onDecline: (DebtInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    if (pendingDebts.isEmpty()) {
        NothingHereYet(placeholder = R.string.no_pending_debts)
        return
    }
    PendingDebtsList(
        pendingDebts,
        onAccept,
        onDecline,
        modifier
    )
}

@Composable
fun PendingDebtsList(
    pendingDebts: List<DebtInfo>,
    onAccept: (DebtInfo) -> Unit,
    onDecline: (DebtInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    for (pendingDebt in pendingDebts) {
        key(pendingDebt.dateTime) {
            PendingDebtItem(
                pendingDebt,
                onAccept,
                onDecline,
                modifier
            )
        }
    }
}

private val ButtonSectionHeight = 40.dp

@Composable
fun PendingDebtItem(
    debtInfo: DebtInfo,
    onAccept: (DebtInfo) -> Unit,
    onDecline: (DebtInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LendyouCard(
        modifier = modifier
            .padding(DebtCardPadding)
            .height(DebtCardHeight + ButtonSectionHeight)
            .fillMaxWidth()
            .clip(DebtCardShape),
        contentColor = LendyouTheme.colors.textSecondary,
    ) {
        PendingDebt(
            debtInfo,
            onAccept,
            onDecline
        )
    }
}

@Composable
fun PendingDebt(debtInfo: DebtInfo, onAccept: (DebtInfo) -> Unit, onDecline: (DebtInfo) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.height(DebtCardHeight), verticalAlignment = Alignment.CenterVertically) {
            SumOfPendingDebt(debtInfo)
            LenderAndDebtor(
                Repos.getInstance().getLender(debtInfo.lenderId),
                Repos.getInstance().getDebtor(debtInfo.debtorId),
                Arrangement.End
            )
        }
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
            ActionButton(R.string.button_accept) { onAccept(debtInfo) }
            ActionButton(R.string.button_decline) { onDecline(debtInfo) }
        }
    }
}

private val SumPadding = 10.dp

@Composable
fun SumOfPendingDebt(debtInfo: DebtInfo) {
    Text(
        text = "${stringResource(R.string.debt_sum)}: ${debtInfo.sum}",
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(SumPadding),
    )
}

private val ButtonPadding = 5.dp

@Composable
fun ActionButton(@StringRes text: Int, onClick: () -> Unit) {
    ActionButton(stringResource(text), onClick)
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(horizontal = ButtonPadding),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LendyouTheme.colors.brandSecondary,
            contentColor = LendyouTheme.colors.textPrimary,
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.body1)
    }
}

