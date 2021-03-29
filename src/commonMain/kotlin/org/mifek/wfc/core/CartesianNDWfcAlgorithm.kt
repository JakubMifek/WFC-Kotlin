package org.mifek.wfc.core

import org.mifek.wfc.datastructures.IntArrayND
import org.mifek.wfc.heuristics.LowestEntropyHeuristic
import org.mifek.wfc.models.Patterns
import org.mifek.wfc.models.Pixels
import org.mifek.wfc.topologies.CartesianNDTopology

open class CartesianNDWfcAlgorithm(
    private val topologyND: CartesianNDTopology,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>,
    private val patterns: Patterns,
    private val pixels: Pixels,
) : WfcAlgorithm(
    topologyND,
    weights,
    propagator,
    LowestEntropyHeuristic(patterns.size, topologyND.totalSize, weights)
) {
    /**
     * Constructs output from a wave for overlapping model, returns averages when multiple patterns available
     */
    open fun constructOutput(): IntArrayND {
        return IntArrayND(topologyND.sizes) { waveIndex ->
            val a = 0
            val b = 1
            val sum = waves[waveIndex].sumOf {
                when (it) {
                    false -> a
                    true -> b
                }
            }
            when (sum) {
                0 -> -123456789
                1 -> patterns.pixels[patterns.pixels.indices.filter { waves[waveIndex, it] }[0]]
                else -> {
                    patterns.indices
                        .filter { waves[waveIndex, it] }
                        .map { patterns.pixels[it] }
                        .sum() / sum
                }
            }
        }
    }
}