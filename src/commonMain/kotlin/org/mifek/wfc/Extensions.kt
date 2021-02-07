package org.mifek.wfc

import org.mifek.wfc.datastructures.Quadruple
import kotlin.random.Random

private val RANDOM = Random(12345)

fun IntArray.product(): Int {
    return this.reduce { acc, it -> acc*it }
}

fun DoubleArray.randomIndex(random: Random = RANDOM): Int {
    val sum = sum()
    val rand = random.nextDouble() * sum
    var partialSum = 0.0
//    println("Sum: $sum\tRand: $rand\tSize: $size")
    for (i in 0 until size) {
        partialSum += this[i]
        if (partialSum > rand) {
//            println("Random: $i")
            return i
        }
    }

//    println("Random: ${size-1}")
    return size-1
}

/**
 * Converts this quadruple into a list.
 */
fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)