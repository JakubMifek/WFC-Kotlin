package org.mifek.wfc

import org.mifek.wfc.utils.column
import org.mifek.wfc.utils.row
import kotlin.test.Test
import kotlin.test.assertEquals

class FacadeTest {
    fun printGrid(grid: IntArray, width: Int, height: Int) {
        for (i in 0 until height) {
            println(grid.slice(i * width until (i + 1) * width).joinToString(" "))
        }
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
            ), 6, 4, 4, 1, 12345
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