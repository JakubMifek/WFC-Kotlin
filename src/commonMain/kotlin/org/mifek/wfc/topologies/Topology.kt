package org.mifek.wfc.topologies

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
     * Returns pairs of neighbour's direction and index
     */
    fun neighbourIterator(index: Int): Sequence<Pair<Int, Int>>
}