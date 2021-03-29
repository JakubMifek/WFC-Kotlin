package org.mifek.wfc.topologies

import org.mifek.wfc.datatypes.Direction3D

open class Cartesian3DTopology(val width: Int, val height: Int, val depth: Int, override val periodic: Boolean = false) :
    Topology {
    override val totalSize = width * height * depth
    override val maxDegree = 6

    fun deserializeCoordinates(index: Int): Triple<Int, Int, Int> {
        val rest = index / width
        return Triple(index % width, rest % height, rest / height)
    }

    fun serializeCoordinates(x: Int, y: Int, z: Int): Int {
        return (z * height + y) * width + x
    }

    override fun neighbourIterator(index: Int): Sequence<Pair<Int, Int>> {
        val triple = deserializeCoordinates(index)
        return sequence {
            if (periodic || triple.second > 0 && height > 1) {
                yield(
                    Pair(
                        Direction3D.UP.toInt(),
                        serializeCoordinates(triple.first, (triple.second + height - 1) % height, triple.third)
                    )
                )
            }
            if (periodic || triple.first < width - 1  && width > 1) {
                yield(
                    Pair(
                        Direction3D.RIGHT.toInt(),
                        serializeCoordinates((triple.first + 1) % width, triple.second, triple.third)
                    )
                )
            }
            if (periodic || triple.third < depth - 1 && depth > 1) {
                yield(
                    Pair(
                        Direction3D.FORWARD.toInt(),
                        serializeCoordinates(triple.first, triple.second, (triple.third + 1) % depth)
                    )
                )
            }
            if (periodic || triple.second < height - 1 && height > 1) {
                yield(
                    Pair(
                        Direction3D.DOWN.toInt(),
                        serializeCoordinates(triple.first, (triple.second + 1) % height, triple.third)
                    )
                )
            }
            if (periodic || triple.first > 0 && width > 1) {
                yield(
                    Pair(
                        Direction3D.LEFT.toInt(),
                        serializeCoordinates((triple.first - 1 + width) % width, triple.second, triple.third)
                    )
                )
            }
            if (periodic || triple.third > 0 && depth > 1) {
                yield(
                    Pair(
                        Direction3D.BACKWARD.toInt(),
                        serializeCoordinates(triple.first, triple.second, (triple.third - 1 + depth) % depth)
                    )
                )
            }
        }
    }
}