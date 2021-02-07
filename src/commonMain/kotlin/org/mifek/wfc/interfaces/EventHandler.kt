package org.mifek.wfc.interfaces

class EventHandler<T> {
    private val set = mutableSetOf<(T) -> Unit>()

    fun subscribe(handler: (T) -> Unit) = set.add(handler)
    fun unsubscribe(handler: (T) -> Unit) = set.remove(handler)

    operator fun plusAssign(handler: (T) -> Unit) {
        subscribe(handler)
    }

    operator fun minusAssign(handler: (T) -> Unit) {
        unsubscribe(handler)
    }

    operator fun invoke(data: T) {
        set.forEach { it(data) }
    }
}