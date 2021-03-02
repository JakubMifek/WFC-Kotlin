package org.mifek.wfc.datastructures

class IntArray2D(val width: Int, val height: Int, init: (Int) -> Int = { 0 }) : Iterable<Int> {
    val data = IntArray(width * height, init)
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

    inline operator fun get(x: Int, y: Int): Int {
        return data[y * width + x]
    }

    inline operator fun set(x: Int, y: Int, value: Int) {
        data[y * width + x] = value
    }

    inline fun contentHashCode(): Int {
        return data.contentHashCode()
    }

    inline fun contentEquals(other: IntArray2D): Boolean {
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
    fun rotated(): IntArray2D {
        val res = IntArray2D(height, width)
        for (y in 0 until res.height) {
            for (x in 0 until res.width) {
                res[x, y] = this[y, height - x - 1]
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