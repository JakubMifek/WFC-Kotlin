package org.mifek.wfc.datastructures

class Node<T>(val value: T) {
    var pred: Node<T>? = null
    var succ: Node<T>? = null
}