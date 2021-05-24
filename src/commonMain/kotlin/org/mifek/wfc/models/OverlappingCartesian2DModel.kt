package org.mifek.wfc.models

import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.datastructures.PatternsArrayBuilder
import org.mifek.wfc.datatypes.Direction2D
import org.mifek.wfc.models.options.Cartesian2DModelOptions
import org.mifek.wfc.topologies.Cartesian2DTopology
import org.mifek.wfc.utils.chain
import org.mifek.wfc.utils.formatPatterns
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex

open class OverlappingCartesian2DModel(
    val input: IntArray2D,
    val overlap: Int,
    val outputWidth: Int,
    val outputHeight: Int,
    val options: Cartesian2DModelOptions = Cartesian2DModelOptions(),
) : OverlappingModel {
    protected val patternSideSize = overlap + 1
    val outputSizes = intArrayOf(outputWidth, outputHeight)

    protected val firstRowPatterns = ArrayList<Int>()
    protected val lastRowPatterns = ArrayList<Int>()
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
    protected val propagator = Array(4) { dir ->
        Array(patternsArray.size) { patternIndex ->
            val d = Direction2D.fromInt(dir)
            patternsArray.indices.filter {
                agrees(
                    patternsArray[patternIndex],
                    patternsArray[it],
                    d,
                )
            }.toIntArray()
        }
    }
    val topology = Cartesian2DTopology(
        outputWidth - if (options.periodicOutput) 0 else overlap, // We can compute smaller array, missing pixels are fixed by boundary patterns
        outputHeight - if (options.periodicOutput) 0 else overlap, // But if output is periodic, this is not desirable because it would scale down the output
        options.periodicOutput
    )
    protected val bans = mutableMapOf<Int, MutableList<Int>>()

    override fun build(): Cartesian2DWfcAlgorithm {
        val algorithm = Cartesian2DWfcAlgorithm(
            topology, weights, propagator, patterns, pixels
        )
        algorithm.beforeStart += {
            for (entry in bans) {
                algorithm.banWavePatterns(entry.key, entry.value)
            }
        }
        if (options.grounded) {
            algorithm.beforeStart += {
                algorithm.setCoordinatePatterns(
                    (0 until topology.width).map {
                        Pair(
                            it,
                            topology.height - 1 - if (options.periodicOutput) overlap else 0
                        )
                    },
                    lastRowPatterns
                )
            }
        }
        if (options.banGroundElsewhere) {
            algorithm.beforeStart += {
                algorithm.banCoordinatePatterns(
                    (0 until topology.width - if (options.periodicOutput) overlap else 0).map { x ->
                        (0 until topology.height - 1 - if (options.periodicOutput) overlap else 0).map { y ->
                            Pair(x, y)
                        }
                    }.reduce { acc, list -> acc.plus(list) },
                    lastRowPatterns
                )
            }
        }
        if (options.roofed) {
            algorithm.beforeStart += {
                algorithm.setCoordinatePatterns(
                    (0 until topology.width - if (options.periodicOutput) overlap else 0).map { Pair(it, 0) },
                    firstRowPatterns
                )
            }
        }
        if (options.banRoofElsewhere) {
            algorithm.beforeStart += {
                algorithm.banCoordinatePatterns(
                    (0 until topology.width - if (options.periodicOutput) overlap else 0).map { x ->
                        (1 until topology.height - if (options.periodicOutput) overlap else 0).map { y ->
                            Pair(x, y)
                        }
                    }.reduce { acc, list -> acc.plus(list) },
                    firstRowPatterns
                )
            }
        }
        if (options.sided) {
            algorithm.beforeStart += {
                val possiblePixels = input.column(0).plus(input.column(input.width - 1)).distinct()
                algorithm.setCoordinatePixels(
                    (0 until topology.height - if (options.periodicOutput) overlap else 0).map { Pair(0, it) }
                        .plus((0 until topology.height - if (options.periodicOutput) overlap else 0).map {
                            Pair(
                                topology.width - 1 - if (options.periodicOutput) overlap else 0,
                                it
                            )
                        }),
                    possiblePixels
                )
            }
        }
        if (options.banSidesElsewhere) {
            algorithm.beforeStart += {
                val possiblePixels = input.column(0).plus(input.column(input.width - 1)).distinct()
                algorithm.setCoordinatePixels(
                    (0 until topology.height - if (options.periodicOutput) overlap else 0)
                        .map { y ->
                            (1 until topology.width - 1 - if (options.periodicOutput) overlap else 0).map { x ->
                                Pair(
                                    x,
                                    y
                                )
                            }
                        }
                        .reduce { acc, list -> acc.plus(list) },
                    possiblePixels
                )
            }
        }
        return algorithm
    }


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

    fun banPatterns(
        coordinates: Iterable<Pair<Int, Int>>,
        patterns: Iterable<Int>
    ): OverlappingCartesian2DModel {
        coordinates.forEach {
            banPatterns(it.first, it.second, patterns)
        }

        return this
    }

    fun setPatterns(x: Int, y: Int, patterns: Iterable<Int>): OverlappingCartesian2DModel {
        banPatterns(x, y, patternsArray.indices.minus(patterns))

        return this
    }

    fun setPatterns(
        coordinates: Iterable<Pair<Int, Int>>,
        patterns: Iterable<Int>
    ): OverlappingCartesian2DModel {
        coordinates.forEach {
            setPatterns(it.first, it.second, patterns)
        }

        return this
    }

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

    fun setPixel(coordinates: Iterable<Pair<Int, Int>>, pixel: Int): OverlappingCartesian2DModel {
        coordinates.forEach {
            setPixel(it.first, it.second, pixel)
        }

        return this
    }

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

    fun banPixel(coordinates: Iterable<Pair<Int, Int>>, pixel: Int): OverlappingCartesian2DModel {
        coordinates.forEach {
            banPixel(it.first, it.second, pixel)
        }

        return this
    }

    fun setPixels(x: Int, y: Int, pixels: Iterable<Int>): OverlappingCartesian2DModel {
        for (
        pixel in this.pixels
            .filter { entry -> entry.key in pixels }
            .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        ) {
            setPixel(x, y, pixel)
        }

        return this
    }

    fun setPixels(coordinates: Iterable<Pair<Int, Int>>, pixels: Iterable<Int>): OverlappingCartesian2DModel {
        coordinates.forEach {
            setPixels(it.first, it.second, pixels)
        }

        return this
    }

    fun banPixels(x: Int, y: Int, pixels: Iterable<Int>): OverlappingCartesian2DModel {
        for (
        pixel in this.pixels
            .filter { entry -> entry.key in pixels }
            .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        ) {
            banPixel(x, y, pixel)
        }

        return this
    }

    fun banPixels(coordinates: Iterable<Pair<Int, Int>>, pixels: Iterable<Int>): OverlappingCartesian2DModel {
        coordinates.forEach {
            banPixels(it.first, it.second, pixels)
        }

        return this
    }

    /**
     * Checks whether two overlapping patterns agree
     * @param size Size of single side of the pattern (square patterns expected)
     */
    protected fun agrees(
        pattern1: IntArray2D,
        pattern2: IntArray2D,
        direction: Direction2D,
    ): Boolean {
        val line1 = when (direction) {
            Direction2D.NORTH -> pattern1.rows(0 until overlap).iterator().asSequence()
                .chain(overlap, patternSideSize)
            Direction2D.EAST -> pattern1.columns((patternSideSize - overlap) until patternSideSize).iterator()
                .asSequence()
                .chain(overlap, patternSideSize)
            Direction2D.SOUTH -> pattern1.rows((patternSideSize - overlap) until patternSideSize).iterator()
                .asSequence()
                .chain(overlap, patternSideSize)
            Direction2D.WEST -> pattern1.columns(0 until overlap).iterator().asSequence()
                .chain(overlap, patternSideSize)
        }
        val line2 = when (direction) {
            Direction2D.NORTH -> pattern2.rows((patternSideSize - overlap) until patternSideSize).iterator()
                .asSequence()
                .chain(overlap, patternSideSize)
            Direction2D.EAST -> pattern2.columns(0 until overlap).iterator().asSequence()
                .chain(overlap, patternSideSize)
            Direction2D.SOUTH -> pattern2.rows(0 until overlap).iterator().asSequence()
                .chain(overlap, patternSideSize)
            Direction2D.WEST -> pattern2.columns((patternSideSize - overlap) until patternSideSize).iterator()
                .asSequence()
                .chain(overlap, patternSideSize)
        }
        return line1.contentEquals(line2)
    }


    /**
     * Loads patterns and number of their occurrences in the input image
     */
    protected fun loadPatterns(
        data: IntArray2D,
        overlap: Int,
    ): List<Pair<IntArray2D, IntHolder>> {
        val pab = PatternsArrayBuilder()

        // Go through input image without borders
        val yMax = (data.height - if (options.periodicInput) 0 else overlap)
        val xMax = (data.width - if (options.periodicInput) 0 else overlap)

        for (yOffset in 0 until yMax) {
            val preIndex = yOffset * data.width
            for (xOffset in 0 until xMax) {
                val index = preIndex + xOffset

                // Create pattern
                val pattern = data.slice(index, 0..overlap, 0..overlap)
                val foundPatterns = mutableListOf(pattern)

                if (options.allowHorizontalFlips || options.allowVerticalFlips) {
                    if (options.allowHorizontalFlips) {
                        val patternH = pattern.hFlipped()
                        foundPatterns.add(patternH)

                        if (options.allowVerticalFlips) {
                            val patternHV = patternH.vFlipped()
                            foundPatterns.add(patternHV)
                        }
                    }
                    if (options.allowVerticalFlips) {
                        val patternV = pattern.vFlipped()
                        foundPatterns.add(patternV)
                    }
                }

                if (options.allowRotations) {
                    val pattern90 = pattern.rotated()
                    val pattern180 = pattern90.rotated()
                    val pattern270 = pattern180.rotated()

                    foundPatterns.addAll(
                        sequenceOf(
                            pattern90,
                            pattern180,
                            pattern270,
                        )
                    )


                    if (options.allowHorizontalFlips || options.allowVerticalFlips) {
                        if (options.allowHorizontalFlips) {
                            val pattern90H = pattern90.hFlipped()
                            val pattern180H = pattern180.hFlipped()
                            val pattern270H = pattern270.hFlipped()
                            foundPatterns.addAll(
                                sequenceOf(
                                    pattern90H,
                                    pattern180H,
                                    pattern270H,
                                )
                            )

                            if (options.allowVerticalFlips) {
                                val pattern90HV = pattern90H.vFlipped()
                                val pattern180HV = pattern180H.vFlipped()
                                val pattern270HV = pattern270H.vFlipped()
                                foundPatterns.addAll(
                                    sequenceOf(
                                        pattern90HV,
                                        pattern180HV,
                                        pattern270HV,
                                    )
                                )
                            }
                        }
                        if (options.allowVerticalFlips) {
                            val pattern90V = pattern90.vFlipped()
                            val pattern180V = pattern180.vFlipped()
                            val pattern270V = pattern270.vFlipped()
                            foundPatterns.addAll(
                                sequenceOf(
                                    pattern90V,
                                    pattern180V,
                                    pattern270V,
                                )
                            )
                        }
                    }
                }

                foundPatterns.forEach {
                    val prevSize = pab.size
                    pab.add(it.asIntArray())
                    if (options.roofed && yOffset == 0 && prevSize != pab.size) {
                        firstRowPatterns.add(prevSize)
                    }
                    if (options.grounded && yOffset == data.height - 1 - overlap && prevSize != pab.size) {
                        lastRowPatterns.add(prevSize)
                    }
                }
            }
        }

        return pab.patterns.map { pair ->
            Pair(
                IntArray2D(patternSideSize, patternSideSize) { pair.first[it] },
                pair.second
            )
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

    /**
     * Shifts wave index from algorithm coordinates to output coordinates.
     */
    fun shiftAlgorithmWave(wave: Int): Int {
        if (options.periodicOutput) return wave
        return wave + (wave / (outputWidth - overlap)) * overlap
    }

    /**
     * Shifts wave index from output coordinates to algorithm coordinates and an optional shift which represents index in the pattern (for boundary pixels).
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
     * Uses Int.MIN_VALUE for pixels without any feasible pattern
     */
    @ExperimentalUnsignedTypes
    open fun constructOutput(algorithm: Cartesian2DWfcAlgorithm): IntArray2D {
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