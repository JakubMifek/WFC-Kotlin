package org.mifek.wfc.topologies

import org.mifek.wfc.datatypes.Directions2D

class Cartesian2DTopology(private val width: Int, private val height: Int) : Topology {
    override val totalSize = width * height
    override val maxDegree = 4

    override fun neighbourIterator(index: Int): Sequence<Pair<Int, Int>> {
        return sequence {
            if (index > width)
                yield(Pair(Directions2D.NORTH.toInt(), index - width))
            if ((index + 1) % width != 0)
                yield(Pair(Directions2D.EAST.toInt(), index + 1))
            if (index < totalSize - width)
                yield(Pair(Directions2D.SOUTH.toInt(), index + width))
            if (index % width != 0)
                yield(Pair(Directions2D.WEST.toInt(), index - 1))
        }
    }
}