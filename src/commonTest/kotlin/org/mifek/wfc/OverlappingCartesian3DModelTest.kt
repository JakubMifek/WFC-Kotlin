package org.mifek.wfc

import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.models.OverlappingCartesian3DModel
import org.mifek.wfc.models.options.Cartesian3DModelOptions
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OverlappingCartesian3DModelTest {
    fun printGrid(grid: IntArray3D, mapping: ((Int) -> Char)? = null) {
        for (d in 0 until grid.depth) {
            for (i in 0 until grid.height) {
                println(grid[null, i, d].joinToString("") { if (mapping != null) mapping(it).toString() else it.toString() })
            }
            println()
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun createModel(
        source: IntArray3D,
        width: Int,
        height: Int,
        depth: Int,
        overlap: Int,
        seed: Int = Random.Default.nextInt()
    ): IntArray3D {
        val model = OverlappingCartesian3DModel(
            source,
            overlap,
            width,
            height,
            depth,
            Cartesian3DModelOptions(true, true, false, false)
        )
//        var i = 0
//        for (pattern in model.patterns) {
//            println("Pattern ${i++}:")
//            printGrid(IntArray3D(overlap+1, overlap+1, overlap+1) { pattern[it] })
////            {
////                when (it) {
////                    8 -> '⬛'
////                    0 -> '⬜'
////                    else -> "$it"[0]
////                }
////            }
//
//            println()
//        }
        val algorithm = model.build()
//        algorithm.onObserve += {
//            printGrid(model.constructOutput(algorithm))
//        }
//        algorithm.onPropagationStep += {
//            printGrid(model.constructOutput(algorithm))
//        }
        val result = algorithm.run(seed)
        assertTrue(result, "Expected algorithm to be successful. Seed $seed")
        return model.constructOutput(algorithm)
    }

    @Test
    fun simpleCheckers() {
        val d = intArrayOf(1, 0, 0, 1, 0, 1, 1, 0)
        val source = IntArray3D(2, 2, 2) {
            d[it]
        }
        printGrid(source) {
            when (it) {
                1 -> '⬛'
                0 -> '⬜'
                else -> "$it"[0]
            }
        }

        val result = createModel(
            source, 4, 4, 4, 1, 377961908
        )
        printGrid(result) {
            when (it) {
                1 -> '⬛'
                0 -> '⬜'
                else -> "$it"[0]
            }
        }
        for (i in 0 until 4) {
            for (j in 0 until 2) {
                assertEquals(2, result[null, i, j].sum())
            }
        }
    }

    @Test
    fun complexCheckers() {
        val data = intArrayOf(
            8, 8, 0, 0,
            8, 8, 0, 0,
            0, 0, 8, 8,
            0, 0, 8, 8,

            8, 8, 0, 0,
            8, 8, 0, 0,
            0, 0, 8, 8,
            0, 0, 8, 8,

            0, 0, 8, 8,
            0, 0, 8, 8,
            8, 8, 0, 0,
            8, 8, 0, 0,

            0, 0, 8, 8,
            0, 0, 8, 8,
            8, 8, 0, 0,
            8, 8, 0, 0,
        )
        val source = IntArray3D(4, 4, 4) { data[it] }
        printGrid(source) {
            when (it) {
                8 -> '⬛'
                0 -> '⬜'
                else -> "$it"[0]
            }
        }

        var result2 = createModel(
            source, 8, 8, 8, 2, 0
        )
        printGrid(result2) {
            when (it) {
                8 -> '⬛'
                0 -> '⬜'
                else -> "$it"[0]
            }
        }
    }

    @Test
    fun redDot() {
        val data = intArrayOf(
            1, 1, 1, 1,
            1, 0, 0, 0,
            1, 0, 0, 0,
            1, 0, 0, 0,

            1, 1, 1, 1,
            1, 0, 0, 0,
            1, 0, 2, 0,
            1, 0, 0, 0,

            1, 1, 1, 1,
            1, 0, 0, 0,
            1, 0, 0, 0,
            1, 0, 0, 0,
        )
        val result2 = createModel(
            IntArray3D(4, 4, 3) { data[it] }, 16, 16, 5, 1, 512312
        )
        printGrid(result2) {
            when (it) {
                2 -> '▣'
                1 -> '⬛'
                0 -> '⬜'
                else -> "$it"[0]
            }
        }
    }
}