package org.mifek.wfc
//
///**
// * Flat side up hexagonal grid coordinate
// */
//class HexCoordinate2D(val x: Int, val y: Int): Coordinate(2, x, y) {
//    inline fun up(height: Int = Int.MAX_VALUE): HexCoordinate2D? {
//        return if (y >= height-2) null else HexCoordinate2D(x, y+2)
//    }
//    inline fun down(): HexCoordinate2D? {
//        return if (y < 2) null else HexCoordinate2D(x, y-2)
//    }
//    inline fun upperLeft(height: Int = Int.MAX_VALUE): HexCoordinate2D? {
//        return if (y >= height-1 || y%2 == 0 && x < 1) null else HexCoordinate2D(if (y%2 == 0) x-1 else x, y+1)
//    }
//    inline fun upperRight(width: Int = Int.MAX_VALUE, height: Int = Int.MAX_VALUE): HexCoordinate2D? {
//        return if (y >= height-1 || y%2 != 0 && x >= width-1) null else HexCoordinate2D(if (y%2 == 0) x else x+1, y+1)
//    }
//    inline fun lowerLeft(): HexCoordinate2D? {
//        return if (y < 1 || y%2 == 0 && x < 1) null else HexCoordinate2D(if (y%2 == 0) x-1 else x, y-1)
//    }
//    inline fun lowerRight(width: Int = Int.MAX_VALUE): HexCoordinate2D? {
//        return if (y < 1 || y%2 != 0 && x >= width-1) null else HexCoordinate2D(if (y%2 == 0) x else x+1, y-1)
//    }
//
//    override fun serialize(vararg size: Int): Int {
//        if(size.size != dimension) {
//            throw Error("Number of parameters (${size.size}) must equal set dimension ($dimension).")
//        }
//
//        return y * size[0] + x
//    }
//
//    override fun onBoundary(vararg size: Int): Boolean {
//        return x == 0 || y == 0 || x == size[0]-1 || y == size[1]-1
//    }
//
//    override fun neighbourIterator(vararg size: Int): Sequence<Coordinate> {
//        if(size.size != dimension) {
//            throw Error("Mismatch in number of dimensions. Expected $dimension sizes but got ${size.size}")
//        }
//
//        return hexNeighbourIterator2D(size[0], size[1])
//    }
//
//    fun hexNeighbourIterator2D(width: Int = Int.MAX_VALUE, height: Int = Int.MAX_VALUE): Sequence<HexCoordinate2D> {
//        return sequence {
//            val u = up(height)
//            if (u != null) yield(u)
//            val ur = upperRight(width, height)
//            if (ur != null) yield(ur)
//            val lr = lowerRight(width)
//            if (lr != null) yield(lr)
//            val d = down()
//            if(d != null) yield(d)
//            val ll = lowerLeft()
//            if(ll != null) yield(ll)
//            val ul = upperLeft(height)
//            if(ul != null) yield(ul)
//        }
//    }
//}