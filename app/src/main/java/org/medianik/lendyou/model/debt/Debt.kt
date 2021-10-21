package org.medianik.lendyou.model.debt

import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.bank.Payment
import java.math.BigDecimal
import java.time.Duration

@JvmInline
value class DebtId(val id: Long)

class Debt
/**
 * @param id unique id of debt
 * @param debtInfo common debt info
 * @param from Account of debtor
 * @param to Account of lender
 * @param period Period between payments
 */(
    private val id: DebtId,
    private val debtInfo: DebtInfo,
    private val from: Account,
    private val to: Account,
    private val period: Duration
) {
    private val status = false
    private val payments = ArrayList<Payment>()
    fun status(): Boolean {
        return status
    }

    fun id(): DebtId {
        return id
    }

    fun debtInfo(): DebtInfo {
        return debtInfo
    }

    /**
     * @return money left to pay to close this debt
     */
    val left: BigDecimal
        get() {
            var current = debtInfo.sum
            for (payment in payments) current = current.subtract(payment.sum())
            return current
        }

    @Suppress("UNCHECKED_CAST")
    fun getPayments(): List<Payment> {
        return payments.clone() as List<Payment>
    }

    val isLatePayment: Boolean
        get() {
            throw RuntimeException("Not implemented exception")
        }
}