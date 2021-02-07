package org.mifek.wfc.interfaces

import org.mifek.wfc.product

open abstract class OrthogonalGrid<T : Coordinate> {
    val dimension: Int
    val size: IntArray
    val data: IntArray
    val totalSize: Int
    val neighbours: Int

    constructor(size: IntArray, neighbours: Int, data: IntArray? = null) {
        this.dimension = size.size

        if(size.size != dimension) {
            throw Error("Size parameter must have equal length to the given dimension.")
        }
        val totalSize = size.reduce { acc, i -> acc * i }
        if(data != null && data.size != totalSize) {
            throw Error("Given data does not contain right amount of elements. Expected $totalSize but got ${data.size}.")
        }

        this.size = size
        this.neighbours = neighbours
        this.data = data ?: IntArray(totalSize) { 0 }
        this.totalSize = size.product()
    }

    open operator fun get(coordinate: T): Int {
        if(coordinate.coordinates.size != dimension) {
            throw Error("Wrong coordinate used. Number of coordinates (${coordinate.coordinates.size}) must match number of dimensions ($dimension) of the grid.")
        }

        return data[coordinate.serializeCoordinates(*size)]
    }

    open operator fun get(index: Int): Int {
        return data[index]
    }

    open operator fun set(coordinate: T, value: Int) {
        if(coordinate.coordinates.size != dimension) {
            throw Error("Wrong coordinate used. Number of coordinates (${coordinate.coordinates.size}) must match number of dimensions ($dimension) of the grid.")
        }

        data[coordinate.serializeCoordinates(*size)] = value
    }

    open operator fun set(index: Int, value: Int) {
        data[index] = value
    }

    abstract fun deserializeCoordinate(value: Int): T
}