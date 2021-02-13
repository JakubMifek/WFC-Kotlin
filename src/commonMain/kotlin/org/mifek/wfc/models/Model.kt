package org.mifek.wfc.models

import org.mifek.wfc.core.WfcAlgorithm

interface Model {
    /**
     * Patterns from the input
     */
    val patterns: Patterns

//    /**
//     * Weights of the patterns from the input
//     */
//    val weights: DoubleArray
//
//    /**
//     * Propagator of the patterns from the input
//     */
//    val propagator: Array<Array<IntArray>>

    /**
     * Builds the WFC Core
     */
    fun build(): WfcAlgorithm
}