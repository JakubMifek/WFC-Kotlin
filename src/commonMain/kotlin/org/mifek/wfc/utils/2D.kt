package org.mifek.wfc.utils

fun getColumn(array: IntArray, column: Int, size: Int): IntArray {
    return IntArray(size) { array[it * size + column] }
}

fun getRow(array: IntArray, row: Int, size: Int): IntArray {
    return IntArray(size) { array[it + row * size] }
}

fun getRows(array: IntArray, rows: IntRange, size: Int): Iterable<IntArray> {
    return Iterable {
        iterator {
            for (row in rows) {
                yield(getRow(array, row, size))
            }
        }
    }
}

fun getColumns(array: IntArray, columns: IntRange, size: Int): Iterable<IntArray> {
    return Iterable {
        iterator {
            for (row in columns) {
                yield(getColumn(array, row, size))
            }
        }
    }
}

fun IntArray.column(column: Int, size: Int): IntArray {
    return getColumn(this, column, size)
}

fun IntArray.columns(columns: IntRange, size: Int): Iterable<IntArray> {
    return getColumns(this, columns, size)
}

fun IntArray.row(row: Int, size: Int): IntArray {
    return getRow(this, row, size)
}

fun IntArray.rows(rows: IntRange, size: Int): Iterable<IntArray> {
    return getRows(this, rows, size)
}

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

fun IntArray.rotate2D(overlap: Int): IntArray {
    return rotate(this, overlap)
}

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

fun IntArray.hFlip2D(overlap: Int): IntArray {
    return hFlip(this, overlap)
}

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

fun IntArray.vFlip2D(overlap: Int): IntArray {
    return vFlip(this, overlap)
}