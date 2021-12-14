package org.medianik.lendyou.model.person

import java.io.Serializable
import java.util.*

@JvmInline
value class PersonId(val value: String) : Comparable<PersonId> {
    override fun compareTo(other: PersonId) =
        value.compareTo(other.value)

    override fun toString(): String {
        return value
    }
}

open class Person(
    val id: PersonId,
    val name: String,
    val email: String,
    val passport: Passport,
) : Serializable {
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

    companion object {
        private val personRegex =
            Regex("Person\\{id=(\\w+), name='(.+)', email=(.+@\\S+)\\}$")

        @JvmStatic
        fun of(person: String): Person {
            val matcher = personRegex.find(person)
            if (matcher != null) {
                val values = matcher.groupValues
                return Person(
                    PersonId(values[1]),
                    values[2],
                    values[3],
                    Passport("sdfsd", values[2], "sdfsdf", "sdfsdf")
                )
            }
            throw IllegalArgumentException("Cannot convert string to person: $person")
        }
    }
}