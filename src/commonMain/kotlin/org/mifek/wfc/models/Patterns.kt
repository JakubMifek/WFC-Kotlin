package org.mifek.wfc.models

import org.mifek.wfc.datastructures.IntArray2D

class Patterns(private val patterns: Array<IntArray2D>) : Iterable<IntArray2D> {
    val size = patterns.size
    val indices = patterns.indices
    val lastIndex = patterns.lastIndex

    val pixels = patterns.map { it[0] }

    operator fun get(index: Int): IntArray2D {
        return patterns[index].copyOf()
    }

    override fun iterator(): Iterator<IntArray2D> {
        return patterns.iterator()
    }
}