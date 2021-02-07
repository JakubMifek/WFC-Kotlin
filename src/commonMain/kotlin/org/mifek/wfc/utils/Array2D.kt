package org.mifek.wfc.utils

/**
 * Finds a specified column in given array
 */
fun getColumn(array: IntArray, column: Int, size: Int): IntArray {
    return IntArray(size) { array[it * size + column] }
}

/**
 * Finds a specified row in given array
 */
fun getRow(array: IntArray, row: Int, size: Int): IntArray {
    return IntArray(size) { array[it + row * size] }
}

/**
 * Finds specified rows in given array
 */
fun getRows(array: IntArray, rows: IntRange, size: Int): Iterable<IntArray> {
    return Iterable {
        iterator {
            for (row in rows) {
                yield(getRow(array, row, size))
            }
        }
    }
}

/**
 * Finds specified columns in given array
 */
fun getColumns(array: IntArray, columns: IntRange, size: Int): Iterable<IntArray> {
    return Iterable {
        iterator {
            for (row in columns) {
                yield(getColumn(array, row, size))
            }
        }
    }
}

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
 * Rotation of a pattern
 */
fun rotate(pattern: IntArray, overlap: Int): IntArray {
    val res = IntArray(pattern.size)
    var tmp = 1
    for (index in pattern.indices) {
        if (index % (overlap + 1) == 0) {
            tmp -= 1
        }
        tmp += overlap
        res[(tmp + index) % pattern.size] = pattern[index]
    }
    return res
}

/**
 * Rotation of a pattern
 */
fun IntArray.rotate2D(overlap: Int): IntArray {
    return rotate(this, overlap)
}

/**
 * Horizontal flip of a pattern
 */
fun hFlip(pattern: IntArray, overlap: Int): IntArray {
    val size = overlap + 1
    val res = IntArray(pattern.size)
    var tmp = size - 1
    for (index in pattern.indices) {
        res[index + tmp] = pattern[index]
        tmp -= 2
        if (tmp < -size) {
            tmp = size - 1
        }
    }
    return res
}

/**
 * Horizontal flip of a pattern
 */
fun IntArray.hFlip2D(overlap: Int): IntArray {
    return hFlip(this, overlap)
}

/**
 * Vertical flip of a pattern
 */
fun vFlip(pattern: IntArray, overlap: Int): IntArray {
    val size = overlap + 1
    val res = IntArray(pattern.size)
    val T = size.shl(1)
    var tmp = size * (size + 1)
    for (index in pattern.indices) {
        if (index % size == 0) {
            tmp -= T
        }
        res[index + tmp] = pattern[index]
    }
    return res
}

/**
 * Vertical flip of a pattern
 */
fun IntArray.vFlip2D(overlap: Int): IntArray {
    return vFlip(this, overlap)
}