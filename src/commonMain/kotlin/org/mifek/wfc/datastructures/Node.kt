package org.mifek.wfc.datastructures

/**
 * Node
 *
 * @param T
 * @property value
 * @constructor Create empty Node
 */
class Node<T>(val value: T) {
    var pred: Node<T>? = null
    var succ: Node<T>? = null
}