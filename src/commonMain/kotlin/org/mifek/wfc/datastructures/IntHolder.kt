package org.mifek.wfc.datastructures

data class IntHolder(var item: Int = 0) {
    inline operator fun inc(): IntHolder {
        item++
        return this
    }
    inline operator fun dec(): IntHolder {
        item--
        return this
    }
}