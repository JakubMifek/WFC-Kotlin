package org.mifek.wfc.datastructures

/**
 * Int holder
 *
 * @property item
 * @constructor Create empty Int holder
 */
data class IntHolder(var item: Int = 0) {
    /**
     * Inc
     *
     * @return
     */
    operator fun inc(): IntHolder {
        item++
        return this
    }

    /**
     * Dec
     *
     * @return
     */
    operator fun dec(): IntHolder {
        item--
        return this
    }
}