package org.mifek.wfc.interfaces

class Coordinate3D(val x: Int, val y: Int, val z: Int): Coordinate(3, x, y, z) {
    val left get() = Coordinate3D(x-1, y, z)
    val right get() = Coordinate3D(x+1, y, z)
    val under get() = Coordinate3D(x, y-1, z)
    val above get() = Coordinate3D(x, y+1, z)
    val behind get() = Coordinate3D(x, y, z-1)
    val before get() = Coordinate3D(x, y, z+1)
}