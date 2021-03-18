package org.mifek.wfc

import org.mifek.wfc.datastructures.IntArray3D
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Array3DFunctionsTest {
    @Test
    fun positiveRotationX() {
        val array = IntArray3D(3, 3, 3) { it + 1 }
        val rotated1 = array.xRotated()
        val rotated2 = rotated1.xRotated()
        val rotated3 = rotated2.xRotated()
        val rotated4 = rotated3.xRotated()
        assertFalse(array.contentEquals(rotated1))
        assertFalse(array.contentEquals(rotated2))
        assertFalse(array.contentEquals(rotated3))
        assertTrue(array.contentEquals(rotated4))
    }

    @Test
    fun negativeRotationX() {
        val array = IntArray3D(3, 3, 3) { it + 1 }
        val rotated1 = array.xNegRotated()
        val rotated2 = rotated1.xNegRotated()
        val rotated3 = rotated2.xNegRotated()
        val rotated4 = rotated3.xNegRotated()
        assertFalse(array.contentEquals(rotated1))
        assertFalse(array.contentEquals(rotated2))
        assertFalse(array.contentEquals(rotated3))
        assertTrue(array.contentEquals(rotated4))
    }

    @Test
    fun positiveRotationY() {
        val array = IntArray3D(3, 3, 3) { it + 1 }
        val rotated1 = array.yRotated()
        val rotated2 = rotated1.yRotated()
        val rotated3 = rotated2.yRotated()
        val rotated4 = rotated3.yRotated()
        assertFalse(array.contentEquals(rotated1))
        assertFalse(array.contentEquals(rotated2))
        assertFalse(array.contentEquals(rotated3))
        assertTrue(array.contentEquals(rotated4))
    }

    @Test
    fun negativeRotationY() {
        val array = IntArray3D(3, 3, 3) { it + 1 }
        val rotated1 = array.yNegRotated()
        val rotated2 = rotated1.yNegRotated()
        val rotated3 = rotated2.yNegRotated()
        val rotated4 = rotated3.yNegRotated()
        assertFalse(array.contentEquals(rotated1))
        assertFalse(array.contentEquals(rotated2))
        assertFalse(array.contentEquals(rotated3))
        assertTrue(array.contentEquals(rotated4))
    }

    @Test
    fun positiveRotationZ() {
        val array = IntArray3D(3, 3, 3) { it + 1 }
        val rotated1 = array.zRotated()
        val rotated2 = rotated1.zRotated()
        val rotated3 = rotated2.zRotated()
        val rotated4 = rotated3.zRotated()
        assertFalse(array.contentEquals(rotated1))
        assertFalse(array.contentEquals(rotated2))
        assertFalse(array.contentEquals(rotated3))
        assertTrue(array.contentEquals(rotated4))
    }

    @Test
    fun negativeRotationZ() {
        val array = IntArray3D(3, 3, 3) { it + 1 }
        val rotated1 = array.zNegRotated()
        val rotated2 = rotated1.zNegRotated()
        val rotated3 = rotated2.zNegRotated()
        val rotated4 = rotated3.zNegRotated()
        assertFalse(array.contentEquals(rotated1))
        assertFalse(array.contentEquals(rotated2))
        assertFalse(array.contentEquals(rotated3))
        assertTrue(array.contentEquals(rotated4))
    }

    @Test
    fun flipZ() {
        val array = IntArray3D(3, 3, 3) { it + 1 }
        val flipped1 = array.flippedZ()
        val flipped2 = flipped1.flippedZ()
        assertFalse(array.contentEquals(flipped1))
        assertTrue(array.contentEquals(flipped2))
    }

    @Test
    fun flipY() {
        val array = IntArray3D(3, 3, 3) { it + 1 }
        val flipped1 = array.flippedY()
        val flipped2 = flipped1.flippedY()
        assertFalse(array.contentEquals(flipped1))
        assertTrue(array.contentEquals(flipped2))
    }

    @Test
    fun flipX() {
        val array = IntArray3D(3, 3, 3) { it + 1 }
        val flipped1 = array.flippedX()
        val flipped2 = flipped1.flippedX()
        assertFalse(array.contentEquals(flipped1))
        assertTrue(array.contentEquals(flipped2))
    }
}
