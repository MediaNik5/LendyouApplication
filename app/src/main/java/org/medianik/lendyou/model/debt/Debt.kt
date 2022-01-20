package org.medianik.lendyou.model.debt

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.medianik.lendyou.model.Jsonable
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.bank.Payment
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

@JvmInline
value class DebtId(val id: Long) {
    override fun toString(): String {
        return id.toString()
    }

    fun toDebt(): Debt? {
        return Repos.getInstance().getDebt(this)
    }
}

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
    @JvmField
    val id: DebtId = DebtId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
    private val payments: MutableList<Payment> = ArrayList()
) : Serializable, Jsonable {
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
            for (payment in payments)
                current -= payment.sumDouble
            return current
        }

    fun getPayments(): List<Payment> {
        return payments
    }

    override fun toString(): String {
        return toJson().toString()
    }

    override fun toJson(): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id.id)
        jsonObject.add("debtInfo", debtInfo.toJson())
        jsonObject.addProperty("from", from.toString())
        jsonObject.addProperty("to", to.toString())
        return jsonObject
    }

    fun addPayment(sum: BigDecimal, time: LocalDateTime = LocalDateTime.now()) {
        if (sum > left)
            throw UnsupportedOperationException("Cannot pay more than you owe.")
        payments.add(
            Payment(
                time,
                sum,
                id.id
            )
        )
    }

    fun addPayment(payment: Payment): Boolean {
        if (!payments.contains(payment)) {
            payments.add(payment)
            return true
        }
        return false
    }

    val isLatePayment: Boolean
        get() {
            throw RuntimeException("Not implemented exception")
        }

    companion object {
        private val debtRegex = Regex(
            "Debt\\{id=(\\d+), debtInfo=(" + DebtInfo.debtInfoRegex + "), from=(\\w+), to=(\\w+)\\}"
        )

        @JvmStatic
        fun of(string: String): Debt {
            val matcher = debtRegex.find(string)
            if (matcher != null) {
                val values = matcher.groupValues
                return of(
                    values[1].toLong(),
                    DebtInfo.of(values[2]),
                    Account(values[8]),
                    Account(values[9]),
                )
            }
            throw IllegalArgumentException("Cannot convert to Debt $string")
        }

        fun of(id: Long, debtInfo: DebtInfo, from: Account, to: Account): Debt {
            return Debt(debtInfo, from, to, DebtId(id))
        }
    }
}

fun Debt.isNotPaid() =
    this.status() == DebtStatus.NOT_PAID

fun Debt.lastPayment() =
    this.getPayments().lastOrNull()

fun Debt.lastPaymentDateOrInitial() =
    (this.lastPayment()?.dateTime ?: this.debtInfo.dateTime).toLocalDate()