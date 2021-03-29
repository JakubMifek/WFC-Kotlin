package org.mifek.wfc.datastructures

class IntArray2D(val width: Int, val height: Int, init: (Int) -> Int = { 0 }) : Iterable<Int> {
    val data = IntArray(width * height, init)
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

    operator fun get(x: Int, y: Int): Int {
        return data[y * width + x]
    }

    operator fun set(x: Int, y: Int, value: Int) {
        data[y * width + x] = value
    }

    fun contentHashCode(): Int {
        return data.contentHashCode()
    }

    fun contentEquals(other: IntArray2D): Boolean {
        return data.contentEquals(other.data)
    }

    fun asIntArray(): IntArray {
        return data
    }

    fun toIntArray(): IntArray {
        return IntArray(size) { data[it] }
    }

    fun copyOf(): IntArray2D {
        return IntArray2D(width, height) { data[it] }
    }

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
     * Returns the specified column
     */
    fun column(column: Int): IntArray {
        return IntArray(height) { this[it * width + column] }
    }

    /**
     * Returns the specified row in given array
     */
    fun row(row: Int): IntArray {
        return IntArray(width) { this[it + row * width] }
    }

    /**
     * Finds specified rows in given array
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
     * Finds specified columns
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
     * Rotated by 90°
     *
     * Returned a new array
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
     * Horizontally flipped
     *
     * Returned a new array
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
     * Vertically flipped
     *
     * Returned a new array
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

    fun clone(): IntArray2D {
        return IntArray2D(width, height) { data[it] }
    }

    /**
     * Up-scaled
     *
     * Returns a new array
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