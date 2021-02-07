package org.mifek.wfc.implementations

import org.mifek.wfc.datatypes.Directions2D
import org.mifek.wfc.interfaces.Core

class Core2D(
    val width: Int,
    val height: Int,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>
) : Core(width * height, 4, weights, propagator) {
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