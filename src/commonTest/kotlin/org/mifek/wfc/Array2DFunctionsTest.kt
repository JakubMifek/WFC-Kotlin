package org.mifek.wfc

import org.mifek.wfc.utils.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Array2DFunctionsTest {
    @Test
    fun playground() {
    }

    @Test
    fun rgbaSerialization() {
        val rgb = ubyteArrayOf(102u, 103u, 104u, 255u)
        val int = rgbaToInt(rgb[0], rgb[1], rgb[2], rgb[3])
        val rgb2 = intToRgba(int)
        assertTrue(int is Int)
        assertEquals(rgb[0], rgb2.first)
        assertEquals(rgb[1], rgb2.second)
        assertEquals(rgb[2], rgb2.third)
        assertEquals(rgb[3], rgb2.fourth)
    }

    @Test
    fun rgbSerialization() {
        val rgb = ubyteArrayOf(104u, 42u, 231u)
        val int = rgbToInt(rgb[0], rgb[1], rgb[2])
        val rgb2 = intToRgb(int)
        assertTrue(int is Int)
        assertEquals(rgb[0], rgb2.first)
        assertEquals(rgb[1], rgb2.second)
        assertEquals(rgb[2], rgb2.third)
    }

    @Test
    fun rgbaSerializationWithRgbDeserialization() {
        val rgb = ubyteArrayOf(104u, 42u, 231u, 128u)
        val int = rgbaToInt(rgb[0], rgb[1], rgb[2], rgb[3])
        val rgb2 = intToRgb(int)
        assertTrue(int is Int)
        assertEquals(rgb[0], rgb2.first)
        assertEquals(rgb[1], rgb2.second)
        assertEquals(rgb[2], rgb2.third)
    }

    @Test
    fun rgbSerializationWithRgbaDeserialization() {
        val rgb = ubyteArrayOf(104u, 42u, 231u)
        val int = rgbToInt(rgb[0], rgb[1], rgb[2])
        val rgb2 = intToRgba(int)
        assertTrue(int is Int)
        assertEquals(rgb[0], rgb2.first)
        assertEquals(rgb[1], rgb2.second)
        assertEquals(rgb[2], rgb2.third)
        assertEquals(255.toUByte(), rgb2.fourth)
    }

    @Test
    fun columnSelection() {
        val data = (1..25).toList().toIntArray()
        val column1 = data.column(0, 5)
        val column2 = data.column(3, 5)
        assertTrue(column1 is IntArray)
        assertTrue(column1.contentEquals(intArrayOf(1, 6, 11, 16, 21)))
        assertTrue(column2 is IntArray)
        assertTrue(column2.contentEquals(intArrayOf(4, 9, 14, 19, 24)))
    }

    @Test
    fun rowSelection() {
        val data = (1..25).toList().toIntArray()
        val row1 = data.row(0, 5)
        val row2 = data.row(3, 5)
        assertTrue(row1 is IntArray)
        assertTrue(row1.contentEquals(intArrayOf(1, 2, 3, 4, 5)))
        assertTrue(row2 is IntArray)
        assertTrue(row2.contentEquals(intArrayOf(16, 17, 18, 19, 20)))
    }

    @Test
    fun gridRotation() {
        val data = (1..25).toList().toIntArray()
        val rot1 = data.rotate2D(4)
        assertTrue(rot1 is IntArray)
        assertTrue(
            rot1.contentEquals(
                intArrayOf(
                    21, 16, 11, 6, 1,
                    22, 17, 12, 7, 2,
                    23, 18, 13, 8, 3,
                    24, 19, 14, 9, 4,
                    25, 20, 15, 10, 5
                )
            )
        )
        val rot2 = rot1.rotate2D(4)
        assertTrue(rot2 is IntArray)
        assertTrue(
            rot2.contentEquals(
                intArrayOf(
                    25, 24, 23, 22, 21,
                    20, 19, 18, 17, 16,
                    15, 14, 13, 12, 11,
                    10, 9, 8, 7, 6,
                    5, 4, 3, 2, 1
                )
            )
        )
        val rot3 = rot2.rotate2D(4)
        assertTrue(rot3 is IntArray)
        assertTrue(
            rot3.contentEquals(
                intArrayOf(
                    5, 10, 15, 20, 25,
                    4, 9, 14, 19, 24,
                    3, 8, 13, 18, 23,
                    2, 7, 12, 17, 22,
                    1, 6, 11, 16, 21
                )
            )
        )
        val rot4 = rot3.rotate2D(4)
        assertTrue(rot4 is IntArray)
        assertTrue(
            rot4.contentEquals(
                intArrayOf(
                    1, 2, 3, 4, 5,
                    6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15,
                    16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25
                )
            )
        )
    }

    @Test
    fun gridHFlip() {
        val data = (1..25).toList().toIntArray()
        val flip1 = data.hFlip2D(4)
        assertTrue(flip1 is IntArray)
        assertTrue(
            flip1.contentEquals(
                intArrayOf(
                    5, 4, 3, 2, 1,
                    10, 9, 8, 7, 6,
                    15, 14, 13, 12, 11,
                    20, 19, 18, 17, 16,
                    25, 24, 23, 22, 21
                )
            )
        )
        val flip2 = flip1.hFlip2D(4)
        assertTrue(flip2 is IntArray)
        assertTrue(
            flip2.contentEquals(
                intArrayOf(
                    1, 2, 3, 4, 5,
                    6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15,
                    16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25
                )
            )
        )
    }

    @Test
    fun gridVFlip() {
        val data = (1..25).toList().toIntArray()
        val flip1 = data.vFlip2D(4)
        assertTrue(flip1 is IntArray)
        assertTrue(
            flip1.contentEquals(
                intArrayOf(
                    21, 22, 23, 24, 25,
                    16, 17, 18, 19, 20,
                    11, 12, 13, 14, 15,
                    6, 7, 8, 9, 10,
                    1, 2, 3, 4, 5
                )
            )
        )
        val flip2 = flip1.vFlip2D(4)
        assertTrue(flip2 is IntArray)
        assertTrue(
            flip2.contentEquals(
                intArrayOf(
                    1, 2, 3, 4, 5,
                    6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15,
                    16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25
                )
            )
        )
    }
}
