package org.mifek.wfc.models

class Patterns(private val patterns: IntArray): Iterable<Int> {
    val size = patterns.size
    val indices = patterns.indices
    val lastIndex = patterns.lastIndex

    operator fun get(index: Int): Int {
        return patterns[index]
    }

    override fun iterator(): Iterator<Int> {
        return patterns.iterator()
    }
}