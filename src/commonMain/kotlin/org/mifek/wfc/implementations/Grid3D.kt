package org.mifek.wfc.implementations
//
//class Grid3D(val width: Int, val height: Int, val depth: Int, data: IntArray? = null) : OrthogonalGrid(3, intArrayOf(width, height, depth), 6, data) {
//    inline operator fun set(coordinate: Coordinate3D, value: Int) {
//        if(coordinate.y < 0 || coordinate.y >= height || coordinate.x < 0 || coordinate.x >= width) {
//            throw IndexOutOfBoundsException("Given coordinates $coordinate are out of bounds $width, $height, $depth.")
//        }
//
//        data[coordinate.serialize(width, height, depth)] = value
//    }
//
//    inline operator fun get(coordinate: Coordinate3D): Int {
//        if(coordinate.z < 0 || coordinate.z >= depth || coordinate.y < 0 || coordinate.y >= height || coordinate.x < 0 || coordinate.x >= width) {
//            throw IndexOutOfBoundsException("Given coordinates $coordinate are out of bounds $width, $height, $depth.")
//        }
//
//        return data[coordinate.serialize(width, height, depth)]
//    }
//}