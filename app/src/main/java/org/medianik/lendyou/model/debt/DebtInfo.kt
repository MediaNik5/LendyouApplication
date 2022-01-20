package org.medianik.lendyou.model.debt

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.medianik.lendyou.model.Jsonable
import org.medianik.lendyou.model.person.PersonId
import java.io.Serializable
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

data class DebtInfo internal constructor(
    val sum: BigDecimal,
    val lenderId: PersonId,
    val debtorId: PersonId,
    val dateTime: LocalDateTime,
    val payPeriod: Duration,
) : Serializable, Jsonable {
    val sumDouble: Double = sum.toDouble()


    override fun toString(): String {
        return toJson().toString()
    }

    override fun toJson(): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("sum", sum.toString())
        jsonObject.addProperty("lenderId", lenderId.toString())
        jsonObject.addProperty("debtorId", debtorId.toString())
        jsonObject.addProperty("dateTime", dateTime.toEpochSecond(ZoneOffset.UTC))
        jsonObject.addProperty("payPeriod", payPeriod.toDays())
        return jsonObject
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
            period: Duration
        ) = DebtInfo(sum, lenderId, debtorId, dateTime, period)

        val debtInfoRegex = Regex(
            "DebtInfo\\{sum=([+-]?(?:[0-9]*[.])?[0-9]+), lenderId=(\\w+), debtorId=(\\w+), dateTime=(\\d+), payPeriod=(\\d+)\\}"
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
                    LocalDateTime.ofEpochSecond(values[4].toLong(), 0, ZoneOffset.UTC),
                    Duration.ofDays(values[5].toLong())
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