package org.medianik.lendyou.model.debt

import org.medianik.lendyou.model.person.PersonId
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class DebtInfo internal constructor(
    val sum: BigDecimal,
    val lenderId: PersonId,
    val debtorId: PersonId,
    val dateTime: LocalDateTime,
) : Serializable{
    val sumDouble: Double = sum.toDouble()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DebtInfo

        if (lenderId != other.lenderId) return false
        if (debtorId != other.debtorId) return false
        if (dateTime != other.dateTime) return false
        if (sumDouble != other.sumDouble) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sum.hashCode()
        result = 31 * result + lenderId.hashCode()
        result = 31 * result + debtorId.hashCode()
        result = 31 * result + dateTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "DebtInfo[" +
                "sum=" + sum + ", " +
                "lenderId=" + lenderId + ", " +
                "debtorId=" + debtorId + ", " +
                "dateTime=" + dateTime + ']'
    }
}