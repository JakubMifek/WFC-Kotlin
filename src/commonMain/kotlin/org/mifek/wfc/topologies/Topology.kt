package org.mifek.wfc.topologies

/**
 * Topology
 *
 * @constructor Create empty Topology
 */
interface Topology {
    val periodic: Boolean

    /**
     * Total number of nodes
     */
    val totalSize: Int

    /**
     * Holds maximum number of neighbours of any node
     */
    val maxDegree: Int

    /**
     * Neighbour iterator
     *
     * @param index
     * @return
     */
    fun neighbourIterator(index: Int): Sequence<Pair<Int, Int>>
}