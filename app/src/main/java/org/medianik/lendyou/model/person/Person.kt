package org.medianik.lendyou.model.person

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.medianik.lendyou.model.Jsonable
import org.medianik.lendyou.model.Repos
import java.io.Serializable
import java.util.*

@JvmInline
value class PersonId(val value: String) : Comparable<PersonId> {
    override fun compareTo(other: PersonId) =
        value.compareTo(other.value)

    override fun toString(): String {
        return value
    }

    fun toDebtor(): Debtor = Repos.getInstance().getDebtor(this)
    fun toLender(): Lender = Repos.getInstance().getLender(this)
}

open class Person(
    val id: PersonId,
    val name: String,
    val email: String,
    val passport: Passport,
) : Serializable, Jsonable {
    val lender: Lender by lazy { Lender(id, name, email, passport) }
    val debtor: Debtor by lazy { Debtor(id, name, email, passport) }

    override fun hashCode(): Int {
        return Objects.hash(email, passport)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }

    override fun toString(): String {
        return "Person(id=$id, name='$name', email=$email)"
    }

    override fun toJson(): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id.value)
        jsonObject.addProperty("email", email)
        jsonObject.addProperty("name", name)
        return jsonObject
    }

    companion object {
        @JvmStatic
        fun of(person: String): Person {
            val json = JsonParser.parseString(person).asJsonObject
            return Person(
                PersonId(json.getAsJsonPrimitive("id").asString),
                json.getAsJsonPrimitive("name").asString,
                json.getAsJsonPrimitive("email").asString,
                Passport("", "", "", "")
            )
        }
    }
}