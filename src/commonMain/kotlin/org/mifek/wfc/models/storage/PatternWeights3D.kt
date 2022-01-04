package org.mifek.wfc.models.storage

import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.datastructures.PatternsArrayBuilder
import org.mifek.wfc.datatypes.Direction3D
import org.mifek.wfc.models.Patterns
import org.mifek.wfc.models.Pixels
import org.mifek.wfc.models.options.Cartesian3DModelOptions
import kotlin.math.pow

/**
 * Pattern weights3d
 *
 * @property input
 * @property overlap
 * @property options
 * @constructor Create empty Pattern weights3d
 */
class PatternWeights3D(
    val input: IntArray3D,
    val overlap: Int,
    val options: Cartesian3DModelOptions = Cartesian3DModelOptions(),
) {
    val patternSideSize = overlap + 1

    private val patternCounts = loadPatterns(
        input,
        overlap,
    )

    val weightSum = patternCounts.sumOf { it.second.item }
    val patternsArray = patternCounts.map { it.first }.toTypedArray()
    val patterns = Patterns(patternsArray.map { it.asIntArray() }.toTypedArray())
    val pixels = Pixels(
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

    val weights = DoubleArray(patternCounts.size) {
        (patternCounts[it].second.item / weightSum.toDouble()).pow(options.weightPower)
    }
    val propagator = Array(6) { dir ->
        Array(patternsArray.size) { patternIndex ->
            val d = Direction3D.fromInt(dir)
            patternsArray.indices.filter {
                agrees(
                    patternsArray[patternIndex],
                    patternsArray[it],
                    d,
                )
            }.toIntArray()
        }
    }

    /**
     * Agrees
     *
     * @param pattern1
     * @param pattern2
     * @param direction
     * @return
     */
    protected fun agrees(
        pattern1: IntArray3D,
        pattern2: IntArray3D,
        direction: Direction3D
    ): Boolean {
        val sequence1 = when (direction) {
            Direction3D.UP -> pattern1[null, 0 until overlap, null]
            Direction3D.DOWN -> pattern1[null, patternSideSize - overlap until patternSideSize, null]
            Direction3D.FORWARD -> pattern1[null, null, patternSideSize - overlap until patternSideSize]
            Direction3D.BACKWARD -> pattern1[null, null, 0 until overlap]
            Direction3D.LEFT -> pattern1[0 until overlap, null, null]
            Direction3D.RIGHT -> pattern1[patternSideSize - overlap until patternSideSize, null, null]
        }
        val sequence2 = when (direction) {
            Direction3D.UP -> pattern2[null, patternSideSize - overlap until patternSideSize, null]
            Direction3D.DOWN -> pattern2[null, 0 until overlap, null]
            Direction3D.FORWARD -> pattern2[null, null, 0 until overlap]
            Direction3D.BACKWARD -> pattern2[null, null, patternSideSize - overlap until patternSideSize]
            Direction3D.LEFT -> pattern2[patternSideSize - overlap until patternSideSize, null, null]
            Direction3D.RIGHT -> pattern2[0 until overlap, null, null]
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
     * Load patterns
     *
     * @param data
     * @param overlap
     * @return
     */
    protected fun loadPatterns(
        data: IntArray3D,
        overlap: Int,
    ): List<Pair<IntArray3D, IntHolder>> {
        val pab = PatternsArrayBuilder()

        // Go through input image without borders
        val zMax = (data.depth - if (options.periodicInput) 0 else overlap)
        val yMax = (data.height - if (options.periodicInput) 0 else overlap)
        val xMax = (data.width - if (options.periodicInput) 0 else overlap)

        for (zOffset in 0 until zMax) {
            val prePreIndex = zOffset * data.height
            for (yOffset in 0 until yMax) {
                val preIndex = (prePreIndex + yOffset) * data.width
                for (xOffset in 0 until xMax) {
                    val index = preIndex + xOffset

                    // Create pattern
                    val pattern = data.slice(index, 0..overlap, 0..overlap, 0..overlap)
                    val foundPatterns = mutableListOf(pattern)

                    if (options.allowXFlips || options.allowYFlips || options.allowZFlips) {
                        foundPatterns.addAll(flips(pattern, options))
                    }

                    if (options.allowXRotations || options.allowYRotations || options.allowZRotations) {
                        val rotations = rotations(pattern, options)
                        foundPatterns.addAll(rotations)

                        if (options.allowXFlips || options.allowYFlips || options.allowZFlips) {
                            foundPatterns.addAll(
                                rotations
                                    .map { flips(it, options) }
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

    /**
     * Flips
     *
     * @param pattern
     * @param options
     * @return
     */
    protected fun flips(pattern: IntArray3D, options: Cartesian3DModelOptions): Sequence<IntArray3D> {
        return sequence {
            if (options.allowXFlips) {
                val patternX = pattern.flippedX()
                yield(patternX)

                if (options.allowYFlips) {
                    val patternXY = patternX.flippedY()
                    yield(patternXY)

                    if (options.allowZFlips) {
                        val patternXYZ = patternXY.flippedZ()
                        yield(patternXYZ)
                    }
                }

                if (options.allowZFlips) {
                    val patternXZ = patternX.flippedZ()
                    yield(patternXZ)
                }
            }
            if (options.allowYFlips) {
                val patternY = pattern.flippedY()
                yield(patternY)

                if (options.allowZFlips) {
                    val patternYZ = patternY.flippedZ()
                    yield(patternYZ)
                }
            }
            if (options.allowZFlips) {
                val patternZ = pattern.flippedZ()
                yield(patternZ)
            }
        }
    }

    /**
     * X rotations
     *
     * @param pattern
     * @return
     */
    protected fun xRotations(pattern: IntArray3D): Sequence<IntArray3D> {
        return sequence {
            val pattern90X = pattern.xRotated()
            yield(pattern90X)
            val pattern180X = pattern90X.xRotated()
            yield(pattern180X)
            val pattern270X = pattern180X.xRotated()
            yield(pattern270X)
        }
    }

    /**
     * Y rotations
     *
     * @param pattern
     * @return
     */
    protected fun yRotations(pattern: IntArray3D): Sequence<IntArray3D> {
        return sequence {
            val pattern90Y = pattern.yRotated()
            yield(pattern90Y)
            val pattern180Y = pattern90Y.yRotated()
            yield(pattern180Y)
            val pattern270Y = pattern180Y.yRotated()
            yield(pattern270Y)
        }
    }

    /**
     * Z rotations
     *
     * @param pattern
     * @return
     */
    protected fun zRotations(pattern: IntArray3D): Sequence<IntArray3D> {
        return sequence {
            val pattern90Z = pattern.zRotated()
            yield(pattern90Z)
            val pattern180Z = pattern90Z.zRotated()
            yield(pattern180Z)
            val pattern270Z = pattern180Z.zRotated()
            yield(pattern270Z)
        }
    }

    /**
     * Rotations
     *
     * @param pattern
     * @param options
     * @return
     */
    protected fun rotations(pattern: IntArray3D, options: Cartesian3DModelOptions): Sequence<IntArray3D> {
        return sequence {
            if (options.allowXRotations) {
                val xRotations = xRotations(pattern)
                for (rotation in xRotations) {
                    yield(rotation)
                }

                if (options.allowYRotations) {
                    val yRotations = xRotations.map { yRotations(it) }.flatten()
                    for (rotation in yRotations) {
                        yield(rotation)
                    }

                    if (options.allowZRotations) {
                        val zRotations = yRotations.map { zRotations(it) }.flatten()
                        for (rotation in zRotations) {
                            yield(rotation)
                        }
                    }
                }

                if (options.allowZRotations) {
                    val zRotations = xRotations.map { zRotations(it) }.flatten()
                    for (rotation in zRotations) {
                        yield(rotation)
                    }
                }
            }
            if (options.allowYRotations) {
                val yRotations = yRotations(pattern)
                for (rotation in yRotations) {
                    yield(rotation)
                }

                if (options.allowZRotations) {
                    val zRotations = yRotations.map { zRotations(it) }.flatten()
                    for (rotation in zRotations) {
                        yield(rotation)
                    }
                }
            }
            if (options.allowZRotations) {
                val zRotations = zRotations(pattern)
                for (rotation in zRotations) {
                    yield(rotation)
                }
            }
        }
    }

}