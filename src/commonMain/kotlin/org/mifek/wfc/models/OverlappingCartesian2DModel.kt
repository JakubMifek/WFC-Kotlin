package org.mifek.wfc.models

import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.datatypes.Directions2D
import org.mifek.wfc.utils.overlapping.agrees
import org.mifek.wfc.utils.overlapping.loadPatterns

class OverlappingCartesian2DModel(
    input: IntArray,
    inputStride: Int,
    val overlap: Int,
    val outputWidth: Int,
    val outputHeight: Int,
    allowRotations: Boolean = true,
    allowFlips: Boolean = true
) : Model {
    private val patternCounts = loadPatterns(
        input,
        inputStride,
        overlap,
        allowRotations = allowRotations,
        allowFlips = allowFlips
    )

    private val sum = patternCounts.map { it.second.item }.sum()
    private val patternSideSize = overlap + 1

    private val patternsArray = patternCounts.map { it.first }.toTypedArray()
    override val patterns = Patterns(patternsArray.map { it[0] }.toIntArray())

    private val weights = DoubleArray(patternCounts.size) { patternCounts[it].second.item / sum.toDouble() }
    private val propagator = Array(4) { dir ->
        Array(patternsArray.size) { patternIndex ->
            val d = Directions2D.fromInt(dir)
            patternsArray.indices.filter {
                agrees(
                    patternsArray[patternIndex],
                    patternsArray[it],
                    patternSideSize,
                    d,
                    overlap
                )
            }.toIntArray()
        }
    }

    override fun build(): Cartesian2DWfcAlgorithm {
        return Cartesian2DWfcAlgorithm(outputWidth, outputHeight, weights, propagator)
    }
}