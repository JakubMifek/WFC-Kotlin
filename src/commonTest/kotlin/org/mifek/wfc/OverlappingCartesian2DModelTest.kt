package org.mifek.wfc

import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.models.OverlappingCartesian2DModel
import org.mifek.wfc.utils.formatPatterns
import kotlin.math.floor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OverlappingCartesian2DModelTest {
    fun printGrid(grid: IntArray2D) {
        for (i in 0 until grid.height) {
            println(grid.data.slice(i * grid.width until (i + 1) * grid.width).joinToString(" "))
        }
    }

    fun createImageModel(source: IntArray2D, width: Int, height: Int, overlap: Int, seed: Int): IntArray2D {
        val model = OverlappingCartesian2DModel(source, overlap, width, height)
        val algorithm = model.build()
//        algorithm.onObserve += {
//            println("\n\n")
//            printGrid(algorithm.constructOutput())
//        }
//        algorithm.onPropagationStep += {
//            println()
//            printGrid(algorithm.constructOutput())
//        }
        val result = algorithm.run(seed)
//        println(formatPatterns(model.patternsArray.map { it.asIntArray() }.toTypedArray()))
        assertTrue(result, "Expected algorithm to be successful. Seed $seed")
        return algorithm.constructOutput()
    }

    @Test
    fun simpleCheckers() {
        val data = intArrayOf(
            1, 0, 1, 0, 1, 0,
            0, 1, 0, 1, 0, 1,
            1, 0, 1, 0, 1, 0,
            0, 1, 0, 1, 0, 1,
            1, 0, 1, 0, 1, 0,
            0, 1, 0, 1, 0, 1,
        )
        val result = createImageModel(
            IntArray2D(6, 6) {data[it]}, 4, 4, 1, 123456789
        )
        for (i in 0 until 4) {
            println(result.data.slice(i * 4 until (i + 1) * 4).joinToString(" "))
        }
        for (i in 0 until 4) {
            assertEquals(2, result.row(i).sum())
            assertEquals(2, result.column(i).sum())
        }
    }

    @Test
    fun complexCheckers() {
        val data = intArrayOf(
            8, 8, 0, 0, 8, 8,
            8, 8, 0, 0, 8, 8,
            0, 0, 8, 8, 0, 0,
            0, 0, 8, 8, 0, 0,
            8, 8, 0, 0, 8, 8,
            8, 8, 0, 0, 8, 8,
        )
        var result2 = createImageModel(
            IntArray2D(6, 6) {data[it]}, 16, 16, 1, 0
        )
//        var last = 0
//        val total = 2000000
//        val step = total / 100.0
//        println("Done $last%")
//        for (i in 0..total) {
//            result2 = createImageModel(
//                intArrayOf(
//                    8, 8, 0, 0, 8, 8,
//                    8, 8, 0, 0, 8, 8,
//                    0, 0, 8, 8, 0, 0,
//                    0, 0, 8, 8, 0, 0,
//                    8, 8, 0, 0, 8, 8,
//                    8, 8, 0, 0, 8, 8,
//                ), 6, 16, 16, 2, i
//            )
//            if((i/step).toInt() != last) {
//                last = (i/step).toInt()
//                println("Done $last%")
//            }
//        }
        printGrid(result2)
    }

    @Test
    fun redDot() {
        val data = intArrayOf(
            1, 1, 1, 1, 1,
            1, 0, 0, 0, 1,
            1, 0, 2, 0, 1,
            1, 0, 0, 0, 1,
            1, 1, 1, 1, 1,
        )
        val result2 = createImageModel(
            IntArray2D(5,5){data[it]}, 32, 32, 1, 12345
        )
        printGrid(result2)
    }
}