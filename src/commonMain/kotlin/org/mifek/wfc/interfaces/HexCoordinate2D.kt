package org.mifek.wfc.interfaces

/**
 * Flat side up hexagonal grid coordinate
 */
class HexCoordinate2D(val x: Int, val y: Int): Coordinate(2, x, y) {
    val up get() = HexCoordinate2D(x, y-2)
    val down get() = HexCoordinate2D(x, y+2)
    val upperLeft get() = HexCoordinate2D(if (y%2 == 0) x-1 else x, y+1)
    val upperRight get() = HexCoordinate2D(if (y%2 == 0) x else x+1 , y+1)
    val lowerLeft get() = HexCoordinate2D(if (y%2 == 0) x-1 else x, y-1)
    val lowerRight get() = HexCoordinate2D(if (y%2 == 0) x else x+1, y-1)
}