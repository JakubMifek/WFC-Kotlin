package org.mifek.wfc.models.storage

import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.datastructures.PatternsArrayBuilder
import org.mifek.wfc.datatypes.Direction2D
import org.mifek.wfc.models.Patterns
import org.mifek.wfc.models.Pixels
import org.mifek.wfc.models.options.Cartesian2DModelOptions
import org.mifek.wfc.utils.chain
import kotlin.math.pow

class PatternWeights2D(
    val input: IntArray2D,
    val overlap: Int,
    val options: Cartesian2DModelOptions = Cartesian2DModelOptions(),
) {
    val patternSideSize = overlap + 1

    val firstRowPatterns = ArrayList<Int>()
    val lastRowPatterns = ArrayList<Int>()
    private val patternCounts = loadPatterns(
        input,
        overlap,
    )

    val weightSum = patternCounts.map { it.second.item }.sum()

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

    val weights =
        DoubleArray(patternCounts.size) {
            (patternCounts[it].second.item / weightSum.toDouble()).pow(options.weightPower)
        }
    val propagator = Array(4) { dir ->
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
                    pab.add(it.asIntArray())
                }

                if (options.roofed && yOffset == 0) {
                    firstRowPatterns.add(pab.indexOfFirst { it.contentEquals(pattern.data) })
                }
                if (options.grounded && yOffset == data.height - 1 - overlap) {
                    lastRowPatterns.add(pab.indexOfFirst { it.contentEquals(pattern.data) })
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
}