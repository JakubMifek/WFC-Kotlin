package org.mifek.wfc.interfaces

open abstract class Coordinate {
    val dimension: Int
    val coordinates: IntArray

    constructor(dimension: Int, vararg coordinates: Int) {
        if(coordinates.size != dimension) {
            throw Error("Number of parameters (${coordinates.size}) must equal provided dimension ($dimension).")
        }

        this.dimension = dimension
        this.coordinates = IntArray(dimension) { coordinates[it] }
    }

    constructor(size: IntArray, value: Int) {
        this.dimension = size.size
        this.coordinates = deserializeCoordinates(value, *size)
    }

    override fun toString(): String {
        return coordinates.joinToString(",", "[", "]")
    }

    abstract fun deserializeCoordinates(value: Int, vararg size: Int): IntArray;

    open fun serializeCoordinates(vararg size: Int): Int {
        if(size.size != dimension) {
            throw Error("Number of parameters (${size.size}) must equal set dimension ($dimension).")
        }

        var coord = 0
        for(i in coordinates.indices.reversed()) {
            if (coordinates[i] >= size[i]) {
                throw IndexOutOfBoundsException("Set coordinate (${coordinates[i]}) of ${i+1}th dimension must be lower than given size ${size[i]}")
            }

            coord = coord * size[i] + coordinates[i]
        }

        return coord
    }

    open fun onBoundary(vararg size: Int): Boolean {
        if(size.size != dimension) {
            throw Error("Number of parameters (${size.size}) must equal set dimension ($dimension).")
        }

        for (i in coordinates.indices) {
            if(coordinates[i] == 0 || coordinates[i] == size[i]-1) {
                return true
            }
        }

        return false
    }

    abstract fun neighbourIterator(vararg size: Int): Sequence<Pair<Int, Coordinate>>
}