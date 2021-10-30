package org.medianik.lendyou.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.bank.Payment
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtId
import org.medianik.lendyou.model.debt.isNotPaid
import org.medianik.lendyou.model.debt.lastPaymentDateOrInitial
import org.medianik.lendyou.ui.component.*
import org.medianik.lendyou.ui.theme.LendyouTheme
import org.medianik.lendyou.util.DateTimeUtil
import org.medianik.lendyou.util.DateTimeUtil.dateTimeFormat
import org.medianik.lendyou.util.DateTimeUtil.isLaterThanToday
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun Debts(
    onDebtClick: (DebtId) -> Unit,
    onNewDebtRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    var changes by rememberSaveable { mutableStateOf(0) }

    val debts = rememberSaveable(changes) {
        Repos.getInstance().currentRepo.getDebts()
    }

    Box {
        Debts(
            debts,
            onDebtClick,
            modifier,
        ) { changes++ }
        LendyouFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            onClick = onNewDebtRequested
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = ""
            )
        }
    }

}

@Composable
private fun Debts(
    debts: List<Debt>,
    onDebtClick: (DebtId) -> Unit,
    modifier: Modifier,
    onDebtsChange: () -> Unit,
) {
    val expandedIndex = rememberSaveable{ mutableStateOf(-1) }
    LendyouSurface(
        modifier = modifier.fillMaxSize(),
        contentColor = LendyouTheme.colors.textInteractive
    ) {
        DebtsList(debts, onDebtClick, onDebtsChange, expandedIndex)
    }
}

private val DebtCardHeight = 100.dp
private val ExpandedDebtCardHeight = DebtCardHeight * 3
private val DebtCardPadding = 5.dp

private val DebtCardShape = RoundedCornerShape(16.dp)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DebtsList(
    debts: List<Debt>,
    onDebtClick: (DebtId) -> Unit,
    onDebtsChange: () -> Unit,
    expandedIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
) {
    Column(modifier.verticalScroll(rememberScrollState())){
        for(index in debts.indices){
            val gradient = when(index % 2){
                0 -> LendyouTheme.colors.gradient6_1
                else -> LendyouTheme.colors.gradient6_2
            }
            key(debts[index].id){
                DebtItem(
                    debts[index],
                    index,
                    onDebtClick,
                    gradient,
                    onDebtsChange,
                    expandedIndex,
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DebtItem(
    debt: Debt,
    index: Int,
    onDebtClick: (DebtId) -> Unit,
    gradient: List<Color>,
    onDebtsChange: () -> Unit,
    expandedIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
) {
    fun isExpanded() = expandedIndex.value == index
    val currentCardHeight =
        if(isExpanded())
            ExpandedDebtCardHeight
        else
            DebtCardHeight/* * ln(debt.debtInfo().sumDouble/100+1).toFloat()*/

    AnimatedExpandingCard(
        modifier = modifier
            .padding(DebtCardPadding)
            .fillMaxWidth()
            .clip(DebtCardShape)
            .clickable {
                expandedIndex.value =
                    if (isExpanded()) -1
                    else index
            },
        initialHeight = DebtCardHeight.value,
        targetHeight = currentCardHeight.value,
        contentColor = LendyouTheme.colors.textSecondary,
        header = { fractionOfExpansion ->
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DebtCardHeight)
                ) {
                    DebtCircle(DebtCardHeight, debt)
                    SumOfDebt(debt)
                    LenderAndDebtor(debt)
                }

                if(!isExpanded())
                    LendyouDivider(
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth())
            }
        },
        expandingContent = { fractionOfExpansion ->
            if(fractionOfExpansion != 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = DebtCardPadding * 2)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val dateTimeText = stringResource(id = R.string.debt_given)
                            .replace("%datetime", dateFormat(debt.debtInfo.dateTime))
                        Text(dateTimeText, modifier = Modifier.align(Alignment.End))
                        Spacer(modifier = Modifier.height(3.dp))
                        LendyouDivider()
                        Spacer(modifier = Modifier.height(3.dp))
                        Payments(debt.getPayments())
                        if (debt.isNotPaid())
                            AwaitingPayment(debt)
                    }
                }
            }
        }
    )
}

@Composable
fun AwaitingPayment(debt: Debt) {
    val lastPaymentDate = debt.lastPaymentDateOrInitial()
    val nextPaymentDate = lastPaymentDate.toEpochDay() + debt.payPeriod.toDays()
    if (isLaterThanToday(nextPaymentDate)) {
        Text(
            stringResource(R.string.overdue_payment)
                .replace("%date", LocalDate.ofEpochDay(nextPaymentDate).toString()),
            color = LendyouTheme.colors.error
        )
    } else {
        Text(
            stringResource(R.string.next_payment)
                .replace("%date", LocalDate.ofEpochDay(nextPaymentDate).toString())
        )
    }
}

