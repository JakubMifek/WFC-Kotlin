package org.mifek.wfc.datastructures

import org.mifek.wfc.utils.product
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex

class IntArrayND(val sizes: IntArray, init: (Int) -> Int = { 0 }) : Iterable<Int> {
    val data = IntArray(sizes.product(), init)
    val size = data.size
    val lastIndex = data.lastIndex
    val indices = data.indices

    override fun iterator(): IntIterator {
        return data.iterator()
    }

    operator fun get(index: Int): Int {
        return data[index]
    }

    operator fun set(index: Int, value: Int) {
        data[index] = value
    }

    operator fun get(coords: IntArray): Int {
        return data[coords.toIndex(sizes)]
    }

    operator fun set(coords: IntArray, value: Int) {
        data[coords.toIndex(sizes)] = value
    }

    fun contentHashCode(): Int {
        return data.contentHashCode()
    }

    fun contentEquals(other: IntArrayND): Boolean {
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
    fun rotated(plane: Pair<Int, Int>, positive: Boolean = true): IntArrayND {
        if(plane.first == plane.second) {
            throw Exception("Rotation plane consists of two different axis.")
        }

        val axis1 = if(plane.first > plane.second) plane.second else plane.first
        val axis2 = if(plane.first > plane.second) plane.first else plane.second

        val newSizes = IntArray(sizes.size) { sizes[it] }
        val tmpS = newSizes[axis1]
        newSizes[axis1] = newSizes[axis2]
        newSizes[axis2] = tmpS

        return IntArrayND(newSizes) {
            val coords = it.toCoordinates(newSizes)

            // Rotate
            val tmp = coords[axis1]
            coords[axis1] = if (!positive) coords[axis2] else sizes[axis1] - 1 - coords[axis2]
            coords[axis2] = if (!positive) sizes[axis2] - 1 - tmp else tmp

            get(coords)
        }
    }

    /**
     * Returns a new array
     */
    fun flipped(axis: Int): IntArrayND {
        return IntArrayND(sizes) {
            val coordinates = it.toCoordinates(sizes)
            coordinates[axis] = sizes[axis] - 1 - coordinates[axis]
            this[coordinates]
        }
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