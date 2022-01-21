package org.medianik.lendyou.model.debt

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.medianik.lendyou.model.Jsonable
import org.medianik.lendyou.model.person.PersonId
import org.medianik.lendyou.model.person.toPersonId
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


        @JvmStatic
        @JvmName("of")
        fun of(string: String): DebtInfo {
            val json = JsonParser.parseString(string).asJsonObject
            return of(json)
        }

        fun of(json: JsonObject): DebtInfo {
            return of(
                json.getAsJsonPrimitive("sum").asString.toBigDecimal(),
                json.getAsJsonPrimitive("lenderId").asString.toPersonId(),
                json.getAsJsonPrimitive("debtorId").asString.toPersonId(),
                LocalDateTime.ofEpochSecond(
                    json.getAsJsonPrimitive("dateTime").asLong,
                    0,
                    ZoneOffset.UTC
                ),
                Duration.ofDays(json.getAsJsonPrimitive("payPeriod").asLong)
            )
        }
    }
}

enum class SortingOrder {
    Sum,
    Lender,
    Debtor,
    DateTime
}