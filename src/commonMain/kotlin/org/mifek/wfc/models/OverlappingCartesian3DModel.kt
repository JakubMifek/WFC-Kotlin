package org.mifek.wfc.models

import org.mifek.wfc.core.Cartesian3DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.models.options.Cartesian3DModelOptions
import org.mifek.wfc.models.storage.PatternWeights3D
import org.mifek.wfc.topologies.Cartesian3DTopology
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex

open class OverlappingCartesian3DModel(
    val storage: PatternWeights3D,
    val outputWidth: Int,
    val outputHeight: Int,
    val outputDepth: Int,
) : OverlappingModel {
    constructor(
        input: IntArray3D,
        overlap: Int,
        outputWidth: Int,
        outputHeight: Int,
        outputDepth: Int,
        options: Cartesian3DModelOptions = Cartesian3DModelOptions()
    ) : this(PatternWeights3D(input, overlap, options), outputWidth, outputHeight, outputDepth)

    val overlap = storage.overlap
    val patternSideSize = storage.patternSideSize

    private val options = storage.options
    private val weights = storage.weights
    private val propagator = storage.propagator
    private val patternsArray = storage.patternsArray

    override val patterns = storage.patterns
    override val pixels = storage.pixels

    val outputSizes = intArrayOf(outputWidth, outputHeight, outputDepth)
    val outputFaceSize = outputWidth * outputHeight
    val algorithmFaceSize = (outputWidth - if (options.periodicOutput) 0 else overlap) *
            (outputHeight - if (options.periodicOutput) 0 else overlap)

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

    /**
     * Shifts wave index from algorithm coordinates to output coordinates.
     */
    fun shiftAlgorithmWave(wave: Int): Int {
        if (options.periodicOutput) return wave
        return wave +
                (((wave % algorithmFaceSize) / (outputWidth - overlap))) * overlap +
                (wave / algorithmFaceSize) * (outputHeight - overlap + outputWidth) * overlap
    }

    /**
     * Shifts wave index from output coordinates to algorithm coordinates and an optional shift which represents index in the pattern (for boundary pixels).
     */
    fun shiftOutputWave(wave: Int): Pair<Int, Int> {
        var index = wave
        var shiftX = 0
        var shiftY = 0
        var shiftZ = 0

        if (!options.periodicOutput) {
            if (onBoundary(wave)) {
                val coordinates = wave.toCoordinates(outputSizes)

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
                    index -= shiftZ * outputFaceSize
                }
            }

            index -= (((index % outputFaceSize) / outputWidth)) * overlap +
                    (index / outputFaceSize) * (outputHeight - overlap + outputWidth) * overlap
        }

        val shift = (shiftZ * patternSideSize + shiftY) * patternSideSize + shiftX
        return Pair(index, shift)
    }

    @ExperimentalUnsignedTypes
    open fun constructNullableOutput(algorithm: Cartesian3DWfcAlgorithm): Array<Array<Array<Int?>>> {
        if (!algorithm.hasRun) {
            println("WARNING: Algorithm hasn't run yet.")
        }

        return Array(outputWidth) { x ->
            Array(outputHeight) { y ->
                Array(outputDepth) { z ->
                    val waveIndex = intArrayOf(x, y, z).toIndex(outputSizes)
                    val pair = shiftOutputWave(waveIndex)
                    val index = pair.first
                    val shift = pair.second

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

    /**
     * Uses Int.MIN_VALUE for pixels without any feasible pattern
     */
    @ExperimentalUnsignedTypes
    open fun constructAveragedOutput(algorithm: Cartesian3DWfcAlgorithm): IntArray3D {
        if (!algorithm.hasRun) {
            println("WARNING: Algorithm hasn't run yet.")
        }

        return IntArray3D(outputWidth, outputHeight, outputDepth) { waveIndex ->
            val pair = shiftOutputWave(waveIndex)
            val index = pair.first
            val shift = pair.second

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