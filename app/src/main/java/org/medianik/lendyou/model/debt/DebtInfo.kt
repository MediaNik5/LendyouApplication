package org.medianik.lendyou.model.debt

import org.medianik.lendyou.model.person.PersonId
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.regex.Pattern

data class DebtInfo internal constructor(
    val sum: BigDecimal,
    val lenderId: PersonId,
    val debtorId: PersonId,
    val dateTime: LocalDateTime,
) : Serializable {
    val sumDouble: Double = sum.toDouble()


    override fun toString(): String {
        return "DebtInfo[" +
                "sum=" + sum + ", " +
                "lenderId=" + lenderId + ", " +
                "debtorId=" + debtorId + ", " +
                "dateTime=" + dateTime.toEpochSecond(ZoneOffset.UTC) + ']'
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

        private val debtInfoMatcher = Pattern.compile(
            "DebtInfo\\[sum=([+-]?(?:[0-9]*[.])?[0-9]+), lenderId=(\\w+), debtorId=(\\w+), dateTime=(\\d+)\\]"
        )

        @JvmStatic
        @JvmName("of")
        fun of(string: String): DebtInfo {
            val matcher = debtInfoMatcher.matcher(string)
            return if (matcher.find()) {
                of(
                    BigDecimal(matcher.group(1)),
                    PersonId(matcher.group(2)),
                    PersonId(matcher.group(3)),
                    LocalDateTime.ofEpochSecond(matcher.group(4).toLong(), 0, ZoneOffset.UTC)
                )
            } else throw IllegalArgumentException("Cannot make DebtInfo of string $string")
        }
    }
}

enum class SortingOrder {
    Sum,
    Lender,
    Debtor,
    DateTime
}