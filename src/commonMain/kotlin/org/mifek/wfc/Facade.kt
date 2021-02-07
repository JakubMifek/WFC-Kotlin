package org.mifek.wfc

import org.mifek.wfc.datastructures.IntHolder
import org.mifek.wfc.datatypes.Directions2D
import org.mifek.wfc.implementations.Grid2D
import org.mifek.wfc.interfaces.OrthogonalModel
import org.mifek.wfc.utils.*

fun loadOverlappingPatternsFromImage(inputData: Grid2D, overlap: Int): Pair<Array<IntArray>, DoubleArray> {
    val size = overlap + 1
    val patternSize = size * size
    val patterns = ArrayList<Pair<IntArray, IntHolder>>()
    val indices = HashMap<Int, MutableList<Int>>()

    // Go through input image without borders
    for (yOffset in 0 until inputData.height - overlap) {
        val preIndex = yOffset * inputData.width
        for (xOffset in 0 until inputData.width - overlap) {
            val index = preIndex + xOffset

            // Create pattern
            val pattern = IntArray(patternSize)
            var patternIndex = 0
            for (y in 0..overlap) {
                val postIndex = index + y * inputData.width
                for (x in 0..overlap) {
                    pattern[patternIndex] = inputData[postIndex + x]
                    patternIndex++
                }
            }

            // Store pattern
            val patternH = pattern.hFlip2D(overlap)
            val patternV = pattern.vFlip2D(overlap)
            val patternHV = patternH.vFlip2D(overlap)

            val pattern90 = pattern.rotate2D(overlap)
            val pattern90H = pattern90.hFlip2D(overlap)
            val pattern90V = pattern90.vFlip2D(overlap)
            val pattern90HV = pattern90H.vFlip2D(overlap)

            val pattern180 = pattern90.rotate2D(overlap)
            val pattern180H = pattern180.hFlip2D(overlap)
            val pattern180V = pattern180.vFlip2D(overlap)
            val pattern180HV = pattern180H.vFlip2D(overlap)

            val pattern270 = pattern180.rotate2D(overlap)
            val pattern270H = pattern270.hFlip2D(overlap)
            val pattern270V = pattern270.vFlip2D(overlap)
            val pattern270HV = pattern270H.vFlip2D(overlap)

            arrayOf(
                pattern,
                patternH,
                patternV,
                patternHV,
                pattern90,
                pattern90H,
                pattern90V,
                pattern90HV,
                pattern180,
                pattern180H,
                pattern180V,
                pattern180HV,
                pattern270,
                pattern270H,
                pattern270V,
                pattern270HV,
            ).forEach { addPattern(indices, it, patterns) }
        }
    }

    val sum = patterns.map { it.second.item }.sum()
    val weights = DoubleArray(patterns.size) { patterns[it].second.item / sum.toDouble() }
    return Pair(patterns.map { it.first }.toTypedArray(), weights)
}

fun addPattern(
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

fun agrees2D(pattern1: IntArray, pattern2: IntArray, size: Int, direction: Directions2D, overlap: Int): Boolean {
    val line1 = when (direction) {
//        Directions2D.NORTH -> pattern1.row(0, size)
        Directions2D.NORTH -> pattern1.rows(0 until overlap, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.EAST -> pattern1.columns((size - overlap) until size, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.SOUTH -> pattern1.rows((size - overlap) until size, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.WEST -> pattern1.columns(0 until overlap, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
    }
    val line2 = when (direction) {
        Directions2D.NORTH -> pattern2.rows((size - overlap) until size, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.EAST -> pattern2.columns(0 until overlap, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.SOUTH -> pattern2.rows(0 until overlap, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
        Directions2D.WEST -> pattern2.columns((size - overlap) until size, size).iterator().asSequence()
            .foldIndexed(IntArray(overlap * size), { idx, acc, curr ->
                for (i in curr.indices) {
                    acc[idx * size + i] = curr[i]
                }
                acc
            })
    }
//    println("${line1.joinToString(",")}\t${line2.joinToString(",")}")
    return line1.contentEquals(line2)
}

/**
 * @param overlap Overlapping pixels
 */
fun createImageModel(
    inputData: Grid2D,
    outputWidth: Int,
    outputHeight: Int,
    overlap: Int,
    seed: Int = 123456
): Grid2D {
//    println("size")
    val size = overlap + 1
//    println("grid")
    val grid = Grid2D(outputWidth, outputHeight)
//    println("loading stuff")
    val (patterns, weights) = loadOverlappingPatternsFromImage(inputData, overlap)
    val patternCount = patterns.size
//    println("propagator")
    val propagator = Array(grid.neighbours) { dir ->
        Array(patterns.size) { patternIndex ->
            val d = Directions2D.fromInt(dir)
            patterns.indices.filter {
//                println("Comparing $patternIndex and $it for $d")
                agrees2D(
                    patterns[patternIndex],
                    patterns[it],
                    size,
                    d,
                    overlap
                )
            }.toIntArray()
        }
    }
//    println("Patterns:")
//    println(patterns.mapIndexed { index, it -> "$index:\n${it.joinToString(" ")}\n\t${
//        (0 until 4).joinToString("\n\t") { dir ->
//            propagator[dir][index].joinToString(
//                ", "
//            )
//        }
//    }" }.joinToString("\n\n"))
//    val x = "   "
//    println((0 until patternCount).joinToString(" ") {
//        x.subSequence(0, x.length - it.toString().length).toString() + it.toString()
//    })
//    val template = "00"
//    for (dir in propagator.indices) {
//        println(
//            propagator[dir].joinToString(" ") {
//                when {
//                    it.isEmpty() -> "[X]"
//                    it.size > 1 -> "[?]"
//                    else -> template.slice(
//                        0 until (3 - it[0].toString().length)
//                    ) + it[0].toString()
//                }
//            })
//    }
//    println("smodel")
    val model = OrthogonalModel(grid, patternCount, weights, propagator)
//    println("run")
    val finished = model.run(seed)
    if (finished) {
//        println("done")
        for (i in 0 until grid.totalSize) {
            grid[i] = patterns[grid[i]][0]
        }
    } else {
        println("ugh, fail.")
    }
    return grid
}

//fun printWave(wave: Array<BooleanArray>) {
//    println(" " + wave.indices.joinToString(" ") { idx ->
//        val s = wave[idx].sumBy {
//            when (it) {
//                true -> 1
//                else -> 0
//            }
//        }
//        val r = when (s) {
//            0 -> "[XX]"
//            1 -> "[" + (if (wave[idx].withIndex().find { it.value }!!.index < 10) "0" + wave[idx].withIndex()
//                .find { it.value }!!.index.toString() else wave[idx].withIndex()
//                .find { it.value }!!.index.toString()) + "]"
//            else -> " " + (if (s < 10) " $s" else s.toString()) + " "
//        }
//        if ((idx + 1) % 10 == 0) (r + "\n") else r
//    })
//}