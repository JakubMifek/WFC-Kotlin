package org.mifek.wfc

import org.mifek.wfc.topologies.Cartesian3DTopology
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Topology3DTest {
    @Test
    fun centerNeighbours() {
        val topology = Cartesian3DTopology(10, 10, 10, false)
        val coord = intArrayOf(5, 5, 5)
        val sizes = intArrayOf(10, 10, 10)
        val center = coord.toIndex(sizes)
        val centerNeighbours = topology.neighbourIterator(center).toList().map { it.second }
        assertEquals(6, centerNeighbours.size)
        assertTrue(centerNeighbours.contains(center - 1))
        assertTrue(centerNeighbours.contains(center - 10))
        assertTrue(centerNeighbours.contains(center - 100))
        assertTrue(centerNeighbours.contains(center + 1))
        assertTrue(centerNeighbours.contains(center + 10))
        assertTrue(centerNeighbours.contains(center + 100))
        val coordinates = centerNeighbours.map { it.toCoordinates(sizes) }
        for (coordinate in coordinates) {
            assertEquals(1, coord.mapIndexed { i, it -> abs(coordinate[i] - it) }.sum())
        }
    }

    @Test
    fun topLeftBackwardCorner() {
        val topology = Cartesian3DTopology(10, 10, 10, false)
        val coord = intArrayOf(0, 0, 0)
        val sizes = intArrayOf(10, 10, 10)
        val center = coord.toIndex(sizes)
        val centerNeighbours = topology.neighbourIterator(center).toList().map { it.second }
        assertEquals(3, centerNeighbours.size)
        assertTrue(centerNeighbours.contains(center + 1))
        assertTrue(centerNeighbours.contains(center + 10))
        assertTrue(centerNeighbours.contains(center + 100))
        val coordinates = centerNeighbours.map { it.toCoordinates(sizes) }
        for (coordinate in coordinates) {
            assertEquals(1, coord.mapIndexed { i, it -> abs(coordinate[i] - it) }.sum())
        }
    }

    @Test
    fun topLeftForwardCorner() {
        val topology = Cartesian3DTopology(10, 10, 10, false)
        val coord = intArrayOf(0, 0, 9)
        val sizes = intArrayOf(10, 10, 10)
        val center = coord.toIndex(sizes)
        val centerNeighbours = topology.neighbourIterator(center).toList().map { it.second }
        assertEquals(3, centerNeighbours.size)
        assertTrue(centerNeighbours.contains(center + 1))
        assertTrue(centerNeighbours.contains(center + 10))
        assertTrue(centerNeighbours.contains(center - 100))
        val coordinates = centerNeighbours.map { it.toCoordinates(sizes) }
        for (coordinate in coordinates) {
            assertEquals(1, coord.mapIndexed { i, it -> abs(coordinate[i] - it) }.sum())
        }
    }

    @Test
    fun topRightBackwardCorner() {
        val topology = Cartesian3DTopology(10, 10, 10, false)
        val coord = intArrayOf(0, 9, 0)
        val sizes = intArrayOf(10, 10, 10)
        val center = coord.toIndex(sizes)
        val centerNeighbours = topology.neighbourIterator(center).toList().map { it.second }
        assertEquals(3, centerNeighbours.size)
        assertTrue(centerNeighbours.contains(center + 1))
        assertTrue(centerNeighbours.contains(center - 10))
        assertTrue(centerNeighbours.contains(center + 100))
        val coordinates = centerNeighbours.map { it.toCoordinates(sizes) }
        for (coordinate in coordinates) {
            assertEquals(1, coord.mapIndexed { i, it -> abs(coordinate[i] - it) }.sum())
        }
    }

    @Test
    fun bottomLeftBackwardCorner() {
        val topology = Cartesian3DTopology(10, 10, 10, false)
        val coord = intArrayOf(9, 0, 0)
        val sizes = intArrayOf(10, 10, 10)
        val center = coord.toIndex(sizes)
        val centerNeighbours = topology.neighbourIterator(center).toList().map { it.second }
        assertEquals(3, centerNeighbours.size)
        assertTrue(centerNeighbours.contains(center - 1))
        assertTrue(centerNeighbours.contains(center + 10))
        assertTrue(centerNeighbours.contains(center + 100))
        val coordinates = centerNeighbours.map { it.toCoordinates(sizes) }
        for (coordinate in coordinates) {
            assertEquals(1, coord.mapIndexed { i, it -> abs(coordinate[i] - it) }.sum())
        }
    }

    @Test
    fun topRightForwardCorner() {
        val topology = Cartesian3DTopology(10, 10, 10, false)
        val coord = intArrayOf(0, 9, 9)
        val sizes = intArrayOf(10, 10, 10)
        val center = coord.toIndex(sizes)
        val centerNeighbours = topology.neighbourIterator(center).toList().map { it.second }
        assertEquals(3, centerNeighbours.size)
        assertTrue(centerNeighbours.contains(center + 1))
        assertTrue(centerNeighbours.contains(center - 10))
        assertTrue(centerNeighbours.contains(center - 100))
        val coordinates = centerNeighbours.map { it.toCoordinates(sizes) }
        for (coordinate in coordinates) {
            assertEquals(1, coord.mapIndexed { i, it -> abs(coordinate[i] - it) }.sum())
        }
    }

    @Test
    fun bottomLeftForwardCorner() {
        val topology = Cartesian3DTopology(10, 10, 10, false)
        val coord = intArrayOf(9, 0, 9)
        val sizes = intArrayOf(10, 10, 10)
        val center = coord.toIndex(sizes)
        val centerNeighbours = topology.neighbourIterator(center).toList().map { it.second }
        assertEquals(3, centerNeighbours.size)
        assertTrue(centerNeighbours.contains(center - 1))
        assertTrue(centerNeighbours.contains(center + 10))
        assertTrue(centerNeighbours.contains(center - 100))
        val coordinates = centerNeighbours.map { it.toCoordinates(sizes) }
        for (coordinate in coordinates) {
            assertEquals(1, coord.mapIndexed { i, it -> abs(coordinate[i] - it) }.sum())
        }
    }

    @Test
    fun bottomRightBackwardCorner() {
        val topology = Cartesian3DTopology(10, 10, 10, false)
        val coord = intArrayOf(9, 9, 0)
        val sizes = intArrayOf(10, 10, 10)
        val center = coord.toIndex(sizes)
        val centerNeighbours = topology.neighbourIterator(center).toList().map { it.second }
        assertEquals(3, centerNeighbours.size)
        assertTrue(centerNeighbours.contains(center - 1))
        assertTrue(centerNeighbours.contains(center - 10))
        assertTrue(centerNeighbours.contains(center + 100))
        val coordinates = centerNeighbours.map { it.toCoordinates(sizes) }
        for (coordinate in coordinates) {
            assertEquals(1, coord.mapIndexed { i, it -> abs(coordinate[i] - it) }.sum())
        }
    }

    @Test
    fun bottomRightForwardCorner() {
        val topology = Cartesian3DTopology(10, 10, 10, false)
        val coord = intArrayOf(9, 9, 9)
        val sizes = intArrayOf(10, 10, 10)
        val center = coord.toIndex(sizes)
        val centerNeighbours = topology.neighbourIterator(center).toList().map { it.second }
        assertEquals(3, centerNeighbours.size)
        assertTrue(centerNeighbours.contains(center - 1))
        assertTrue(centerNeighbours.contains(center - 10))
        assertTrue(centerNeighbours.contains(center - 100))
        val coordinates = centerNeighbours.map { it.toCoordinates(sizes) }
        for (coordinate in coordinates) {
            assertEquals(1, coord.mapIndexed { i, it -> abs(coordinate[i] - it) }.sum())
        }
    }
}