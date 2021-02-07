package org.mifek.wfc

import org.mifek.wfc.implementations.Grid2D
import org.mifek.wfc.utils.column
import org.mifek.wfc.utils.row
import kotlin.test.Test
import kotlin.test.assertEquals

class FacadeTest {
    @Test
    fun simpleCheckers() {
        val data = Grid2D(
            6, 6, intArrayOf(
                1, 0, 1, 0, 1, 0,
                0, 1, 0, 1, 0, 1,
                1, 0, 1, 0, 1, 0,
                0, 1, 0, 1, 0, 1,
                1, 0, 1, 0, 1, 0,
                0, 1, 0, 1, 0, 1,
            )
        )

        val result = createImageModel(data, 4, 4, 1, 12345)
        for (i in 0 until result.height) {
            println(result.data.slice(i * result.width until (i + 1) * result.width).joinToString(" "))
        }
        for (i in 0 until 4) {
            assertEquals(2, result.data.row(i, 4).sum())
            assertEquals(2, result.data.column(i, 4).sum())
        }
    }

    fun printGrid(grid: Grid2D) {
        for (i in 0 until grid.height) {
            println(grid.data.slice(i * grid.width until (i + 1) * grid.width).joinToString(" "))
        }
    }

    @Test
    fun complexCheckers() {
        val data = Grid2D(
            6, 6, intArrayOf(
                1, 1, 0, 0, 1, 1,
                1, 1, 0, 0, 1, 1,
                0, 0, 1, 1, 0, 0,
                0, 0, 1, 1, 0, 0,
                1, 1, 0, 0, 1, 1,
                1, 1, 0, 0, 1, 1,
            )
        )

        val result2 = createImageModel(data, 32, 32, 1, 12345)
        printGrid(result2)
    }

    @Test
    fun redDot() {
        val data = Grid2D(
            5, 5, intArrayOf(
                1, 1, 1, 1, 1,
                1, 0, 0, 0, 1,
                1, 0, 2, 0, 1,
                1, 0, 0, 0, 1,
                1, 1, 1, 1, 1,
            )
        )

        val result2 = createImageModel(data, 32, 32, 1, 12345)
        printGrid(result2)
    }
}