package org.mifek.wfc.heuristics

import org.mifek.wfc.core.WfcAlgorithm
import kotlin.random.Random

/**
 * Selection heuristic
 *
 * @constructor Create empty Selection heuristic
 */
interface SelectionHeuristic {
    /**
     * Initialize
     *
     * @param algorithm
     * @param random
     */
    fun initialize(algorithm: WfcAlgorithm, random: Random = Random.Default)

    /**
     * Select
     *
     * @return
     */
    fun select(): Int?
}