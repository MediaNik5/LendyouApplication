package org.medianik.lendyou.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.bank.Payment
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtId
import org.medianik.lendyou.model.person.Debtor
import org.medianik.lendyou.model.person.Lender
import org.medianik.lendyou.ui.MainDestinations
import org.medianik.lendyou.ui.component.*
import org.medianik.lendyou.ui.theme.LendyouTheme
import java.math.BigDecimal
import java.time.LocalDateTime

fun NavGraphBuilder.addPaymentScreenGraph(
    navigateBack: () -> Unit
) {
    composable(MainDestinations.NEW_PAYMENT_ROUT) { from ->
        NewPayment(navigateBack)
    }
}

@Composable
fun Payments(
    modifier: Modifier = Modifier,
    onNewPaymentRequested: () -> Unit,
) {
    var changes by remember { mutableStateOf(0) }
    val onChange: () -> Unit = { changes++ }

    val payments = remember(changes) {
        Repos.getInstance().getDebts().flatMap { it.getPayments() }
    }

    Repos.getInstance().subscribeToChanges(onChange)

    Box(modifier.fillMaxSize()) {
        Payments(
            payments,
            onChange
        )
        LendyouFloatingActionButton(onClick = onNewPaymentRequested)
    }
}

@Composable
private fun Payments(
    payments: List<Payment>,
    onChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    LendyouSurface(
        modifier = modifier.fillMaxSize(),
        contentColor = LendyouTheme.colors.textInteractive
    ) {
        PaymentsList(payments, onChange)
    }
}

@Composable
fun PaymentsList(
    payments: List<Payment>,
    onChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        if (payments.isEmpty()) {
            item {
                NothingHereYet(R.string.no_payments)
            }
        } else {
            items(payments.size) { index ->
                PaymentItem(
                    payments[index],
                    onChange
                )
            }
        }
    }
}

val PaymentCardHeight = 100.dp
val PaymentCardPadding = 5.dp
val PaymentCardShape = RoundedCornerShape(16.dp)

@Composable
private fun PaymentItem(
    payment: Payment,
    onChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LendyouCard(
        modifier = modifier
            .height(PaymentCardHeight)
            .padding(PaymentCardPadding)
            .fillMaxWidth()
            .clip(PaymentCardShape)
    ) {
        HalfHalfColumn(
            modifier = Modifier.padding(start = 10.dp),
            contentTop = {
                Text(
                    text = "${stringResource(R.string.debt_sum)}: ${payment.sum}",
                    style = MaterialTheme.typography.h6
                )
            },
            contentBottom = {
                Text(
                    text = dateFormat(payment.dateTime),
                    style = MaterialTheme.typography.subtitle1
                )
            }
        )
        val debt = DebtId(payment.debtId).toDebt()!!
        LenderAndDebtor(debt.lender(), debt.debtor())
    }
}

@Composable
fun NewPayment(navigateBack: () -> Unit) {
    val debts = remember {
        Repos.getInstance().getDebts()
    }
    LendyouSurface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(
                top = 100.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 12.dp
            )
        ) {
            val inputSum = remember { mutableStateOf("1000") }
            NumberField(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                inputSum
            ) { Text("Sum") }

            val selectedDebt = remember { mutableStateOf(-1) }
            DropdownMenuInput(
                selectedDebt,
                debts,
                R.string.debt,
                { "${this.debtInfo.lenderId.toLender().name}, on ${dateFormat(this.debtInfo.dateTime)}, sum ${this.debtInfo.sum}" }
            )
            val context = LocalContext.current
            if (selectedDebt.value != -1) {
                val debt = debts[selectedDebt.value]
                val left = debt.left
                val sumMoreThanLeft =
                    stringResource(R.string.payment_more_left).replace("%s", left.toString())
                ConfirmButton(selectedDebt.value) {
                    val sum = inputSum.value.toBigDecimal()
                    if (sum.compareTo(BigDecimal.ZERO) == 0) {
                        Toast.makeText(context, R.string.zero_value, Toast.LENGTH_LONG).show()
                        return@ConfirmButton
                    }
                    if (sum > left) {
                        Toast.makeText(context, sumMoreThanLeft, Toast.LENGTH_LONG).show()
                    }
                    Repos.getInstance().addPayment(
                        Payment(
                            LocalDateTime.now(),
                            sum,
                            debt.id.id
                        )
                    )
                    navigateBack()
                }
            }
        }
    }
}

private fun Debt.debtor(): Debtor = debtInfo.debtorId.toDebtor()
private fun Debt.lender(): Lender = debtInfo.lenderId.toLender()