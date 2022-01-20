package org.medianik.lendyou.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.End
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.bank.Payment
import org.medianik.lendyou.model.debt.*
import org.medianik.lendyou.model.person.Debtor
import org.medianik.lendyou.model.person.Lender
import org.medianik.lendyou.ui.MainDestinations
import org.medianik.lendyou.ui.component.*
import org.medianik.lendyou.ui.debts.NewDebtScreen
import org.medianik.lendyou.ui.debts.PendingDebts
import org.medianik.lendyou.ui.theme.LendyouTheme
import org.medianik.lendyou.util.DateTimeUtil
import org.medianik.lendyou.util.DateTimeUtil.dateTimeFormat
import org.medianik.lendyou.util.DateTimeUtil.isLaterThanToday
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun NavGraphBuilder.addDebtScreenGraph(
    onPendingDebtsRequested: (NavBackStackEntry) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable(MainDestinations.NEW_DEBT_ROUT) { from ->
        NewDebtScreen(navigateBack) { onPendingDebtsRequested(from) }
    }
    composable(MainDestinations.PENDING_DEBTS_ROUT) { from ->
        PendingDebts()
    }
}

@Composable
fun Debts(
    onDebtClick: (DebtId) -> Unit,
    onNewDebtRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    var changes by remember { mutableStateOf(0) }
    val onChange: () -> Unit = { changes++ }

    val debts = remember(changes) {
        Repos.getInstance().getDebts()
    }
    Repos.getInstance().subscribeToChanges(onChange)

    Box(modifier.fillMaxSize()) {
        Debts(
            debts,
            onDebtClick,
            Modifier,
            onChange,
        )
        LendyouFloatingActionButton(onClick = onNewDebtRequested)
        LendyouFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 10.dp, vertical = 40.dp),
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Outlined.FilterList,
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
    val expandedIndex = remember { mutableStateOf(-1) }
    LendyouSurface(
        modifier = modifier.fillMaxSize(),
        contentColor = LendyouTheme.colors.textInteractive
    ) {
        DebtsList(debts, onDebtClick, onDebtsChange, expandedIndex)
    }
}

val DebtCardHeight = 100.dp
private val ExpandedDebtCardHeight = DebtCardHeight * 3
val DebtCardPadding = 5.dp

val DebtCardShape = RoundedCornerShape(16.dp)

@Composable
fun DebtsList(
    debts: List<Debt>,
    onDebtClick: (DebtId) -> Unit,
    onDebtsChange: () -> Unit,
    expandedIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
) {

    Column {
        if (debts.isEmpty()) {
            NothingHereYet(R.string.no_debts)
        } else {

            for (index in debts.indices) {
                key(debts[index].id) {
                    DebtItem(
                        debts[index],
                        index,
                        onDebtClick,
                        onDebtsChange,
                        expandedIndex,
                    )
                }
            }
        }
    }
}

@Composable
fun DebtItem(
    debt: Debt,
    index: Int,
    onDebtClick: (DebtId) -> Unit,
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
        header = { fractionOfExpansion -> DebtHeader(debt, isExpanded()) },
        expandingContent = { fractionOfExpansion -> ExpandedDebtInfo(fractionOfExpansion, debt) }
    )
}

@Composable
private fun DebtHeader(debt: Debt, isExpanded: Boolean) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(DebtCardHeight)
        ) {
            DebtCircle(DebtCardHeight, debt)
            SumOfDebt(debt)
            LenderAndDebtor(debt.debtInfo.getLender(), debt.debtInfo.getDebtor())
        }

        if (!isExpanded)
            LendyouDivider(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            )
    }
}

@Composable
private fun ExpandedDebtInfo(
    fractionOfExpansion: Float,
    debt: Debt
) {
    if (fractionOfExpansion != 0f) {
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

@Composable
fun AwaitingPayment(debt: Debt) {
    val lastPaymentDate = debt.lastPaymentDateOrInitial()
    val nextPaymentDate = lastPaymentDate.toEpochDay() + debt.debtInfo.payPeriod.toDays()
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
        EndRow {
            val paymentText = stringResource(id = R.string.payment_item)
                .replace("%sum", payment.sum.toString())
                .replace("%account", DebtId(payment.debtId).toDebt()!!.debtInfo.getLender().name)
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
    val zoned = date.plusSeconds(ZoneId.systemDefault().rules.getOffset(date).totalSeconds.toLong())
    if(showDate)
        string += "${zoned.toLocalDate()}"
    if(showDate && showTime) //delimiter
        string += " "
    if(showTime)
        string += zoned.toLocalTime().format(dateTimeFormat)

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
    lender: Lender,
    debtor: Debtor,
    horizontalArrangement: Arrangement.Horizontal = End
) {
    Row(
        Modifier
            .fillMaxSize(),
        horizontalArrangement = horizontalArrangement
    ) {
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
                    lender.name,
                    style = MaterialTheme.typography.h6
                )
            },
            contentBottom = {
                Text(
                    debtor.name,
                    style = MaterialTheme.typography.h6
                )
            }
        )
    }
}

private fun DebtInfo.getDebtor() =
    Repos.getInstance().getDebtor(debtorId)

private fun DebtInfo.getLender() =
    Repos.getInstance().getLender(lenderId)


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
            debt = Repos.getInstance().getDebts().first(),
            index = 0,
            onDebtClick = { },
            onDebtsChange = { },
            expandedIndex = remember { mutableStateOf(-1) }
        )
    }
}