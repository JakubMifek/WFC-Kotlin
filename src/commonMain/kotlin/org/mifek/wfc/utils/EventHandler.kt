package org.mifek.wfc.utils

class EventHandler<T> {
    protected val set = arrayListOf<(T) -> Unit>()

    fun subscribe(handler: (T) -> Unit) = set.add(handler)
    fun unsubscribe(handler: (T) -> Unit) = set.remove(handler)
    fun includes(handler: (T) -> Unit) = handler in set

    /**
     * Subscribes event handler
     */
    operator fun plusAssign(handler: (T) -> Unit) {
        subscribe(handler)
    }

    /**
     * Unsubscribes event handler
     */
    operator fun minusAssign(handler: (T) -> Unit) {
        unsubscribe(handler)
    }

    operator fun contains(handler: (T) -> Unit): Boolean {
        return handler in set
    }

    operator fun invoke(data: T) {
        set.forEach { it(data) }
    }
}