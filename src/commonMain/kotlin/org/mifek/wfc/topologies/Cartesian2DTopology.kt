package org.mifek.wfc.topologies

import org.mifek.wfc.datatypes.Directions2D

class Cartesian2DTopology(val width: Int, val height: Int, override val periodic: Boolean = false) :
    Topology {
    override val totalSize = width * height
    override val maxDegree = 4

    override fun neighbourIterator(index: Int): Sequence<Pair<Int, Int>> {
        return sequence {
            if (periodic || index >= width)
                yield(Pair(Directions2D.NORTH.toInt(), (index - width + totalSize) % totalSize))
            if (periodic || (index + 1) % width != 0)
                yield(Pair(Directions2D.EAST.toInt(), if ((index + 1) % width == 0) index + 1 - width else index + 1))
            if (periodic || index < totalSize - width)
                yield(Pair(Directions2D.SOUTH.toInt(), (index + width) % totalSize))
            if (periodic || index % width != 0)
                yield(Pair(Directions2D.WEST.toInt(), if (index % width == 0) index - 1 + width else index - 1))
        }
    }
}