@Composable
fun Payments(payments: List<Payment>) {
    Text(stringResource(R.string.payments), style = MaterialTheme.typography.h6)
    if (payments.isNotEmpty()) {
        Column {
            for (payment in payments) {
                PaymentItem(payment)
            }
        }
    } else {
        Text(stringResource(R.string.no_payments))
    }
}
@Composable
fun PaymentItem(payment: Payment) {
    Row {
        EndColumn {
            val paymentText = stringResource(id = R.string.payment_item)
                .replace("%sum", "${payment.sum}")
                .replace("%account", "${payment.to}")
            Text(paymentText)

            val dateTime = payment.dateTime.toLocalTime().format(dateTimeFormat)
            Text(
                dateTime,
                style = MaterialTheme.typography.caption
            )
        }
    }
}


fun dateFormat(
    date: LocalDateTime,
    isToday: Boolean = DateTimeUtil.isToday(date),
    showTime: Boolean = isToday,
    showDate: Boolean = !isToday
): String {
    var string = ""
    if(showDate)
        string += "${date.toLocalDate()}"
    if(showDate && showTime) //delimiter
        string += " "
    if(showTime)
        string += date.toLocalTime().format(dateTimeFormat)

    return string
}


@Composable
private fun DebtCircle(
    currentCardHeight: Dp,
    debt: Debt
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(currentCardHeight)
    ) {
        // Proportions of paid to left to pay
        val proportions = proportionsOfPaidAndUnpaidPartsOfDebt(debt)
        AnimatedCircle(
            proportions,
            LendyouTheme.colors.gradient2_1,
            Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(10.dp)
        )
    }
}

private fun proportionsOfPaidAndUnpaidPartsOfDebt(debt: Debt): List<Float> {
    val leftSum = debt.leftDouble
    return listOf(
        ((debt.debtInfo.sumDouble - leftSum) / debt.debtInfo.sumDouble).toFloat(),
        (leftSum / debt.debtInfo.sumDouble).toFloat()
    )
}

@Composable
fun SumOfDebt(debt: Debt) {
    HalfHalfColumn(
        Modifier.widthIn(80.dp),
        contentTop = {
            Text(
                text = "${stringResource(R.string.debt_sum)}: ${debt.debtInfo.sum}",
                style = MaterialTheme.typography.h6,

            )
        },
        contentBottom = {
            Text(
                text = "${stringResource(R.string.debt_left)}: ${debt.left}",
                style = MaterialTheme.typography.caption
            )
        }
    )
}

@Composable
fun LenderAndDebtor(
    debt: Debt,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(), horizontalArrangement = Arrangement.End) {
        HalfHalfColumn(
            modifier = Modifier.padding(vertical = 3.dp),
            horizontalAlignment = Alignment.End,
            contentTop = {
                Text(
                    stringResource(R.string.lender),
                    style = MaterialTheme.typography.caption
                )
            },
            contentBottom = {
                Text(
                    stringResource(R.string.debtor),
                    style = MaterialTheme.typography.caption
                )
            }
        )
        HalfHalfColumn(
            modifier = Modifier.padding(horizontal = DebtCardPadding),
            horizontalAlignment = Alignment.Start,
            contentTop = {
                Text(
                    debt.getLender().name,
                    style = MaterialTheme.typography.h6
                )
            },
            contentBottom = {
                Text(
                    debt.getDebtor().name,
                    style = MaterialTheme.typography.h6
                )
            }
        )
    }
}

private fun Debt.getDebtor() =
    Repos.getInstance().currentRepo.getDebtor(debtInfo.debtorId)

private fun Debt.getLender() =
    Repos.getInstance().currentRepo.getLender(debtInfo.lenderId)


// The Cards show a gradient which spans 3 cards and scrolls with parallax.
private val gradientWidth
    @Composable
    get() = with(LocalDensity.current) {
        (6 * (DebtCardHeight + DebtCardPadding).toPx())
    }

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DebtItemPreview(){
    LendyouTheme{
        DebtItem(
            debt = Repos.getInstance().currentRepo.getDebts().first(),
            index = 0,
            onDebtClick = { },
            gradient = LendyouTheme.colors.gradient6_1,
            onDebtsChange = {  },
            expandedIndex = remember { mutableStateOf(-1)}
        )
    }
}