package org.mifek.wfc.core

import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.Quadruple
import org.mifek.wfc.models.Patterns
import org.mifek.wfc.models.Pixels
import org.mifek.wfc.topologies.Cartesian2DTopology
import org.mifek.wfc.utils.intToRgba
import org.mifek.wfc.utils.rgbaToInt
import kotlin.math.min
import kotlin.math.roundToInt

class Cartesian2DWfcAlgorithm(
    val width: Int,
    val height: Int,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>,
    private val patterns: Patterns,
    private val pixels: Pixels
) : WfcAlgorithm(
    Cartesian2DTopology(width, height),
    weights,
    propagator
) {
    fun setPixel(waveIndex: Int, pixel: Int) {
        (0..patterns.size).minus(pixels[pixel]).forEach {
            this.ban(waveIndex, it)
        }
    }

    fun setPixel(x: Int, y: Int, pixel: Int) {
        this.setPixel(x + y * width, pixel)
    }

    fun setPixels(data: Array<Int?>) {
        if (data.size !== topology.totalSize) {
            throw ArrayIndexOutOfBoundsException("Wrong size of input array. Expected ${topology.totalSize} but got ${data.size}")
        }

        data.forEachIndexed { index, i ->
            if (i !== null) {
                this.setPixel(index, i)
            }
        }
    }

    fun setPixels(data: Array<Array<Int?>>) {
        if (data.size !== height) {
            throw ArrayIndexOutOfBoundsException("Wrong size of input array. Expected $height but got ${data.size}")
        }

        data.forEachIndexed { rowIndex, row ->
            if (row.size !== width) {
                throw ArrayIndexOutOfBoundsException("Wrong size of inner input array $rowIndex. Expected $width but got ${row.size}")
            }

            row.forEachIndexed { index, i ->
                if (i !== null) {
                    this.setPixel(index + rowIndex * width, i)
                }
            }
        }
    }

    /**
     * Constructs output from a wave for overlapping model
     */
    fun constructOutput(): IntArray2D {
        return IntArray2D(width, height) { waveIndex ->
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
                1 -> patterns[patterns.indices.filter { waves[waveIndex, it] }[0]]
                else -> -2
//                else -> {
//                    val tmp = patterns.indices
//                        .filter { waves[waveIndex, it] }
//                        .map { intToRgba(patterns[it]) }
//                        .fold(
//                            doubleArrayOf(0.0, 0.0, 0.0, 0.0),
//                            { acc, it ->
//                                acc[0] += it.first.toDouble() / sum
//                                acc[1] += it.second.toDouble() / sum
//                                acc[2] += it.third.toDouble() / sum
//                                acc[3] += it.fourth.toDouble() / sum
//                                acc
//                            }
//                        )
//                    rgbaToInt(
//                        Quadruple(
//                            min(tmp[0].roundToInt(), 255).toUByte(),
//                            min(tmp[1].roundToInt(), 255).toUByte(),
//                            min(tmp[2].roundToInt(), 255).toUByte(),
//                            min(tmp[3].roundToInt(), 255).toUByte()
//                        )
//                    )
//                }
            }
        }
    }
}