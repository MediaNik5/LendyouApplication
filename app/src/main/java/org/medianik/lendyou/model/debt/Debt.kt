package org.medianik.lendyou.model.debt

import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.bank.Payment
import java.io.Serializable
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

@JvmInline
value class DebtId(val id: Long)

class Debt
/**
 * @param id unique id of debt
 * @param debtInfo common debt info
 * @param from Account of debtor
 * @param to Account of lender
 * @param payPeriod Period between payments
 */(
    val debtInfo: DebtInfo,
    val from: Account,
    val to: Account,
    val payPeriod: Duration,
    @JvmField
    val id: DebtId = DebtId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
    private val payments: MutableList<Payment> = ArrayList()
) : Serializable {
    private val status = DebtStatus.NOT_PAID
    fun status(): DebtStatus {
        return status
    }

    /**
     * @return money left to pay to close this debt
     */
    val left: BigDecimal
        get() {
            var current = debtInfo.sum
            for (payment in payments)
                current = current.subtract(payment.sum)
            return current
        }

    val leftDouble: Double
        get(){
            var current = debtInfo.sumDouble
            for(payment in payments)
                current -= payment.sumDouble
            return current
        }

    fun getPayments(): List<Payment> {
        return payments
    }

    override fun toString(): String {
        return "Debt(id=$id, debtInfo=$debtInfo, from=$from, to=$to, period=$payPeriod, status=$status, payments=$payments)"
    }

    fun addPayment(sum: BigDecimal, time: LocalDateTime = LocalDateTime.now()) {
        if (sum > left)
            throw UnsupportedOperationException("Cannot pay more than you owe.")
        payments.add(Payment(time, sum, from, to))
    }

    val isLatePayment: Boolean
        get() {
            throw RuntimeException("Not implemented exception")
        }
}

fun Debt.isNotPaid() =
    this.status() == DebtStatus.NOT_PAID

fun Debt.lastPayment() =
    this.getPayments().lastOrNull()

fun Debt.lastPaymentDateOrInitial() =
    (this.lastPayment()?.dateTime ?: this.debtInfo.dateTime).toLocalDate()