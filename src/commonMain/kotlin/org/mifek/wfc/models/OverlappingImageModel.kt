package org.mifek.wfc.models

import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.Quadruple
import org.mifek.wfc.models.options.Cartesian2DModelOptions
import org.mifek.wfc.utils.intToRgba
import org.mifek.wfc.utils.rgbaToInt
import kotlin.math.min
import kotlin.math.roundToInt

open class OverlappingImageModel(
    input: IntArray2D,
    overlap: Int,
    outputWidth: Int,
    outputHeight: Int,
    options: Cartesian2DModelOptions = Cartesian2DModelOptions(),
) : OverlappingCartesian2DModel(input, overlap, outputWidth, outputHeight, options) {
    @ExperimentalUnsignedTypes
    override fun constructOutput(algorithm: Cartesian2DWfcAlgorithm): IntArray2D {
        return IntArray2D(outputWidth, outputHeight) { waveIndex ->
            var index = waveIndex
            var shiftX = 0
            var shiftY = 0

            if (!options.periodicOutput) {
                if (onBoundary(waveIndex)) {
                    val x = waveIndex % outputWidth
                    val y = waveIndex / outputWidth

                    if (x >= outputWidth - overlap) {
                        shiftX = x - outputWidth + overlap + 1
                        index -= shiftX
                    }
                    if (y >= outputHeight - overlap) {
                        shiftY = y - outputHeight + overlap + 1
                        index -= shiftY * outputWidth
                    }
                }

                index -= (index / outputWidth) * overlap
            }
            val shift = shiftY * (overlap + 1) + shiftX

            val a = 0
            val b = 1
            val sum = algorithm.waves[index].sumOf {
                when (it) {
                    false -> a
                    true -> b
                }
            }
            when (sum) {
                0 -> -123456789
                1 -> patternsArray[patterns.indices.filter { algorithm.waves[index, it] }[0]][shift]
                else -> {
                    val tmp = patterns.indices
                        .filter { algorithm.waves[index, it] }
                        .map { intToRgba(patternsArray[it][shift]) }
                        .fold(
                            doubleArrayOf(0.0, 0.0, 0.0, 0.0),
                            { acc, it ->
                                acc[0] += it.first.toDouble() / sum
                                acc[1] += it.second.toDouble() / sum
                                acc[2] += it.third.toDouble() / sum
                                acc[3] += it.fourth.toDouble() / sum
                                acc
                            }
                        )
                    rgbaToInt(
                        Quadruple(
                            min(tmp[0].roundToInt(), 255).toUByte(),
                            min(tmp[1].roundToInt(), 255).toUByte(),
                            min(tmp[2].roundToInt(), 255).toUByte(),
                            min(tmp[3].roundToInt(), 255).toUByte()
                        )
                    )
                }
            }
        }
    }
}