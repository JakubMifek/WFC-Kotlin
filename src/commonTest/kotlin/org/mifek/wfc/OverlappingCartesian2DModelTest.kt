package org.mifek.wfc

import org.mifek.wfc.models.OverlappingCartesian2DModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OverlappingCartesian2DModelTest {
    fun printGrid(grid: IntArray, width: Int, height: Int) {
        for (i in 0 until height) {
            println(grid.slice(i * width until (i + 1) * width).joinToString(" "))
        }
    }

    fun createImageModel(source: IntArray, stride: Int, width: Int, height: Int, overlap: Int, seed: Int): IntArray {
        val model = OverlappingCartesian2DModel(source, stride, overlap, width, height)
        val algorithm = model.build()
        val result = algorithm.run(seed)
        assertTrue(result, "Expected algorithm to be successful.")
        return algorithm.constructOutput()
    }

    @Test
    fun simpleCheckers() {
        val result = createImageModel(
            intArrayOf(
                1, 0, 1, 0, 1, 0,
                0, 1, 0, 1, 0, 1,
                1, 0, 1, 0, 1, 0,
                0, 1, 0, 1, 0, 1,
                1, 0, 1, 0, 1, 0,
                0, 1, 0, 1, 0, 1,
            ), 6, 4, 4, 1, 123456789
        )
        for (i in 0 until 4) {
            println(result.slice(i * 4 until (i + 1) * 4).joinToString(" "))
        }
        for (i in 0 until 4) {
            assertEquals(2, result.row(i, 4).sum())
            assertEquals(2, result.column(i, 4).sum())
        }
    }

    @Test
    fun complexCheckers() {
        val result2 = createImageModel(
            intArrayOf(
                1, 1, 0, 0, 1, 1,
                1, 1, 0, 0, 1, 1,
                0, 0, 1, 1, 0, 0,
                0, 0, 1, 1, 0, 0,
                1, 1, 0, 0, 1, 1,
                1, 1, 0, 0, 1, 1,
            ), 6, 32, 32, 1, 123456
        )
        printGrid(result2, 32, 32)
    }

    @Test
    fun redDot() {
        val result2 = createImageModel(intArrayOf(
            1, 1, 1, 1, 1,
            1, 0, 0, 0, 1,
            1, 0, 2, 0, 1,
            1, 0, 0, 0, 1,
            1, 1, 1, 1, 1,
        ), 5, 32, 32, 1, 12345)
        printGrid(result2, 32, 32)
    }
}