package org.mifek.wfc.topologies

import org.mifek.wfc.utils.product
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex

open class CartesianNDTopology(val sizes: IntArray, override val periodic: Boolean = false) :
    Topology {
    val dimension = sizes.size
    override val totalSize = sizes.product()
    override val maxDegree = 2 * sizes.size

    override fun neighbourIterator(index: Int): Sequence<Pair<Int, Int>> {
        val coords = index.toCoordinates(sizes)
        return sequence {
            var dir = 0
            for (i in sizes.indices) {
                if (periodic || coords[i] > 0 && sizes[i] > 1) {
                    yield(
                        Pair(
                            dir,
                            IntArray(coords.size) {
                                if (it != i) coords[it] else (coords[it] - 1 + sizes[i]) % sizes[i]
                            }.toIndex(sizes)
                        )
                    )
                }
                dir++
            }
            for (i in sizes.indices) {
                if (periodic || coords[i] < sizes[i] - 1 && sizes[i] > 1) {
                    yield(
                        Pair(
                            dir,
                            IntArray(coords.size) {
                                if (it != i) coords[it] else (coords[it] + 1 + sizes[i]) % sizes[i]
                            }.toIndex(sizes)
                        )
                    )
                }
                dir++
            }
        }
    }
}