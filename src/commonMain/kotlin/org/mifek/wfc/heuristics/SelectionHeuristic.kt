package org.mifek.wfc.heuristics

import org.mifek.wfc.core.WfcAlgorithm
import kotlin.random.Random

interface SelectionHeuristic {
    /**
     * Runs on wfc startup - can be called multiple times!
     */
    fun initialize(algorithm: WfcAlgorithm, random: Random = Random.Default)
    fun select(): Int?
}