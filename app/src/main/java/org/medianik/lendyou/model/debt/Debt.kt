package org.medianik.lendyou.model.debt

import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.bank.Payment
import java.io.Serializable
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime

@JvmInline
value class DebtId(val value: Long)

class Debt
/**
 * @param id unique id of debt
 * @param debtInfo common debt info
 * @param from Account of debtor
 * @param to Account of lender
 * @param period Period between payments
 */(
    val id: DebtId,
    val debtInfo: DebtInfo,
    private val from: Account,
    private val to: Account,
    private val period: Duration
) : Serializable {
    private val status = false
    private val payments = ArrayList<Payment>()
    fun status(): Boolean {
        return status
    }

    /**
     * @return money left to pay to close this debt
     */
    val left: BigDecimal
        get() {
            var current = debtInfo.sum
            for (payment in payments)
                current = current.subtract(payment.sum())
            return current
        }

    val leftDouble: Double
        get(){
            var current = debtInfo.sumDouble
            for(payment in payments)
                current -= payment.sumDouble()
            return current
        }

    @Suppress("UNCHECKED_CAST")
    fun getPayments(): List<Payment> {
        return payments.clone() as List<Payment>
    }

    override fun toString(): String {
        return "Debt(id=$id, debtInfo=$debtInfo, from=$from, to=$to, period=$period, status=$status, payments=$payments)"
    }

    fun addPayment(sum: BigDecimal, time: LocalDateTime = LocalDateTime.now()) {
        if(sum > left)
            throw UnsupportedOperationException("Cannot pay more than you owe.")
        payments.add(Payment(time, sum, from, to))
    }

    val isLatePayment: Boolean
        get() {
            throw RuntimeException("Not implemented exception")
        }

}