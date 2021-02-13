package org.mifek.wfc.utils.overlapping

import org.mifek.wfc.core.Waves
import org.mifek.wfc.models.Patterns

/**
 * Constructs output from a wave for overlapping model
 */
fun constructOutput(waves: Waves, patterns: Patterns): IntArray {
    return IntArray(waves.size) { waveIndex ->
        val a = 0
        val b = 1
        val sum = waves[waveIndex].sumOf {
            when (it) {
                false -> a
                true -> b
            }
        }
        when (sum) {
            0 -> -1
            else -> patterns.indices.filter { waves[waveIndex, it] }.map { patterns[it] }.sum() / sum
        }
    }
}