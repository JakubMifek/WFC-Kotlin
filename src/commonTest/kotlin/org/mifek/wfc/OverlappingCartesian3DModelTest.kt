package org.mifek.wfc

import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.models.OverlappingCartesian2DModel
import org.mifek.wfc.models.OverlappingCartesian3DModel
import org.mifek.wfc.models.options.Cartesian3DModelOptions
import org.mifek.wfc.utils.formatPatterns
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex
import kotlin.math.min
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.asserter

class OverlappingCartesian3DModelTest {
    fun printGrid(grid: IntArray3D, mapping: ((Int) -> Char)? = null) {
        for (d in 0 until grid.depth) {
            for (i in 0 until grid.height) {
                println(grid[null, i, d].joinToString("") { if (mapping != null) mapping(it).toString() else it.toString() })
            }
            println()
        }
    }

    fun createModel(
        source: IntArray3D,
        width: Int,
        height: Int,
        depth: Int,
        overlap: Int,
        options: Cartesian3DModelOptions = Cartesian3DModelOptions(true, true, false, false)
    ): OverlappingCartesian3DModel {
        val model = OverlappingCartesian3DModel(
            source,
            overlap,
            width,
            height,
            depth,
            options
        )
//        var i = 0
//        for (pattern in model.patterns) {
//            println("Pattern ${i++}:")
//            printGrid(IntArray3D(overlap+1, overlap+1, overlap+1) { pattern[it] })
//            {
//                when (it) {
//                    8 -> '⬛'
//                    0 -> '⬜'
//                    else -> "$it"[0]
//                }
//            }
//
//            println()
//        }
//        println(formatPropagator(model.propagator))
        return model
    }

