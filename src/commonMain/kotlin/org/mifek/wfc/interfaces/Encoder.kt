package org.mifek.wfc.interfaces

interface Encoder<T, C: Coordinate> {
    fun encode(source: T): OrthogonalGrid<C>
    fun decode(grid: OrthogonalGrid<C>): T
}