package org.medianik.lendyou

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import kotlin.reflect.KClass

class Filters(
    vararg filters: Filter<*>
) {
    val filters: List<Filter<*>> = filters.toList()

    @Composable
    fun <K> getFilterValue(name: String): K {
        for (filter in filters) {
            if (filter.name == name) {
                return filter.value() as K
            }
        }
        throw IllegalArgumentException("Invalid type")
    }
}

abstract class Filter<T : Any>(val name: String, val klass: KClass<T>) {
    @Composable
    abstract fun value(): T
}

class DoubleFilter(name: String) : Filter<Double>(name, Double::class) {
    private lateinit var _value: MutableState<Double>

    @Composable
    override fun value(): Double = _value.value

}