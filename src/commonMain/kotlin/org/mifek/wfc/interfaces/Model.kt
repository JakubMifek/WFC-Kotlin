package org.mifek.wfc.interfaces

interface Model<T: Core> {
    /**
     * Patterns from the input
     */
    val patterns: Array<IntArray>

    /**
     * Weights of the patterns from the input
     */
    val weights: DoubleArray

    /**
     * Propagator of the patterns from the input
     */
    val propagator: Array<Array<IntArray>>

    /**
     * Builds the WFC Core
     */
    fun build(): T
}