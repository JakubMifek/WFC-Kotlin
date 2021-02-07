package org.mifek.wfc.implementations

import org.mifek.wfc.interfaces.Coordinate

class Coordinate2D(val x: Int, val y: Int): Coordinate(2, x, y) {
    inline fun left(): Coordinate2D? { return if (x == 0) null else Coordinate2D(x - 1, y) }
    inline fun right(width: Int = Int.MAX_VALUE): Coordinate2D? { return if (x == width-1) null else Coordinate2D(x + 1, y) }
    inline fun under(): Coordinate2D? { return if (y == 0) null else Coordinate2D(x, y - 1) }
    inline fun above(height: Int = Int.MAX_VALUE): Coordinate2D? { return if (y == height-1) null else Coordinate2D(x, y + 1) }

    override fun serializeCoordinates(vararg size: Int): Int {
        if(size.size != dimension) {
            throw Error("Number of parameters (${size.size}) must equal set dimension ($dimension).")
        }

        return y * size[0] + x
    }

    override fun deserializeCoordinates(value: Int, vararg size: Int): IntArray {
        return intArrayOf(value % size[0], value / size[0])
    }

    override fun onBoundary(vararg size: Int): Boolean {
        return x == 0 || y == 0 || x == size[0]-1 || y == size[1]-1
    }

    override fun neighbourIterator(vararg size: Int): Sequence<Pair<Int, Coordinate2D>> {
        if(size.size != dimension) {
            throw Error("Mismatch in number of dimensions. Expected $dimension sizes but got ${size.size}")
        }

        return neighbourIterator2D(size[0], size[1])
    }

    fun neighbourIterator2D(width: Int = Int.MAX_VALUE, height: Int = Int.MAX_VALUE): Sequence<Pair<Int, Coordinate2D>> {
        return sequence {
            val a = above(height)
            if (a != null) yield(Pair(0, a))
            val r = right(width)
            if (r != null) yield(Pair(1, r))
            val u = under()
            if(u != null) yield(Pair(2, u))
            val l = left()
            if(l != null) yield(Pair(3, l))
        }
    }
}