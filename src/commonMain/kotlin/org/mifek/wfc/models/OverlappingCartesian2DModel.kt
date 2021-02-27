package org.mifek.wfc.models

import org.mifek.wfc.chain
import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.datatypes.Directions2D

class OverlappingCartesian2DModel(
        input: IntArray2D,
        val overlap: Int,
        val outputWidth: Int,
        val outputHeight: Int,
        allowRotations: Boolean = true,
        allowFlips: Boolean = true
) : OverlappingModel {
    private val patternCounts = loadPatterns(
            input,
            overlap,
            allowRotations = allowRotations,
            allowFlips = allowFlips
    )

    private val sum = patternCounts.map { it.second.item }.sum()
    private val patternSideSize = overlap + 1

    val patternsArray = patternCounts.map { it.first }.toTypedArray()
    override val patterns = Patterns(patternsArray.map { it[0] }.toIntArray())
    override val pixels = Pixels(
            mapOf(
                    *patterns.map { it }.distinct().map { pixel ->
                        Pair(
                                pixel,
                                patterns
                                        .mapIndexed { it, index -> Pair(it, index) }
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
        return Cartesian2DWfcAlgorithm(outputWidth, outputHeight, weights, propagator, patterns, pixels)
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
     * Adds pattern to accumulator (patterns). Indices used for faster search.
     */
    private fun addPattern(
            indices: HashMap<Int, MutableList<Int>>,
            pattern: IntArray2D,
            patterns: MutableList<Pair<IntArray2D, IntHolder>>
    ) {
        val hash = pattern.contentHashCode()

        // Check whether pattern exists
        if (indices.containsKey(hash)) {

            // If so, try to find it among hashed arguments
            val candidate = indices[hash]!!.find { patterns[it].first.contentEquals(pattern) }
            if (candidate != null) {
                // Increase counter if found [We expect this to be the case most often]
                patterns[candidate].second.item++
                return
            }

            // Add the pattern if not found
            indices[hash]!!.add(patterns.size)
            patterns.add(Pair(pattern, IntHolder(1)))
            return
        }

        // Add the pattern and create hash table for
        indices[hash] = arrayListOf(patterns.size)
        patterns.add(Pair(pattern, IntHolder(1)))
    }

    /**
     * Loads patterns and number of their occurrences in the input image
     */
    fun loadPatterns(
            data: IntArray2D,
            overlap: Int,
            allowRotations: Boolean = true,
            allowFlips: Boolean = true
    ): ArrayList<Pair<IntArray2D, IntHolder>> {
        val size = overlap + 1
        val patterns = ArrayList<Pair<IntArray2D, IntHolder>>()
        val indices = HashMap<Int, MutableList<Int>>()

        // Go through input image without borders
        for (yOffset in 0 until (data.height - overlap)) {
            val preIndex = yOffset * data.width
            for (xOffset in 0 until (data.width - overlap)) {
                val index = preIndex + xOffset

                // Create pattern
                val pattern = IntArray2D(size, size)
                var patternIndex = 0
                for (y in 0..overlap) {
                    val postIndex = index + y * data.width
                    for (x in 0..overlap) {
                        pattern[patternIndex] = data[postIndex + x]
                        patternIndex++
                    }
                }

                val foundPatterns = mutableListOf(pattern)

                if (allowFlips) {
                    val patternH = pattern.hFlipped()
                    val patternV = pattern.vFlipped()
                    val patternHV = patternH.vFlipped()

                    foundPatterns.addAll(sequenceOf(patternH, patternV, patternHV))
                }

                if (allowRotations) {
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


                    if (allowFlips) {
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

                foundPatterns.forEach { addPattern(indices, it, patterns) }
            }
        }

        return patterns
    }
}