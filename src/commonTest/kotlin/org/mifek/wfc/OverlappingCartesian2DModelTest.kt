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

    @ExperimentalUnsignedTypes
    fun createImageModel(source: IntArray2D, width: Int, height: Int, overlap: Int, seed: Int): IntArray2D {
        val model = OverlappingCartesian2DModel(source, overlap, width, height)
        val algorithm = model.build()
        val result = algorithm.run(seed)
        assertTrue(result, "Expected algorithm to be successful. Seed $seed")
        return model.constructOutput(algorithm)
    }

    @ExperimentalUnsignedTypes
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
        printGrid(result)
        for (i in 0 until 4) {
            assertEquals(2, result.row(i).sum())
            assertEquals(2, result.column(i).sum())
        }
    }

    @ExperimentalUnsignedTypes
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
        printGrid(result2)
    }

    @ExperimentalUnsignedTypes
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