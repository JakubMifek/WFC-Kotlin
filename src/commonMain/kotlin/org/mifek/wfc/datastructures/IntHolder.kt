package org.mifek.wfc.datastructures

data class IntHolder(var item: Int = 0) {
    operator fun inc(): IntHolder {
        item++
        return this
    }
    operator fun dec(): IntHolder {
        item--
        return this
    }
}