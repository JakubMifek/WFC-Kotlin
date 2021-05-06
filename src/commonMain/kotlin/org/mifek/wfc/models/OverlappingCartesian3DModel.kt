package org.mifek.wfc.models

import org.mifek.wfc.core.Cartesian3DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.datastructures.PatternsArrayBuilder
import org.mifek.wfc.datatypes.Direction3D
import org.mifek.wfc.models.options.Cartesian3DModelOptions
import org.mifek.wfc.topologies.Cartesian3DTopology
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex

open class OverlappingCartesian3DModel(
    val input: IntArray3D,
    val overlap: Int,
    val outputWidth: Int,
    val outputHeight: Int,
    val outputDepth: Int,
    val options: Cartesian3DModelOptions = Cartesian3DModelOptions(),
) : OverlappingModel {
    protected val patternSideSize = overlap + 1

    protected val patternCounts = loadPatterns(
        input,
        overlap,
    )

    protected val weightSum = patternCounts.map { it.second.item }.sum()

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

    protected val weights = DoubleArray(patternCounts.size) { patternCounts[it].second.item / weightSum.toDouble() }
    internal val propagator = Array(6) { dir ->
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
    internal val topology = Cartesian3DTopology(
        outputWidth - if (options.periodicOutput) 0 else overlap, // We can compute smaller array, missing pixels are fixed by boundary patterns
        outputHeight - if (options.periodicOutput) 0 else overlap, // But if output is periodic, this is not desirable because it would scale down the output
        outputDepth - if (options.periodicOutput) 0 else overlap,
        options.periodicOutput
    )
    protected val bans = mutableMapOf<Int, MutableList<Int>>()

    override fun build(): Cartesian3DWfcAlgorithm {
        val algorithm = Cartesian3DWfcAlgorithm(
            topology, weights, propagator, patterns, pixels,
        )
        algorithm.beforeStart += {
            for (entry in bans) {
                algorithm.banWavePatterns(entry.key, entry.value)
            }
        }
        return algorithm
    }

    fun banPatterns(x: Int, y: Int, z: Int, patterns: Iterable<Int>): OverlappingCartesian3DModel {
        if (
            x >= outputWidth - (if (options.periodicOutput) 0 else overlap) ||
            y >= outputHeight - (if (options.periodicOutput) 0 else overlap) ||
            z >= outputDepth - (if (options.periodicOutput) 0 else overlap)
        ) {
            throw Error("Output dimensions exceeded [$x, $y, $z]x[${outputWidth - (if (options.periodicOutput) 0 else overlap)}, ${outputHeight - (if (options.periodicOutput) 0 else overlap)}, ${outputDepth - (if (options.periodicOutput) 0 else overlap)}].")
        }

        val coordinates = topology.serializeCoordinates(x, y, z)
        if (!bans.containsKey(coordinates)) {
            bans[coordinates] = patterns.toMutableList()
        } else {
            bans[coordinates]!!.addAll(patterns)
        }

        return this
    }

    fun banPatterns(
        coordinates: Iterable<Triple<Int, Int, Int>>,
        patterns: Iterable<Int>
    ): OverlappingCartesian3DModel {
        coordinates.forEach {
            banPatterns(it.first, it.second, it.third, patterns)
        }

        return this
    }

    fun setPatterns(x: Int, y: Int, z: Int, patterns: Iterable<Int>): OverlappingCartesian3DModel {
        banPatterns(x, y, z, patternsArray.indices.minus(patterns))

        return this
    }

    fun setPatterns(
        coordinates: Iterable<Triple<Int, Int, Int>>,
        patterns: Iterable<Int>
    ): OverlappingCartesian3DModel {
        coordinates.forEach {
            setPatterns(it.first, it.second, it.third, patterns)
        }

        return this
    }

    fun setPixel(x: Int, y: Int, z: Int, pixel: Int): OverlappingCartesian3DModel {
        if (options.periodicOutput) {
            setPatterns(x, y, z, pixels[pixel].asIterable())
            return this
        }

        var X = x
        var xShift = 0
        if (X >= outputWidth - overlap) {
            X = outputWidth - overlap - 1
            xShift = x - X
        }

        var Y = y
        var yShift = 0
        if (Y >= outputHeight - overlap) {
            Y = outputHeight - overlap - 1
            yShift = y - Y
        }

        var Z = z
        var zShift = 0
        if (Z >= outputDepth - overlap) {
            Z = outputDepth - overlap - 1
            zShift = z - Z
        }

        setPatterns(X, Y, Z, patterns.indices.filter { patternsArray[it][xShift, yShift, zShift] == pixel })

        return this
    }

    fun setPixel(coordinates: Iterable<Triple<Int, Int, Int>>, pixel: Int): OverlappingCartesian3DModel {
        coordinates.forEach {
            setPixel(it.first, it.second, it.third, pixel)
        }

        return this
    }

    fun banPixel(x: Int, y: Int, z: Int, pixel: Int): OverlappingCartesian3DModel {
        if (options.periodicOutput) {
            banPatterns(x, y, z, pixels[pixel].asIterable())
            return this
        }

        var X = x
        var xShift = 0
        if (X >= outputWidth - overlap) {
            X = outputWidth - overlap - 1
            xShift = x - X
        }

        var Y = y
        var yShift = 0
        if (Y >= outputHeight - overlap) {
            Y = outputHeight - overlap - 1
            yShift = y - Y
        }

        var Z = z
        var zShift = 0
        if (Z >= outputDepth - overlap) {
            Z = outputDepth - overlap - 1
            zShift = z - Z
        }

        banPatterns(X, Y, Z, patterns.indices.filter { patternsArray[it][xShift, yShift, zShift] == pixel })

        return this
    }

    fun banPixel(coordinates: Iterable<Triple<Int, Int, Int>>, pixel: Int): OverlappingCartesian3DModel {
        coordinates.forEach {
            banPixel(it.first, it.second, it.third, pixel)
        }

        return this
    }

    fun setPixels(x: Int, y: Int, z: Int, pixels: Iterable<Int>): OverlappingCartesian3DModel {
        for (
        pixel in this.pixels
            .filter { entry -> entry.key in pixels }
            .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        ) {
            setPixel(x, y, z, pixel)
        }

        return this
    }

    fun setPixels(coordinates: Iterable<Triple<Int, Int, Int>>, pixels: Iterable<Int>): OverlappingCartesian3DModel {
        coordinates.forEach {
            setPixels(it.first, it.second, it.third, pixels)
        }

        return this
    }

    fun banPixels(x: Int, y: Int, z: Int, pixels: Iterable<Int>): OverlappingCartesian3DModel {
        for (
        pixel in this.pixels
            .filter { entry -> entry.key in pixels }
            .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        ) {
            banPixel(x, y, z, pixel)
        }

        return this
    }

    fun banPixels(coordinates: Iterable<Triple<Int, Int, Int>>, pixels: Iterable<Int>): OverlappingCartesian3DModel {
        coordinates.forEach {
            banPixels(it.first, it.second, it.third, pixels)
        }

        return this
    }

    /**
     * Checks whether two overlapping patterns agree
     * @param size Size of single side of the pattern (square patterns expected)
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
     * Loads patterns and number of their occurrences in the input image
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

                    if (options.allowRotations) {
                        val rotations = rotations(pattern)
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

    protected fun rotations(pattern: IntArray3D): Sequence<IntArray3D> {
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
        if ((waveIndex / outputWidth) % outputHeight >= outputHeight - overlap) {
            return true
        }
        if ((waveIndex / outputWidth) / outputHeight >= outputDepth - overlap) {
            return true
        }
        return false
    }

    @ExperimentalUnsignedTypes
    open fun constructNullableOutput(algorithm: Cartesian3DWfcAlgorithm): Array<Array<Array<Int?>>> {
        return Array(outputWidth) { x ->
            Array(outputHeight) { y ->
                Array(outputDepth) { z ->
                    val waveIndex = intArrayOf(x, y, z).toIndex(intArrayOf(outputWidth, outputHeight, outputDepth))
                    var index = waveIndex
                    var shiftX = 0
                    var shiftY = 0
                    var shiftZ = 0
                    val sizes = intArrayOf(outputWidth, outputHeight, outputDepth)
                    val faceSize = outputWidth * outputHeight

                    if (!options.periodicOutput) {
                        if (onBoundary(waveIndex)) {
                            val coordinates = waveIndex.toCoordinates(sizes)

                            if (coordinates[0] >= outputWidth - overlap) {
                                shiftX = coordinates[0] - outputWidth + overlap + 1
                                index -= shiftX
                            }
                            if (coordinates[1] >= outputHeight - overlap) {
                                shiftY = coordinates[1] - outputHeight + overlap + 1
                                index -= shiftY * outputWidth
                            }
                            if (coordinates[2] >= outputDepth - overlap) {
                                shiftZ = coordinates[2] - outputDepth + overlap + 1
                                index -= shiftZ * faceSize
                            }
                        }

                        index -= (((index % faceSize) / outputWidth)) * overlap +
                                (index / faceSize) * (outputHeight - overlap + outputWidth) * overlap
                    }

                    val shift = (shiftZ * patternSideSize + shiftY) * patternSideSize + shiftX

                    val a = 0
                    val b = 1
                    val sum = algorithm.waves[index].sumOf {
                        when (it) {
                            false -> a
                            true -> b
                        }
                    }

                    when (sum) {
                        0 -> Int.MIN_VALUE
                        1 -> patternsArray[patterns.indices.filter { algorithm.waves[index, it] }[0]][shift]
                        else -> null
                    }
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    open fun constructAveragedOutput(algorithm: Cartesian3DWfcAlgorithm): IntArray3D {
        return IntArray3D(outputWidth, outputHeight, outputDepth) { waveIndex ->
            var index = waveIndex
            var shiftX = 0
            var shiftY = 0
            var shiftZ = 0
            val sizes = intArrayOf(outputWidth, outputHeight, outputDepth)
            val faceSize = outputWidth * outputHeight

            if (!options.periodicOutput) {
                if (onBoundary(waveIndex)) {
                    val coordinates = waveIndex.toCoordinates(sizes)

                    if (coordinates[0] >= outputWidth - overlap) {
                        shiftX = coordinates[0] - outputWidth + overlap + 1
                        index -= shiftX
                    }
                    if (coordinates[1] >= outputHeight - overlap) {
                        shiftY = coordinates[1] - outputHeight + overlap + 1
                        index -= shiftY * outputWidth
                    }
                    if (coordinates[2] >= outputDepth - overlap) {
                        shiftZ = coordinates[2] - outputDepth + overlap + 1
                        index -= shiftZ * faceSize
                    }
                }

                index -= (((index % faceSize) / outputWidth)) * overlap +
                        (index / faceSize) * (outputHeight - overlap + outputWidth) * overlap
            }

            val shift = (shiftZ * patternSideSize + shiftY) * patternSideSize + shiftX

            val a = 0
            val b = 1
            val sum = algorithm.waves[index].sumOf {
                when (it) {
                    false -> a
                    true -> b
                }
            }
            when (sum) {
                0 -> Int.MIN_VALUE
                1 -> patternsArray[patterns.indices.filter { algorithm.waves[index, it] }[0]][shift]
                else -> {
                    patterns.indices
                        .filter { algorithm.waves[index, it] }.sumOf { patternsArray[it][shift] } / sum
                }
            }
        }
    }
}