package org.medianik.lendyou.model.person

import java.io.Serializable
import java.util.*

@JvmInline
value class PersonId(val value: Long)

open class Person(
    val id: PersonId,
    val name: String,
    val phone: String,
    val passport: Passport,
) : Serializable {
    val lender: Lender by lazy { Lender(id, name, phone, passport) }
    val debtor: Debtor by lazy { Debtor(id, name, phone, passport) }

    override fun hashCode(): Int {
        return Objects.hash(phone, passport)
    }

    override fun toString(): String {
        return "User[" +
                "phone=" + phone + ", " +
                "passport=" + passport + ']'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }
}