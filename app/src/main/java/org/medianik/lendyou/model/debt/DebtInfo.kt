package org.medianik.lendyou.model.debt

import org.medianik.lendyou.model.person.PersonId
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class DebtInfo internal constructor(
    val sum: BigDecimal,
    val lenderId: PersonId,
    val debtorId: PersonId,
    val dateTime: LocalDateTime
) {

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other.javaClass != this.javaClass) return false
        val that = other as DebtInfo
        return sum == that.sum && lenderId == that.lenderId && debtorId == that.debtorId &&
                dateTime == that.dateTime
    }

    override fun hashCode(): Int {
        return Objects.hash(sum, lenderId, debtorId, dateTime)
    }

    override fun toString(): String {
        return "DebtInfo[" +
                "sum=" + sum + ", " +
                "lenderId=" + lenderId + ", " +
                "debtorId=" + debtorId + ", " +
                "dateTime=" + dateTime + ']'
    }
}