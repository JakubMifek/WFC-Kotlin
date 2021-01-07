package org.mifek.wfc.interfaces

class Coordinate2D(val x: Int, val y: Int): Coordinate(2, x, y) {
    val left get() = Coordinate2D(x-1, y)
    val right get() = Coordinate2D(x+1, y)
    val under get() = Coordinate2D(x, y-1)
    val above get() = Coordinate2D(x, y+1)
}