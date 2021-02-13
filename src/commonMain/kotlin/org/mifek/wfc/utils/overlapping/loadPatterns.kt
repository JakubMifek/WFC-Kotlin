package org.mifek.wfc.utils.overlapping

import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.hFlip2D
import org.mifek.wfc.rotate2D
import org.mifek.wfc.vFlip2D

/**
 * Adds pattern to accumulator (patterns). Indices used for faster search.
 */
private fun addPattern(
    indices: HashMap<Int, MutableList<Int>>,
    pattern: IntArray,
    patterns: MutableList<Pair<IntArray, IntHolder>>
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
    data: IntArray,
    stride: Int,
    overlap: Int,
    allowRotations: Boolean = true,
    allowFlips: Boolean = true
): ArrayList<Pair<IntArray, IntHolder>> {
    val size = overlap + 1
    val height = data.size / stride
    val patternSize = size * size
    val patterns = ArrayList<Pair<IntArray, IntHolder>>()
    val indices = HashMap<Int, MutableList<Int>>()

    // Go through input image without borders
    for (yOffset in 0 until (height - overlap)) {
        val preIndex = yOffset * stride
        for (xOffset in 0 until (stride - overlap)) {
            val index = preIndex + xOffset

            // Create pattern
            val pattern = IntArray(patternSize)
            var patternIndex = 0
            for (y in 0..overlap) {
                val postIndex = index + y * stride
                for (x in 0..overlap) {
                    pattern[patternIndex] = data[postIndex + x]
                    patternIndex++
                }
            }

            val foundPatterns = mutableListOf(pattern)

            if(allowFlips) {
                val patternH = pattern.hFlip2D(overlap)
                val patternV = pattern.vFlip2D(overlap)
                val patternHV = patternH.vFlip2D(overlap)

                foundPatterns.addAll(sequenceOf(patternH, patternV, patternHV))
            }

            if (allowRotations) {
                val pattern90 = pattern.rotate2D(overlap)
                val pattern180 = pattern90.rotate2D(overlap)
                val pattern270 = pattern180.rotate2D(overlap)

                foundPatterns.addAll(
                    sequenceOf(
                        pattern90,
                        pattern180,
                        pattern270,
                    )
                )


                if(allowFlips) {
                    val pattern90H = pattern90.hFlip2D(overlap)
                    val pattern90V = pattern90.vFlip2D(overlap)
                    val pattern90HV = pattern90H.vFlip2D(overlap)

                    val pattern180H = pattern180.hFlip2D(overlap)
                    val pattern180V = pattern180.vFlip2D(overlap)
                    val pattern180HV = pattern180H.vFlip2D(overlap)

                    val pattern270H = pattern270.hFlip2D(overlap)
                    val pattern270V = pattern270.vFlip2D(overlap)
                    val pattern270HV = pattern270H.vFlip2D(overlap)

                    foundPatterns.addAll(sequenceOf(
                        pattern90H,
                        pattern90V,
                        pattern90HV,
                        pattern180H,
                        pattern180V,
                        pattern180HV,
                        pattern270H,
                        pattern270V,
                        pattern270HV,
                    ))
                }
            }

            foundPatterns.forEach { addPattern(indices, it, patterns) }
        }
    }

    return patterns
}