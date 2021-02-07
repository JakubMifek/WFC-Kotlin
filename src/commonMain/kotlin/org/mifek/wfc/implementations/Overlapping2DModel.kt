package org.mifek.wfc.implementations

import org.mifek.wfc.datatypes.Directions2D
import org.mifek.wfc.interfaces.Model
import org.mifek.wfc.utils.agrees2D
import org.mifek.wfc.utils.loadOverlappingPatternsFromImage

class Overlapping2DModel(
    input: IntArray,
    width: Int,
    private val overlap: Int,
    private val outputWidth: Int,
    private val outputHeight: Int
) :
    Model<Core2D> {
    private val patternCounts = loadOverlappingPatternsFromImage(input, width, overlap)
    private val sum = patternCounts.map { it.second.item }.sum()
    private val patternSideSize = overlap + 1

    override val patterns = patternCounts.map { it.first }.toTypedArray()
    override val weights = DoubleArray(patternCounts.size) { patternCounts[it].second.item / sum.toDouble() }
    override val propagator = Array(4) { dir ->
        Array(patterns.size) { patternIndex ->
            val d = Directions2D.fromInt(dir)
            patterns.indices.filter {
                agrees2D(
                    patterns[patternIndex],
                    patterns[it],
                    patternSideSize,
                    d,
                    overlap
                )
            }.toIntArray()
        }
    }

    override fun build(): Core2D {
        return Core2D(outputWidth, outputHeight, weights, propagator)
    }
}