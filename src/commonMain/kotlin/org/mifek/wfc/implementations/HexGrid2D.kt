package org.mifek.wfc.implementations
//
//class HexGrid2D(val width: Int, val height: Int, data: IntArray? = null) : OrthogonalGrid(2, intArrayOf(width, height), 6, data) {
//    inline operator fun set(coordinate: Coordinate2D, value: Int) {
//        if(coordinate.y < 0 || coordinate.y >= height || coordinate.x < 0 || coordinate.x >= width) {
//            throw IndexOutOfBoundsException("Given coordinates $coordinate are out of bounds $width, $height.")
//        }
//
//        data[coordinate.serialize(width, height)] = value
//    }
//
//    inline operator fun get(coordinate: Coordinate2D): Int {
//        if(coordinate.y < 0 || coordinate.y >= height || coordinate.x < 0 || coordinate.x >= width) {
//            throw IndexOutOfBoundsException("Given coordinates $coordinate are out of bounds $width, $height.")
//        }
//
//        return data[coordinate.serialize(width, height)]
//    }
//}