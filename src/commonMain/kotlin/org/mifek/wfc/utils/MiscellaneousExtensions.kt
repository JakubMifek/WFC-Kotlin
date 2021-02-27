package org.mifek.wfc

import org.mifek.wfc.datastructures.Quadruple
import kotlin.math.min
import kotlin.random.Random

/**
 * Product of int array elements
 */
fun IntArray.product(): Int {
    return this.reduce { acc, it -> acc * it }
}

/**
 * Return index of random element based on given distribution
 */
fun DoubleArray.randomIndex(random: Random = Random.Default): Int {
    val sum = sum()
    val rand = random.nextDouble() * sum
    var partialSum = 0.0

    for (i in 0 until size) {
        partialSum += this[i]
        if (partialSum > rand) {
            return i
        }
    }

    return size - 1
}

/**
 * Converts this quadruple into a list.
 */
fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)

operator fun Quadruple<UByte, UByte, UByte, UByte>.plus(other: Quadruple<UByte, UByte, UByte, UByte>): Quadruple<UByte, UByte, UByte, UByte> {
    return Quadruple(
        min(first + other.first, 255u).toUByte(),
        min(second + other.second, 255u).toUByte(),
        min(third + other.third, 255u).toUByte(),
        min(fourth + other.fourth, 255u).toUByte()
    )
}

operator fun Quadruple<UByte, UByte, UByte, UByte>.div(value: Int): Quadruple<UByte, UByte, UByte, UByte> {
    return Quadruple(
        ((first + value.toUInt() - 1u) / value.toUInt()).toUByte(),
        ((second + value.toUInt() - 1u) / value.toUInt()).toUByte(),
        ((third + value.toUInt() - 1u) / value.toUInt()).toUByte(),
        ((fourth + value.toUInt() - 1u) / value.toUInt()).toUByte()
    )
}

/**
 * Links up the sequence of arrays into one big array
 */
fun Sequence<IntArray>.chain(number: Int = 1, size: Int = 0): IntArray {
    return this.fold(ArrayList<Int>(number * size), { acc, curr ->
        acc.addAll(curr.asIterable())
        acc
    }).toIntArray()
}