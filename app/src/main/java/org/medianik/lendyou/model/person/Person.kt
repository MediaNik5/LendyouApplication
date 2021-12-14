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

    override fun toString(): String {
        return "User[" +
                "phone=" + email + ", " +
                "passport=" + passport + ']'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }
}