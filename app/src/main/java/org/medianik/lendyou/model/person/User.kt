package org.medianik.lendyou.model.person

import org.medianik.lendyou.model.person.Passport
import java.util.*

open class User(
    val phone: String,
    val passport: Passport,
    id: PersonId,
    name: String
) : Person(id, name) {
    override fun hashCode(): Int {
        return Objects.hash(phone, passport)
    }

    override fun toString(): String {
        return "User[" +
                "phone=" + phone + ", " +
                "passport=" + passport + ']'
    }
}