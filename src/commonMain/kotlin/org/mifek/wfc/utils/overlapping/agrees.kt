package org.mifek.wfc.utils.overlapping

import org.mifek.wfc.columns
import org.mifek.wfc.datatypes.Directions2D
import org.mifek.wfc.rows

/**
 * Checks whether two overlapping patterns agree
 * @param size Size of single side of the pattern (square patterns expected)
 */
fun agrees(pattern1: IntArray, pattern2: IntArray, size: Int, direction: Directions2D, overlap: Int): Boolean {
    val line1 = when (direction) {
        Directions2D.NORTH -> pattern1.rows(0 until overlap, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.EAST -> pattern1.columns((size - overlap) until size, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.SOUTH -> pattern1.rows((size - overlap) until size, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.WEST -> pattern1.columns(0 until overlap, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
    }
    val line2 = when (direction) {
        Directions2D.NORTH -> pattern2.rows((size - overlap) until size, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.EAST -> pattern2.columns(0 until overlap, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.SOUTH -> pattern2.rows(0 until overlap, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.WEST -> pattern2.columns((size - overlap) until size, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
    }
//    println("${line1.joinToString(",")}\t${line2.joinToString(",")}")
    return line1.contentEquals(line2)
}
