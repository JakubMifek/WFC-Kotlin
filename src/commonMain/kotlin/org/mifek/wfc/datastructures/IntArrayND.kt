package org.mifek.wfc.datastructures

import org.mifek.wfc.utils.product
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex

class IntArrayND(val sizes: IntArray, init: (Int) -> Int = { 0 }) : Iterable<Int> {
    val data = IntArray(sizes.product(), init)
    val size = data.size
    val lastIndex = data.lastIndex
    val indices = data.indices

    override inline fun iterator(): IntIterator {
        return data.iterator()
    }

    inline operator fun get(index: Int): Int {
        return data[index]
    }

    inline operator fun set(index: Int, value: Int) {
        data[index] = value
    }

    inline operator fun get(coords: IntArray): Int {
        return data[coords.toIndex(sizes)]
    }

    inline operator fun set(coords: IntArray, value: Int) {
        data[coords.toIndex(sizes)] = value
    }

    inline fun contentHashCode(): Int {
        return data.contentHashCode()
    }

    inline fun contentEquals(other: IntArrayND): Boolean {
        return data.contentEquals(other.data)
    }

    fun asIntArray(): IntArray {
        return data
    }

    fun toIntArray(): IntArray {
        return IntArray(size) { data[it] }
    }

    fun copyOf(): IntArrayND {
        return IntArrayND(sizes) { data[it] }
    }

    fun slice(startIndex: Int, ranges: Sequence<IntRange?>): IntArrayND {
        val ranges2 = ranges.mapIndexed { index, it -> (it ?: 0 until sizes[index]).iterator().asSequence().toList() }
        val rangeSizes = ranges2.map { it.size }.toList().toIntArray()
        val initCoords = startIndex.toCoordinates(sizes)

        return IntArrayND(rangeSizes) {
            val coords = it.toCoordinates(rangeSizes)
            coords.indices.map { i -> (coords[i] + initCoords[i]) % sizes[i] }.toIntArray().toIndex(sizes)
        }
    }

    /**
     * Rotated by 90°
     *
     * Returns a new array
     */
    fun rotated(axis: Int, positive: Boolean = true): IntArrayND {
        // TODO: rotations
        throw Error()
    }

    /**
     * Returns a new array
     */
    fun flipped(axis: Int): IntArrayND {
        // TODO: Flips
        throw Error()
    }

    fun clone(): IntArrayND {
        return IntArrayND(sizes) { data[it] }
    }

    /**
     * Up-scaled
     *
     * Returns a new array
     */
    fun upScaled(scale: Int): IntArrayND {
        if (scale < 1) {
            throw NumberFormatException("Scale must be >= 1")
        }
        if (scale == 1) return clone()

        val scaledSizes = sizes.map { it * scale }.toIntArray()
        return IntArrayND(scaledSizes) {
            this[it.toCoordinates(scaledSizes).map { c -> c / scale }.toIntArray().toIndex(sizes)]
        }
    }
}