package org.mifek.wfc.models

import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.core.Cartesian3DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.datastructures.PatternsArrayBuilder
import org.mifek.wfc.datatypes.Direction3D
import org.mifek.wfc.models.options.Cartesian3DModelOptions
import org.mifek.wfc.topologies.Cartesian3DTopology

open class OverlappingCartesian3DModel(
    val input: IntArray3D,
    val overlap: Int,
    val outputWidth: Int,
    val outputHeight: Int,
    val outputDepth: Int,
    val options: Cartesian3DModelOptions = Cartesian3DModelOptions(),
) : OverlappingModel {
    private val patternSideSize = overlap + 1

    private val patternCounts = loadPatterns(
        input,
        overlap,
    )

    private val sum = patternCounts.map { it.second.item }.sum()

    protected val patternsArray = patternCounts.map { it.first }.toTypedArray()
    override val patterns = Patterns(patternsArray.map { it.asIntArray() }.toTypedArray())
    override val pixels = Pixels(
        mapOf(
            *patterns.pixels.map { it }.distinct().map { pixel ->
                Pair(
                    pixel,
                    patterns.pixels
                        .mapIndexed { index, it -> Pair(it, index) }
                        .filter { it.first == pixel }
                        .map { it.second }
                        .toIntArray()
                )
            }.toTypedArray()
        )
    )

    private val weights = DoubleArray(patternCounts.size) { patternCounts[it].second.item / sum.toDouble() }
    private val propagator = Array(6) { dir ->
        Array(patternsArray.size) { patternIndex ->
            val d = Direction3D.fromInt(dir)
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

    override fun build(): Cartesian3DWfcAlgorithm {
        val topology = Cartesian3DTopology(
            outputWidth - if (options.periodicOutput) 0 else overlap, // We can compute smaller array, missing pixels are fixed by boundary patterns
            outputHeight - if (options.periodicOutput) 0 else overlap, // But if output is periodic, this is not desirable because it would scale down the output
            outputDepth - if (options.periodicOutput) 0 else overlap,
            options.periodicOutput
        )
        val algorithm = Cartesian3DWfcAlgorithm(
            topology, weights, propagator, patterns, pixels
        )
        return algorithm
    }


    /**
     * Checks whether two overlapping patterns agree
     * @param size Size of single side of the pattern (square patterns expected)
     */
    private fun agrees(
        pattern1: IntArray3D,
        pattern2: IntArray3D,
        size: Int,
        direction: Direction3D,
        overlap: Int
    ): Boolean {
        val sequence1 = when (direction) {
            Direction3D.UP -> pattern1[null, 0 until overlap, null]
            Direction3D.DOWN -> pattern1[null, size - overlap until size, null]
            Direction3D.FORWARD -> pattern1[null, null, size - overlap until size]
            Direction3D.BACKWARD -> pattern1[null, null, 0 until overlap]
            Direction3D.LEFT -> pattern1[null, null, 0 until overlap]
            Direction3D.RIGHT -> pattern1[null, null, size - overlap until size]
        }
        val sequence2 = when (direction) {
            Direction3D.UP -> pattern2[null, size - overlap until size, null]
            Direction3D.DOWN -> pattern2[null, 0 until overlap, null]
            Direction3D.FORWARD -> pattern2[null, null, 0 until overlap]
            Direction3D.BACKWARD -> pattern2[null, null, size - overlap until size]
            Direction3D.LEFT -> pattern2[null, null, size - overlap until size]
            Direction3D.RIGHT -> pattern2[null, null, 0 until overlap]
        }
        return sequence1.zip(sequence2)
            .all {
                it.first.zip(it.second)
                    .all { it1 ->
                        it1.first.zip(it1.second)
                            .all { it2 ->
                                it2.first == it2.second
                            }
                    }
            }
    }


    /**
     * Loads patterns and number of their occurrences in the input image
     */
    private fun loadPatterns(
        data: IntArray3D,
        overlap: Int,
    ): List<Pair<IntArray3D, IntHolder>> {
        val pab = PatternsArrayBuilder()

        // Go through input image without borders
        val zMax = (data.depth - if (options.periodicInput) 0 else overlap)
        val yMax = (data.height - if (options.periodicInput) 0 else overlap)
        val xMax = (data.width - if (options.periodicInput) 0 else overlap)

        for (zOffset in 0 until zMax) {
            val prePreIndex = zOffset * data.depth
            for (yOffset in 0 until yMax) {
                val preIndex = (prePreIndex + yOffset) * data.width
                for (xOffset in 0 until xMax) {
                    val index = preIndex + xOffset

                    // Create pattern
                    val pattern = data.slice(index, 0..overlap, 0..overlap, 0..overlap)
                    val foundPatterns = mutableListOf(pattern)

                    if (options.allowFlips) {
                        foundPatterns.addAll(flips(pattern))
                    }

                    if (options.allowRotations) {
                        val rotations = rotations(pattern)
                        foundPatterns.addAll(rotations)

                        if (options.allowFlips) {
                            foundPatterns.addAll(
                                rotations
                                    .map { flips(it) }
                                    .reduce { acc, curr -> acc.plus(curr) }
                            )
                        }
                    }

                    foundPatterns.forEach {
                        pab.add(it.asIntArray())
                    }
                }
            }
        }

        return pab.patterns.map { pair ->
            Pair(
                IntArray3D(patternSideSize, patternSideSize, patternSideSize) { pair.first[it] },
                pair.second
            )
        }
    }

    private fun flips(pattern: IntArray3D): Sequence<IntArray3D> {
        return sequence {
            val patternX = pattern.flippedX()
            yield(patternX)
            val patternY = pattern.flippedY()
            yield(patternY)
            val patternZ = pattern.flippedZ()
            yield(patternZ)
            val patternXY = patternX.flippedY()
            yield(patternXY)
            val patternXZ = patternX.flippedZ()
            yield(patternXZ)
            val patternYZ = patternY.flippedZ()
            yield(patternYZ)
            val patternXYZ = patternXY.flippedZ()
            yield(patternXYZ)
        }
    }

    private fun rotations(pattern: IntArray3D): Sequence<IntArray3D> {
        return sequence {
            val pattern90X = pattern.xRotated()
            yield(pattern90X)
            val pattern180X = pattern90X.xRotated()
            yield(pattern180X)
            val pattern270X = pattern180X.xRotated()
            yield(pattern270X)
            val pattern90Y = pattern.yRotated()
            yield(pattern90Y)
            val pattern180Y = pattern90Y.yRotated()
            yield(pattern180Y)
            val pattern270Y = pattern180Y.yRotated()
            yield(pattern270Y)
            val pattern90Z = pattern.zRotated()
            yield(pattern90Z)
            val pattern180Z = pattern90Z.zRotated()
            yield(pattern180Z)
            val pattern270Z = pattern180Z.zRotated()
            yield(pattern270Z)
        }
    }

    protected fun onBoundary(waveIndex: Int): Boolean {
        if (waveIndex % outputWidth >= outputWidth - overlap) {
            return true
        }
        if (waveIndex >= outputWidth * (outputHeight - overlap)) {
            return true
        }
        return false
    }

    @ExperimentalUnsignedTypes
    open fun constructOutput(algorithm: Cartesian2DWfcAlgorithm): IntArray2D {
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

                index -= index / outputWidth
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
                    patterns.indices
                        .filter { algorithm.waves[index, it] }
                        .map { patternsArray[it][shift] }
                        .sum() / sum
                }
            }
        }
    }
}