package org.mifek.wfc.topologies

import org.mifek.wfc.datatypes.Direction2D

/**
 * Cartesian2d topology
 *
 * @property width
 * @property height
 * @property periodic
 * @constructor Create empty Cartesian2d topology
 */
open class Cartesian2DTopology(val width: Int, val height: Int, override val periodic: Boolean = false) :
    Topology {
    override val totalSize = width * height
    override val maxDegree = 4

    /**
     * Deserialize coordinates
     *
     * @param index
     * @return
     */
    fun deserializeCoordinates(index: Int): Pair<Int, Int> {
        return Pair(index % width, index / width)
    }

    /**
     * Serialize coordinates
     *
     * @param x
     * @param y
     * @return
     */
    fun serializeCoordinates(x: Int, y: Int): Int {
        return y * width + x
    }

    override fun neighbourIterator(index: Int): Sequence<Pair<Int, Int>> {
        return sequence {
            if (periodic || index >= width && height > 1)
                yield(Pair(Direction2D.NORTH.toInt(), (index - width + totalSize) % totalSize))
            if (periodic || (index + 1) % width != 0 && width > 1)
                yield(Pair(Direction2D.EAST.toInt(), if ((index + 1) % width == 0) index + 1 - width else index + 1))
            if (periodic || index < totalSize - width && height > 1)
                yield(Pair(Direction2D.SOUTH.toInt(), (index + width) % totalSize))
            if (periodic || index % width != 0 && width > 1)
                yield(Pair(Direction2D.WEST.toInt(), if (index % width == 0) index - 1 + width else index - 1))
        }
    }
}