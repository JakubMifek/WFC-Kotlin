package org.mifek.wfc.datastructures

/**
 * Int array2d
 *
 * @property width
 * @property height
 * @constructor
 *
 * @param init
 */
class IntArray2D(val width: Int, val height: Int, init: (Int) -> Int = { 0 }) : Iterable<Int> {
    val data = IntArray(width * height, init)
    val size = data.size
    val lastIndex = data.lastIndex
    val indices = data.indices

    override fun iterator(): IntIterator {
        return data.iterator()
    }

    /**
     * Get
     *
     * @param index
     * @return
     */
    operator fun get(index: Int): Int {
        return data[index]
    }

    /**
     * Set
     *
     * @param index
     * @param value
     */
    operator fun set(index: Int, value: Int) {
        data[index] = value
    }

    /**
     * Get
     *
     * @param x
     * @param y
     * @return
     */
    operator fun get(x: Int, y: Int): Int {
        return data[y * width + x]
    }

    /**
     * Set
     *
     * @param x
     * @param y
     * @param value
     */
    operator fun set(x: Int, y: Int, value: Int) {
        data[y * width + x] = value
    }

    /**
     * Content hash code
     *
     * @return
     */
    fun contentHashCode(): Int {
        return data.contentHashCode()
    }

    /**
     * Content equals
     *
     * @param other
     * @return
     */
    fun contentEquals(other: IntArray2D): Boolean {
        return data.contentEquals(other.data)
    }

    /**
     * As int array
     *
     * @return
     */
    fun asIntArray(): IntArray {
        return data
    }

    /**
     * To int array
     *
     * @return
     */
    fun toIntArray(): IntArray {
        return IntArray(size) { data[it] }
    }

    /**
     * Copy of
     *
     * @return
     */
    fun copyOf(): IntArray2D {
        return IntArray2D(width, height) { data[it] }
    }

    /**
     * Slice
     *
     * @param startIndex
     * @param xRange
     * @param yRange
     * @return
     */
    fun slice(startIndex: Int, xRange: IntRange?, yRange: IntRange?): IntArray2D {
        val xRange2 = (xRange ?: 0 until width).iterator().asSequence().toList()
        val yRange2 = (yRange ?: 0 until height).iterator().asSequence().toList()

        val slice = IntArray2D(xRange2.size, yRange2.size)
        var sliceIndex = 0
        for (y in yRange2) {
            val postIndex = (startIndex + y * width) % size
            for (x in xRange2) {
                val height = postIndex / width
                val position = ((postIndex % width) + x) % width
                slice[sliceIndex] = data[height * width + position]
                sliceIndex++
            }
        }

        return slice
    }

    /**
     * Column
     *
     * @param column
     * @return
     */
    fun column(column: Int): IntArray {
        return IntArray(height) { this[it * width + column] }
    }

    /**
     * Row
     *
     * @param row
     * @return
     */
    fun row(row: Int): IntArray {
        return IntArray(width) { this[it + row * width] }
    }

    /**
     * Rows
     *
     * @param rows
     * @return
     */
    fun rows(rows: IntRange): Iterable<IntArray> {
        return Iterable {
            iterator {
                for (row in rows) {
                    yield(row(row))
                }
            }
        }
    }

    /**
     * Columns
     *
     * @param columns
     * @return
     */
    fun columns(columns: IntRange): Iterable<IntArray> {
        return Iterable {
            iterator {
                for (row in columns) {
                    yield(column(row))
                }
            }
        }
    }

    /**
     * Rotated
     *
     * @param positive
     * @return
     */
    fun rotated(positive: Boolean = true): IntArray2D {
        val res = IntArray2D(height, width)
        for (y in 0 until res.height) {
            for (x in 0 until res.width) {
                res[x, y] = this[if (positive) y else height - 1 - y, if (positive) width - 1 - x else x]
            }
        }
        return res
    }

    /**
     * H flipped
     *
     * @return
     */
    fun hFlipped(): IntArray2D {
        val res = IntArray2D(width, height)
        for (y in 0 until res.height) {
            for (x in 0 until res.width) {
                res[x, y] = this[width - x - 1, y]
            }
        }
        return res
    }

    /**
     * V flipped
     *
     * @return
     */
    fun vFlipped(): IntArray2D {
        val res = IntArray2D(width, height)
        for (y in 0 until res.height) {
            for (x in 0 until res.width) {
                res[x, y] = this[x, height - y - 1]
            }
        }
        return res
    }

    /**
     * Clone
     *
     * @return
     */
    fun clone(): IntArray2D {
        return IntArray2D(width, height) { data[it] }
    }

    /**
     * Up scaled
     *
     * @param scale
     * @return
     */
    fun upScaled(scale: Int): IntArray2D {
        if (scale < 1) {
            throw NumberFormatException("Scale must be >= 1")
        }
        if (scale == 1) return clone()

        val scaledWidth = width * scale
        val scaledHeight = height * scale
        return IntArray2D(scaledWidth, scaledHeight) {
            val x = it % scaledWidth
            val y = it / scaledWidth
            val oriX = x / scale
            val oriY = y / scale
            this[oriY * width + oriX]
        }
    }
}