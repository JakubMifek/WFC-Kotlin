package org.mifek.wfc.interfaces

open class Coordinate {
    val dimension: Int
    val coordinates: IntArray

    constructor(dimension: Int, vararg coordinates: Int) {
        if(coordinates.size != dimension) {
            throw Error("Number of parameters must equal provided dimension.")
        }

        this.dimension = dimension
        this.coordinates = IntArray(dimension) { coordinates[it] }
    }
}