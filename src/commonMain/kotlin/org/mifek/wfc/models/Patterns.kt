package org.mifek.wfc.models

/**
 * Patterns
 *
 * @property patterns
 * @constructor Create empty Patterns
 */
class Patterns(private val patterns: Array<IntArray>) : Iterable<IntArray> {
    val size = patterns.size
    val indices = patterns.indices
    val lastIndex = patterns.lastIndex

    val pixels = patterns.map { it[0] }

    /**
     * Get
     *
     * @param index
     * @return
     */
    operator fun get(index: Int): IntArray {
        return patterns[index].copyOf()
    }

    override fun iterator(): Iterator<IntArray> {
        return patterns.iterator()
    }
}