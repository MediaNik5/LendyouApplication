package org.medianik.lendyou.ui.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.min

data class IntIntPair(val int1: Int, val int2: Int)

private const val integerIndex = 0
private const val fractionIndex = 1

object TransformSum : VisualTransformation {
    // 234 456 234.12
    override fun filter(text: AnnotatedString): TransformedText {
        val integerAndFraction = text.text.split('.', ignoreCase = true, limit = 2)
        val (integer, mapping) = parseInteger(integerAndFraction[integerIndex])
        if (integerAndFraction.size == 2) {
            return TransformedText(
                AnnotatedString("$integer.${integerAndFraction[fractionIndex]}"),
                mapping
            )
        }
        return TransformedText(AnnotatedString(integer), mapping)
    }

    private fun parseInteger(integer: String): Pair<String, OffsetMapping> {
        if (integer.isEmpty())
            return "" to OffsetMapping.Identity
        val sections = integer.splitToLength(sequenceLength = 3)
        val transformedText = StringBuilder(3 * sections.size + sections.size - 1)
        val mappingOriginalToTransformedText = ArrayList<IntIntPair>(integer.length)
        for (index in 0 until (sections.size - 1)) {
            transformedText.append(sections[index])
            transformedText.append(' ')
            mappingOriginalToTransformedText.addNextMapping(sections[index].length)
        }
        transformedText.append(sections.last())
        mappingOriginalToTransformedText.addNextMapping(sections.last().length, false)
        return transformedText.toString() to OffsetMappingOfPairs(mappingOriginalToTransformedText)
    }
}

private class OffsetMappingOfPairs(private val mappingOriginalToTransformedText: List<IntIntPair>) :
    OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        if (offset == 0)
            return 0
        return mappingOriginalToTransformedText.firstOrNull { it.int1 == offset }?.int2
            ?: (offset + mappingOriginalToTransformedText.last().run { int2 - int1 })
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset == 0)
            return 0
        return mappingOriginalToTransformedText.firstOrNull { it.int2 == offset }?.int1
            ?: (offset - mappingOriginalToTransformedText.last().run { int2 - int1 })
    }
}

/**
 * [(0, 0), (1, 1), (2, 1), ]
 */
private fun MutableList<IntIntPair>.addNextMapping(length: Int, addLast: Boolean = true) {
    var lastPair = lastOrNull() ?: IntIntPair(0, 0)
    for (index in 1..length) {
        add(IntIntPair(lastPair.int1 + index, lastPair.int2 + index))
    }
    if (addLast)
        add(IntIntPair(lastPair.int1 + length, lastPair.int2 + length + 1))
}

/**
 * Given with 12345678
 * Outputs [12, 345, 678]
 */
private fun String.splitToLength(sequenceLength: Int): Array<StringBuilder> {
    return Array((length + sequenceLength - 1) / sequenceLength) { index ->
        val sb = StringBuilder(sequenceLength)
        var indexOfChar = min((index + 1) * sequenceLength, length)
        val nextIndex = index * sequenceLength
        while (indexOfChar > nextIndex)
            sb.append(this[length - 1 - (--indexOfChar)])
        return@Array sb
    }.apply { reverse() }
}
