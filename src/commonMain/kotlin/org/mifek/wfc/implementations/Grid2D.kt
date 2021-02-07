package org.mifek.wfc.implementations

//import org.mifek.wfc.interfaces.OrthogonalGrid
//
//class Grid2D(val width: Int, val height: Int, data: IntArray? = null) : OrthogonalGrid<Coordinate2D>(intArrayOf(width, height), 4, data) {
//    override inline operator fun set(coordinate: Coordinate2D, value: Int) {
//        if(coordinate.y < 0 || coordinate.y >= height || coordinate.x < 0 || coordinate.x >= width) {
//            throw IndexOutOfBoundsException("Given coordinates $coordinate are out of bounds $width, $height.")
//        }
//
//        data[coordinate.serializeCoordinates(width, height)] = value
//    }
//
//    override inline operator fun get(coordinate: Coordinate2D): Int {
//        if(coordinate.y < 0 || coordinate.y >= height || coordinate.x < 0 || coordinate.x >= width) {
//            throw IndexOutOfBoundsException("Given coordinates $coordinate are out of bounds $width, $height.")
//        }
//
//        return data[coordinate.serializeCoordinates(width, height)]
//    }
//
//    override fun deserializeCoordinate(value: Int): Coordinate2D {
//        return Coordinate2D(value % width, value / width)
//    }
//}