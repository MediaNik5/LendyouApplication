package org.medianik.lendyou.model.debt

import org.medianik.lendyou.model.person.PersonId
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

data class DebtInfo internal constructor(
    val sum: BigDecimal,
    val lenderId: PersonId,
    val debtorId: PersonId,
    val dateTime: LocalDateTime,
) : Serializable {
    val sumDouble: Double = sum.toDouble()


    override fun toString(): String {
        return "DebtInfo{" +
                "sum=" + sum + ", " +
                "lenderId=" + lenderId + ", " +
                "debtorId=" + debtorId + ", " +
                "dateTime=" + dateTime.toEpochSecond(ZoneOffset.UTC) + '}'
    }

    companion object {
        private const val serialVersionUID = 6529685098267757690L

        @JvmStatic
        @JvmName("of")
        fun of(
            sum: BigDecimal,
            lenderId: PersonId,
            debtorId: PersonId,
            dateTime: LocalDateTime,
        ) = DebtInfo(sum, lenderId, debtorId, dateTime)

        val debtInfoRegex = Regex(
            "DebtInfo\\{sum=([+-]?(?:[0-9]*[.])?[0-9]+), lenderId=(\\w+), debtorId=(\\w+), dateTime=(\\d+)\\}"
        )

        @JvmStatic
        @JvmName("of")
        fun of(string: String): DebtInfo {
            val matcher = debtInfoRegex.find(string)
            return if (matcher != null) {
                val values = matcher.groupValues
                of(
                    BigDecimal(values[1]),
                    PersonId(values[2]),
                    PersonId(values[3]),
                    LocalDateTime.ofEpochSecond(values[4].toLong(), 0, ZoneOffset.UTC)
                )
            } else
                throw IllegalArgumentException("Cannot make DebtInfo of string $string")
        }
    }
}

enum class SortingOrder {
    Sum,
    Lender,
    Debtor,
    DateTime
}