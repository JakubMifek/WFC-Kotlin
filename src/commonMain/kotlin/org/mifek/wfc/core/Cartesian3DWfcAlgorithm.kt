package org.mifek.wfc.core

import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.heuristics.LowestEntropyHeuristic
import org.mifek.wfc.models.Patterns
import org.mifek.wfc.models.Pixels
import org.mifek.wfc.topologies.Cartesian3DTopology

open class Cartesian3DWfcAlgorithm(
    private val topology3D: Cartesian3DTopology,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>,
    private val patterns: Patterns,
) : WfcAlgorithm(
    topology3D,
    weights,
    propagator,
    LowestEntropyHeuristic(patterns.size, topology3D.totalSize, weights)
) {
    /**
     * Constructs output from a wave for overlapping model, returns averages when multiple patterns available
     */
    open fun constructOutput(): IntArray3D {
        return IntArray3D(topology3D.width, topology3D.height, topology3D.depth) { waveIndex ->
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