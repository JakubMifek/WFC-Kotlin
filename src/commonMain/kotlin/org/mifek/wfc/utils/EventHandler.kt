package org.mifek.wfc.utils

class EventHandler<T> {
    protected val set = mutableSetOf<(T) -> Unit>()

    fun subscribe(handler: (T) -> Unit) = set.add(handler)
    fun unsubscribe(handler: (T) -> Unit) = set.remove(handler)

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

    operator fun invoke(data: T) {
        set.forEach { it(data) }
    }
}