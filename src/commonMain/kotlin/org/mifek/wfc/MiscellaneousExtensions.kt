package org.mifek.wfc

import org.mifek.wfc.datastructures.Quadruple
import org.mifek.wfc.utils.*
import kotlin.math.max
import kotlin.math.min
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

/**
 * Returns a column
 */
fun IntArray.column(column: Int, size: Int): IntArray {
    return getColumn(this, column, size)
}

/**
 * Returns iterator of selected columns
 */
fun IntArray.columns(columns: IntRange, size: Int): Iterable<IntArray> {
    return getColumns(this, columns, size)
}

/**
 * Returns a row
 */
fun IntArray.row(row: Int, size: Int): IntArray {
    return getRow(this, row, size)
}

/**
 * Returns iterator of selected rows
 */
fun IntArray.rows(rows: IntRange, size: Int): Iterable<IntArray> {
    return getRows(this, rows, size)
}

/**
 * Horizontal flip of a pattern
 */
fun IntArray.hFlip2D(overlap: Int): IntArray {
    return hFlip(this, overlap)
}

/**
 * Rotation of a pattern
 */
fun IntArray.rotate2D(overlap: Int): IntArray {
    return rotate(this, overlap)
}

/**
 * Vertical flip of a pattern
 */
fun IntArray.vFlip2D(overlap: Int): IntArray {
    return vFlip(this, overlap)
}

fun IntArray.scale2D(stride: Int, scale: Int): IntArray {
    if(scale < 1) {
        throw NumberFormatException("Scale must be >= 1")
    }
    if(scale == 1) return clone()
    val scaledStride = stride * scale
    val scaledHeight = size/stride * scale
    return IntArray(scaledStride * scaledHeight) {
        val x = it % scaledStride
        val y = it / scaledStride
        val oriX = x / scale
        val oriY = y / scale
        this[oriY * stride + oriX]
    }
}

operator fun Quadruple<UByte, UByte, UByte, UByte>.plus(other: Quadruple<UByte, UByte, UByte, UByte>): Quadruple<UByte, UByte, UByte, UByte> {
    return Quadruple(min(first + other.first, 255u).toUByte(), min(second + other.second, 255u).toUByte(), min(third + other.third, 255u).toUByte(), min(fourth + other.fourth, 255u).toUByte())
}

operator fun Quadruple<UByte, UByte, UByte, UByte>.div(value: Int): Quadruple<UByte, UByte, UByte, UByte> {
    return Quadruple(((first + value.toUInt() - 1u) / value.toUInt()).toUByte(), ((second + value.toUInt() - 1u)  / value.toUInt()).toUByte(), ((third + value.toUInt() - 1u) / value.toUInt()).toUByte(), ((fourth + value.toUInt() - 1u) / value.toUInt()).toUByte())
}