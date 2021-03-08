package org.mifek.wfc.models

import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.datastructures.PatternsArrayBuilder
import org.mifek.wfc.datatypes.Directions2D
import org.mifek.wfc.models.options.Cartesian2DModelOptions
import org.mifek.wfc.topologies.Cartesian2DTopology
import org.mifek.wfc.utils.chain

open class OverlappingCartesian2DModel(
    val input: IntArray2D,
    val overlap: Int,
    val outputWidth: Int,
    val outputHeight: Int,
    val options: Cartesian2DModelOptions = Cartesian2DModelOptions(),
) : OverlappingModel {
    private val patternSideSize = overlap + 1

    private val firstRowPatterns = ArrayList<Int>()
    private val lastRowPatterns = ArrayList<Int>()
    private val patternCounts = loadPatterns(
        input,
        overlap,
    )

    private val sum = patternCounts.map { it.second.item }.sum()

    protected val patternsArray = patternCounts.map { it.first }.toTypedArray()
    override val patterns = Patterns(patternsArray)
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
        val topology = Cartesian2DTopology(
            outputWidth - if (options.periodicOutput) 0 else overlap, // We can compute smaller array, missing pixels are fixed by boundary patterns
            outputHeight - if (options.periodicOutput) 0 else overlap, // But if output is periodic, this is not desirable because it would scale down the output
            options.periodicOutput
        )
        val algorithm = Cartesian2DWfcAlgorithm(
            topology, weights, propagator, patterns, pixels
        )
        if (options.grounded) {
            algorithm.onStart += {
                algorithm.setMultiplePatterns(
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
            algorithm.onStart += {
                algorithm.banMultiplePatterns(
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
            algorithm.onStart += {
                algorithm.setMultiplePatterns(
                    (0 until topology.width - if (options.periodicOutput) overlap else 0).map { Pair(it, 0) },
                    firstRowPatterns
                )
            }
        }
        if (options.banRoofElsewhere) {
            algorithm.onStart += {
                algorithm.banMultiplePatterns(
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
            algorithm.onStart += {
                val possiblePixels = input.column(0).plus(input.column(input.width - 1)).distinct()
                algorithm.setMultiplePixels(
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
            algorithm.onStart += {
                val possiblePixels = input.column(0).plus(input.column(input.width - 1)).distinct()
                algorithm.setMultiplePixels(
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


    /**
     * Checks whether two overlapping patterns agree
     * @param size Size of single side of the pattern (square patterns expected)
     */
    private fun agrees(
        pattern1: IntArray2D,
        pattern2: IntArray2D,
        size: Int,
        direction: Directions2D,
        overlap: Int
    ): Boolean {
        val line1 = when (direction) {
            Directions2D.NORTH -> pattern1.rows(0 until overlap).iterator().asSequence()
                .chain(overlap, size)
            Directions2D.EAST -> pattern1.columns((size - overlap) until size).iterator().asSequence()
                .chain(overlap, size)
            Directions2D.SOUTH -> pattern1.rows((size - overlap) until size).iterator().asSequence()
                .chain(overlap, size)
            Directions2D.WEST -> pattern1.columns(0 until overlap).iterator().asSequence()
                .chain(overlap, size)
        }
        val line2 = when (direction) {
            Directions2D.NORTH -> pattern2.rows((size - overlap) until size).iterator().asSequence()
                .chain(overlap, size)
            Directions2D.EAST -> pattern2.columns(0 until overlap).iterator().asSequence()
                .chain(overlap, size)
            Directions2D.SOUTH -> pattern2.rows(0 until overlap).iterator().asSequence()
                .chain(overlap, size)
            Directions2D.WEST -> pattern2.columns((size - overlap) until size).iterator().asSequence()
                .chain(overlap, size)
        }
        return line1.contentEquals(line2)
    }


    /**
     * Loads patterns and number of their occurrences in the input image
     */
    private fun loadPatterns(
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
                val pattern = IntArray2D(patternSideSize, patternSideSize)
                var patternIndex = 0
                for (y in 0..overlap) {
                    val postIndex = (index + y * data.width) % data.size
                    for (x in 0..overlap) {
                        val height = postIndex / data.width
                        val position = ((postIndex % data.width) + x) % data.width
                        pattern[patternIndex] = data[height * data.width + position]
                        patternIndex++
                    }
                }

                val foundPatterns = mutableListOf(pattern)

                if (options.allowFlips) {
                    val patternH = pattern.hFlipped()
                    val patternV = pattern.vFlipped()
                    val patternHV = patternH.vFlipped()

                    foundPatterns.addAll(sequenceOf(patternH, patternV, patternHV))
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


                    if (options.allowFlips) {
                        val pattern90H = pattern90.hFlipped()
                        val pattern90V = pattern90.vFlipped()
                        val pattern90HV = pattern90H.vFlipped()

                        val pattern180H = pattern180.hFlipped()
                        val pattern180V = pattern180.vFlipped()
                        val pattern180HV = pattern180H.vFlipped()

                        val pattern270H = pattern270.hFlipped()
                        val pattern270V = pattern270.vFlipped()
                        val pattern270HV = pattern270H.vFlipped()

                        foundPatterns.addAll(
                            sequenceOf(
                                pattern90H,
                                pattern90V,
                                pattern90HV,
                                pattern180H,
                                pattern180V,
                                pattern180HV,
                                pattern270H,
                                pattern270V,
                                pattern270HV,
                            )
                        )
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