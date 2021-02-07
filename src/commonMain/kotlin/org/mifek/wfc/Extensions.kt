package org.mifek.wfc

import org.mifek.wfc.datastructures.Quadruple
import org.mifek.wfc.utils.RANDOM
import kotlin.random.Random

/**
 * Product of int array elements
 */
fun IntArray.product(): Int {
    return this.reduce { acc, it -> acc*it }
}

/**
 * Return index of random element based on given distribution
 */
fun DoubleArray.randomIndex(random: Random = RANDOM): Int {
    val sum = sum()
    val rand = random.nextDouble() * sum
    var partialSum = 0.0

    for (i in 0 until size) {
        partialSum += this[i]
        if (partialSum > rand) {
            return i
        }
    }

    return size-1
}

/**
 * Converts this quadruple into a list.
 */
fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)