    @ExperimentalUnsignedTypes
    fun simulate(
        source: IntArray3D,
        width: Int,
        height: Int,
        depth: Int,
        overlap: Int,
        seed: Int = Random.Default.nextInt()
    ): IntArray3D {
        val model = createModel(source, width, height, depth, overlap)
        val algorithm = model.build()

//        algorithm.afterClear += {
//            println("\n==========\n")
//            println("Cleared")
//            printGrid(model.constructOutput(algorithm)){
//                when (it) {
//                    8 -> '⬛'
//                    0 -> '⬜'
//                    else -> "$it"[0]
//                }
//            }
//        }
//        algorithm.afterCollapse += {
//            println("\n----------\n")
//            println("Collapsed")
//            printGrid(model.constructOutput(algorithm)){
//                when (it) {
//                    8 -> '⬛'
//                    0 -> '⬜'
//                    else -> "$it"[0]
//                }
//            }
//        }
//        algorithm.afterObserve += {
//            println("\n----------\n")
//            println("Observed ${it.third} on ${it.second}")
//            printGrid(model.constructOutput(algorithm)){
//                when (it) {
//                    8 -> '⬛'
//                    0 -> '⬜'
//                    else -> "$it"[0]
//                }
//            }
//        }
//        algorithm.afterBan += {
//            println("Banning ${it.third} for ${it.second}")
//        }
//        algorithm.afterPropagation += {
//            println("\n----------\n")
//            println("Prop")
//            printGrid(model.constructOutput(algorithm)){
//                when (it) {
//                    8 -> '⬛'
//                    0 -> '⬜'
//                    else -> "$it"[0]
//                }
//            }
//        }

        val result = algorithm.run(seed)
//        val result2 = algorithm.run(1)
        assertTrue(result, "Expected algorithm to be successful. Seed $seed")
//        assertTrue(result2, "Expected algorithm to be successful. Seed 1")
        return model.constructAveragedOutput(algorithm)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun shiftWaves() {
        val d = intArrayOf(
            1, 0, 1,
            0, 1, 0,
            1, 0, 1,

            0, 1, 0,
            1, 0, 1,
            0, 1, 0,

            1, 0, 1,
            0, 1, 0,
            1, 0, 1,
        )
        val source = IntArray3D(3, 3, 3) {
            d[it]
        }
        printGrid(source) {
            when (it) {
                1 -> '⬛'
                0 -> '⬜'
                else -> "$it"[0]
            }
        }

        val model = createModel(
            source, 16, 16, 16, 2
        )

        for (z in 0 until model.topology.depth) {
            for (y in 0 until model.topology.height) {
                for (x in 0 until model.topology.width) {
                    assertEquals(
                        intArrayOf(x, y, z).toIndex(model.outputSizes),
                        model.shiftAlgorithmWave(model.topology.serializeCoordinates(x, y, z))
                    )
                }
            }
        }

        for (z in 0 until model.outputDepth) {
            for (y in 0 until model.outputHeight) {
                for (x in 0 until model.outputWidth) {
                    assertEquals(
                        model.topology.serializeCoordinates(
                            min(model.outputWidth - model.overlap - 1, x),
                            min(model.outputHeight - model.overlap - 1, y),
                            min(model.outputDepth - model.overlap - 1, z)
                        ), model.shiftOutputWave(intArrayOf(x, y, z).toIndex(model.outputSizes)).first
                    )
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
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

        val result = simulate(
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

    @ExperimentalUnsignedTypes
    @Test
    fun simpleCheckersOnCollapse() {
        val d = intArrayOf(1, 0, 0, 1, 0, 1, 1, 0)
        val source = IntArray3D(2, 2, 2) {
            d[it]
        }
        val model = OverlappingCartesian3DModel(source, 1, 4, 4, 4, Cartesian3DModelOptions(periodicInput = true))
        val algorithm = model.build()

        println(formatPatterns(model.patterns.toList().toTypedArray()))

        algorithm.afterCollapse += { event ->
            println("Collapsed ${event.second} to ${event.third}")
            println("Shifted ${model.shiftAlgorithmWave(event.second)}")
            val coordinates = model.shiftAlgorithmWave(event.second).toCoordinates(model.outputSizes)
            println(
                "Coordinates: ${
                    model.shiftAlgorithmWave(event.second).toCoordinates(model.outputSizes).joinToString(", ", "[", "]")
                }"
            )
            println(
                "Pattern: ${
                    IntArray3D(
                        model.overlap + 1,
                        model.overlap + 1,
                        model.overlap + 1
                    ) { model.patterns[event.third][it] }.toIntArray().joinToString(", ", "[", "]")
                }"
            )
            for (i in 0 until 3) {
                assertTrue(coordinates[i] >= 0)
                assertTrue(coordinates[i] + model.overlap < model.outputSizes[i])
            }
        }

        val seed = 377961908
        val result = algorithm.run(seed)
        assertTrue(result, "Expected algorithm to be successful. Seed $seed")
    }

    @ExperimentalUnsignedTypes
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

        var result2 = simulate(
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

    @ExperimentalUnsignedTypes
    @Test
    fun woodenBox() {
        val data = intArrayOf(
            8, 8, 8, 8, 8,
            8, 8, 8, 8, 8,
            8, 8, 8, 8, 8,
            8, 8, 8, 8, 8,
            8, 8, 8, 8, 8,

            8, 8, 8, 8, 8,
            8, 0, 0, 0, 8,
            8, 0, 0, 0, 8,
            8, 0, 0, 0, 8,
            8, 8, 8, 8, 8,

            8, 8, 8, 8, 8,
            8, 0, 0, 0, 8,
            8, 0, 0, 0, 8,
            8, 0, 0, 0, 8,
            8, 8, 8, 8, 8,

            8, 8, 8, 8, 8,
            8, 0, 0, 0, 8,
            8, 0, 0, 0, 8,
            8, 0, 0, 0, 8,
            8, 8, 8, 8, 8,

            8, 8, 8, 8, 8,
            8, 8, 8, 8, 8,
            8, 8, 8, 8, 8,
            8, 8, 8, 8, 8,
            8, 8, 8, 8, 8,
        )
        val source = IntArray3D(5, 5, 5) { data[it] }
        printGrid(source) {
            when (it) {
                8 -> '⬛'
                0 -> '⬜'
                else -> "$it"[0]
            }
        }


        val model = createModel(source, 10, 10, 10, 2)
        val algorithm = model.build()
        algorithm.beforeStart += {
            for (x in 0..1) {
                for (y in 0..1) {
                    println("Setting 8 to [$x,$y,0]")
                    algorithm.setCoordinatePixel(x, y, 0, 8)
                }
            }
        }
        val seed = 12315153
        val result = algorithm.run(seed)
        assertTrue(result, "Expected algorithm to be successful. Seed $seed")
        var result2 = model.constructAveragedOutput(algorithm)
        printGrid(result2) {
            when (it) {
                8 -> '⬛'
                0 -> '⬜'
                else -> "$it"[0]
            }
        }
    }

    @ExperimentalUnsignedTypes
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
        val result2 = simulate(
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

    @ExperimentalUnsignedTypes
    @Test
    fun setPixel() {
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
        val seed = Random.nextInt()
        val source = IntArray3D(4, 4, 3) { data[it] }
        val width = 9
        val height = 9
        val depth = 9
        val overlap = 1

        val model =
            OverlappingCartesian3DModel(source, overlap, width, height, depth)
                .setPixel(4, 4, 4, 2)
                .setPixel(2, 2, 2, 2)
        val algorithm = model.build()

        val result = algorithm.run(seed)
        assertTrue(result, "Expected algorithm to be successful. Seed $seed")

        val result2 = model.constructAveragedOutput(algorithm)
        assertEquals(2, result2[4, 4, 4])
        assertEquals(2, result2[2, 2, 2])
    }
}