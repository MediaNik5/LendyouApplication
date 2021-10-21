package org.medianik.lendyou.model.person

import java.util.*

@JvmInline
value class PersonId(val id: Long)

open class Person(
    val id: PersonId,
    val name: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return if (other is Person) id == other.id else false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Person[" +
                "id=" + id + ", " +
                "name=" + name + ']'
    }
}