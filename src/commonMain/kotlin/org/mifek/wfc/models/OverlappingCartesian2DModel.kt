package org.mifek.wfc.models

import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.datastructures.PatternsArrayBuilder
import org.mifek.wfc.datatypes.Direction2D
import org.mifek.wfc.models.options.Cartesian2DModelOptions
import org.mifek.wfc.models.storage.PatternWeights2D
import org.mifek.wfc.topologies.Cartesian2DTopology
import org.mifek.wfc.utils.chain
import org.mifek.wfc.utils.formatPatterns
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex

/**
 * Overlapping cartesian2d model
 *
 * @property storage
 * @property outputWidth
 * @property outputHeight
 * @constructor Create empty Overlapping cartesian2d model
 */
open class OverlappingCartesian2DModel(
    val storage: PatternWeights2D,
    val outputWidth: Int,
    val outputHeight: Int,
) : OverlappingModel {
    constructor(
        input: IntArray2D,
        overlap: Int,
        outputWidth: Int,
        outputHeight: Int,
        options: Cartesian2DModelOptions = Cartesian2DModelOptions()
    ) : this(PatternWeights2D(input, overlap, options), outputWidth, outputHeight)

    val overlap = storage.overlap
    val patternSideSize = storage.patternSideSize

    private val options = storage.options
    private val weights = storage.weights
    private val propagator = storage.propagator
    private val lastRowPatterns = storage.lastRowPatterns
    private val firstRowPatterns = storage.firstRowPatterns
    private val patternsArray = storage.patternsArray
    private val input = storage.input

    override val pixels = storage.pixels
    override val patterns = storage.patterns

    val outputSizes = intArrayOf(outputWidth, outputHeight)
    val topology = Cartesian2DTopology(
        outputWidth - if (options.periodicOutput) 0 else overlap, // We can compute smaller array, missing pixels are fixed by boundary patterns
        outputHeight - if (options.periodicOutput) 0 else overlap, // But if output is periodic, this is not desirable because it would scale down the output
        options.periodicOutput
    )
    protected val bans = mutableMapOf<Int, MutableList<Int>>()

    init {
        if (options.grounded) {
            this.setPixels(
                (0 until outputWidth).map {
                    Pair(
                        it,
                        outputHeight - 1
                    )
                },
                input.row(input.height - 1).distinct()
            )
        }
        if (options.banGroundElsewhere) {
            this.banPixels(
                (0 until outputWidth).map { x ->
                    (0 until outputHeight - 1 - overlap).map { y ->
                        Pair(
                            x,
                            y
                        )
                    }
                }.flatten(),
                input.row(input.height - 1).distinct()
            )
        }
        if (options.roofed) {
            this.setPixels(
                (0 until outputWidth).map {
                    Pair(
                        it,
                        0
                    )
                },
                input.row(0).distinct()
            )
        }
        if (options.banRoofElsewhere) {
            this.banPixels(
                (0 until outputWidth).map { x ->
                    (1 + overlap until outputHeight).map { y ->
                        Pair(
                            x,
                            y
                        )
                    }
                }.flatten(),
                input.row(0).distinct()
            )
        }
        if (options.leftSided) {
            this.setPixels(
                (0 until outputHeight).map { y ->
                    Pair(
                        0,
                        y
                    )
                },
                input.column(0).distinct()
            )
        }
        if (options.banLeftSideElsewhere) {
            this.banPixels(
                (1 + overlap until outputWidth).map { x ->
                    (0 until outputHeight).map { y ->
                        Pair(
                            x,
                            y
                        )
                    }
                }.flatten(),
                input.column(0).distinct()
            )
        }
        if (options.rightSided) {
            this.setPixels(
                (0 until outputHeight).map { y ->
                    Pair(
                        outputWidth - 1,
                        y
                    )
                },
                input.column(input.width - 1).distinct()
            )
        }
        if (options.banRightSideElsewhere) {
            this.banPixels(
                (0 until outputWidth - 1 - overlap).map { x ->
                    (0 until outputHeight).map { y ->
                        Pair(
                            x,
                            y
                        )
                    }
                }.flatten(),
                input.column(input.width - 1).distinct()
            )
        }
    }

    override fun build(): Cartesian2DWfcAlgorithm {
        val algorithm = Cartesian2DWfcAlgorithm(
            topology, weights, propagator, patterns, pixels
        )
        algorithm.beforeStart += {
            for (entry in bans) {
                if (!algorithm.banWavePatterns(entry.key, entry.value)) {
                    break
                }
            }
        }
        return algorithm
    }


    /**
     * Ban patterns
     *
     * @param x
     * @param y
     * @param patterns
     * @return
     */
    fun banPatterns(x: Int, y: Int, patterns: Iterable<Int>): OverlappingCartesian2DModel {
        if (
            x >= outputWidth - (if (options.periodicOutput) 0 else overlap) ||
            y >= outputHeight - (if (options.periodicOutput) 0 else overlap)
        ) {
            throw Error("Output dimensions exceeded [$x, $y]x[${outputWidth - (if (options.periodicOutput) 0 else overlap)}, ${outputHeight - (if (options.periodicOutput) 0 else overlap)}].")
        }

        val coordinates = topology.serializeCoordinates(x, y)
        if (!bans.containsKey(coordinates)) {
            bans[coordinates] = patterns.toMutableList()
        } else {
            bans[coordinates]!!.addAll(patterns)
        }

        return this
    }

    /**
     * Ban patterns
     *
     * @param coordinates
     * @param patterns
     * @return
     */
    fun banPatterns(
        coordinates: Iterable<Pair<Int, Int>>,
        patterns: Iterable<Int>
    ): OverlappingCartesian2DModel {
        coordinates.forEach {
            banPatterns(it.first, it.second, patterns)
        }

        return this
    }

    /**
     * Set patterns
     *
     * @param x
     * @param y
     * @param patterns
     * @return
     */
    fun setPatterns(x: Int, y: Int, patterns: Iterable<Int>): OverlappingCartesian2DModel {
        banPatterns(x, y, patternsArray.indices.minus(patterns))

        return this
    }

    /**
     * Set patterns
     *
     * @param coordinates
     * @param patterns
     * @return
     */
    fun setPatterns(
        coordinates: Iterable<Pair<Int, Int>>,
        patterns: Iterable<Int>
    ): OverlappingCartesian2DModel {
        coordinates.forEach {
            setPatterns(it.first, it.second, patterns)
        }

        return this
    }

    /**
     * Set pixel
     *
     * @param x
     * @param y
     * @param pixel
     * @return
     */
    fun setPixel(x: Int, y: Int, pixel: Int): OverlappingCartesian2DModel {
        if (options.periodicOutput) {
            setPatterns(x, y, pixels[pixel].asIterable())
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

        setPatterns(X, Y, patterns.indices.filter { patternsArray[it][xShift, yShift] == pixel })

        return this
    }

    /**
     * Set pixel
     *
     * @param coordinates
     * @param pixel
     * @return
     */
    fun setPixel(coordinates: Iterable<Pair<Int, Int>>, pixel: Int): OverlappingCartesian2DModel {
        coordinates.forEach {
            setPixel(it.first, it.second, pixel)
        }

        return this
    }

    /**
     * Ban pixel
     *
     * @param x
     * @param y
     * @param pixel
     * @return
     */
    fun banPixel(x: Int, y: Int, pixel: Int): OverlappingCartesian2DModel {
        if (options.periodicOutput) {
            banPatterns(x, y, pixels[pixel].asIterable())
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

        banPatterns(X, Y, patterns.indices.filter { patternsArray[it][xShift, yShift] == pixel })

        return this
    }

    /**
     * Ban pixel
     *
     * @param coordinates
     * @param pixel
     * @return
     */
    fun banPixel(coordinates: Iterable<Pair<Int, Int>>, pixel: Int): OverlappingCartesian2DModel {
        coordinates.forEach {
            banPixel(it.first, it.second, pixel)
        }

        return this
    }

    /**
     * Set pixels
     *
     * @param x
     * @param y
     * @param pixels
     * @return
     */
    fun setPixels(x: Int, y: Int, pixels: Iterable<Int>): OverlappingCartesian2DModel {
        pixels.forEach {
            setPixel(x, y, it)
        }

        return this
    }

    /**
     * Set pixels
     *
     * @param coordinates
     * @param pixels
     * @return
     */
    fun setPixels(coordinates: Iterable<Pair<Int, Int>>, pixels: Iterable<Int>): OverlappingCartesian2DModel {
        coordinates.forEach {
            setPixels(it.first, it.second, pixels)
        }

        return this
    }

    /**
     * Ban pixels
     *
     * @param x
     * @param y
     * @param pixels
     * @return
     */
    fun banPixels(x: Int, y: Int, pixels: Iterable<Int>): OverlappingCartesian2DModel {
        pixels.forEach {
            banPixel(x, y, it)
        }

        return this
    }

    /**
     * Ban pixels
     *
     * @param coordinates
     * @param pixels
     * @return
     */
    fun banPixels(coordinates: Iterable<Pair<Int, Int>>, pixels: Iterable<Int>): OverlappingCartesian2DModel {
        coordinates.forEach {
            banPixels(it.first, it.second, pixels)
        }

        return this
    }

    /**
     * On boundary
     *
     * @param waveIndex
     * @return
     */
    protected fun onBoundary(waveIndex: Int): Boolean {
        if (waveIndex % outputWidth >= outputWidth - overlap) {
            return true
        }
        if (waveIndex >= outputWidth * (outputHeight - overlap)) {
            return true
        }
        return false
    }

    /**
     * Shift algorithm wave
     *
     * @param wave
     * @return
     */
    fun shiftAlgorithmWave(wave: Int): Int {
        if (options.periodicOutput) return wave
        return wave + (wave / (outputWidth - overlap)) * overlap
    }

    /**
     * Shift output wave
     *
     * @param wave
     * @return
     */
    fun shiftOutputWave(wave: Int): Pair<Int, Int> {
        var index = wave
        var shiftX = 0
        var shiftY = 0

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
            }

            index -= (index / outputWidth) * overlap
        }

        val shift = shiftY * (overlap + 1) + shiftX

        return Pair(index, shift)
    }

    /**
     * Construct nullable output
     *
     * @param algorithm
     * @return
     */
    @ExperimentalUnsignedTypes
    open fun constructNullableOutput(algorithm: Cartesian2DWfcAlgorithm): Array<Array<Int?>> {
        if (!algorithm.hasRun) {
            println("WARNING: Algorithm hasn't run yet.")
        }

        return Array(outputWidth) { x ->
            Array(outputHeight) { y ->
                val waveIndex = intArrayOf(x, y).toIndex(outputSizes)
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

    /**
     * Construct averaged output
     *
     * @param algorithm
     * @return
     */
    @ExperimentalUnsignedTypes
    open fun constructAveragedOutput(algorithm: Cartesian2DWfcAlgorithm): IntArray2D {
        if (!algorithm.hasRun) {
            println("WARNING: Algorithm hasn't run yet.")
        }

        return IntArray2D(outputWidth, outputHeight) { waveIndex ->
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
                        .filter { algorithm.waves[index, it] }
                        .map { patternsArray[it][shift] }
                        .sum() / sum
                }
            }
        }
    }
}