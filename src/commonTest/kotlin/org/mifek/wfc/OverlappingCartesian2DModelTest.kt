package org.mifek.wfc

import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.models.OverlappingCartesian2DModel
import org.mifek.wfc.models.options.Cartesian2DModelOptions
import org.mifek.wfc.utils.formatPatterns
import org.mifek.wfc.utils.toCoordinates
import kotlin.math.floor
import kotlin.random.Random
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
        return model.constructAveragedOutput(algorithm)
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
            IntArray2D(6, 6) { data[it] }, 4, 4, 1, 123456789
        )
        printGrid(result)
        for (i in 0 until 4) {
            assertEquals(2, result.row(i).sum())
            assertEquals(2, result.column(i).sum())
        }
    }

    @ExperimentalUnsignedTypes
    @Test
    fun simpleCheckersOnCollapse() {
        val data = intArrayOf(
            1, 0, 1, 0, 1, 0,
            0, 1, 0, 1, 0, 1,
            1, 0, 1, 0, 1, 0,
            0, 1, 0, 1, 0, 1,
            1, 0, 1, 0, 1, 0,
            0, 1, 0, 1, 0, 1,
        )
        val source = IntArray2D(6, 6) { data[it] }
        val model = OverlappingCartesian2DModel(source, 1, 4, 4)
        val algorithm = model.build()

        println(formatPatterns(model.patterns.toList().toTypedArray()))

        algorithm.afterCollapse += { event ->
            println("Collapsed ${event.second} to ${event.third}")
            println("Shifted ${model.shiftAlgorithmWave(event.second)}")
            val coordinates = model.shiftAlgorithmWave(event.second).toCoordinates(model.outputSizes)
            println(
                "Coordinates: ${
                    coordinates.joinToString(", ", "[", "]")
                }"
            )
            println(
                "Pattern: ${
                    IntArray2D(
                        model.overlap + 1,
                        model.overlap + 1
                    ) { model.patterns[event.third][it] }.toIntArray().joinToString(", ", "[", "]")
                }"
            )
            for (i in 0 until 2) {
                assertTrue(coordinates[i] >= 0)
                assertTrue(coordinates[i] + model.overlap < model.outputSizes[i])
            }
        }

        val seed = 12345
        val result = algorithm.run(seed)
        assertTrue(result, "Expected algorithm to be successful. Seed $seed")
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
            IntArray2D(6, 6) { data[it] }, 16, 16, 1, 0
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
            IntArray2D(5, 5) { data[it] }, 32, 32, 1, 12345
        )
        printGrid(result2)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun setPixel() {
        val data = intArrayOf(
            1, 1, 1, 1, 1,
            1, 0, 0, 0, 1,
            1, 0, 2, 0, 1,
            1, 0, 0, 0, 1,
            1, 1, 1, 1, 1,
        )
        val seed = Random.nextInt()
        val source = IntArray2D(5, 5) { data[it] }
        val width = 9
        val height = 9
        val overlap = 1

        val model = OverlappingCartesian2DModel(source, overlap, width, height).setPixel(4, 4, 2)
        val algorithm = model.build()
        val result = algorithm.run(seed)
        assertTrue(result, "Expected algorithm to be successful. Seed $seed")
        printGrid(model.constructAveragedOutput(algorithm))
    }

    @Test
    fun setPixels() {
        val data = intArrayOf(
            0, 1, 2,
            3, 4, 5,
            6, 7, 8,
        )
        val seed = Random.nextInt()
        val source = IntArray2D(3, 3) { data[it] }
        val width = source.width * 2
        val height = source.height * 2
        val overlap = 1
        val initSize = 3 - overlap

        val model = OverlappingCartesian2DModel(source, overlap, width, height, Cartesian2DModelOptions(
            allowHorizontalFlips = true, allowVerticalFlips = true
        ))

        (0 until initSize ).forEach { y ->
            (0 until initSize ).forEach { x ->
                model.setPixel(x, y, source[x, y])
            }
        }

        val algorithm = model.build()
        val result = algorithm.run(seed)
        printGrid(model.constructAveragedOutput(algorithm))
        assertTrue(result, "Expected algorithm to be successful. Seed $seed ")
    }
}