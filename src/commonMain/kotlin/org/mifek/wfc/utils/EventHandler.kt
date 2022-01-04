package org.mifek.wfc.utils

/**
 * Event handler
 *
 * @param T
 * @constructor Create empty Event handler
 */
class EventHandler<T> {
    protected val set = arrayListOf<(T) -> Unit>()

    /**
     * Subscribe
     *
     * @param handler
     * @receiver
     */
    fun subscribe(handler: (T) -> Unit) = set.add(handler)

    /**
     * Unsubscribe
     *
     * @param handler
     * @receiver
     */
    fun unsubscribe(handler: (T) -> Unit) = set.remove(handler)

    /**
     * Includes
     *
     * @param handler
     * @receiver
     */
    fun includes(handler: (T) -> Unit) = handler in set

    /**
     * Plus assign
     *
     * @param handler
     * @receiver
     */
    operator fun plusAssign(handler: (T) -> Unit) {
        subscribe(handler)
    }

    /**
     * Minus assign
     *
     * @param handler
     * @receiver
     */
    operator fun minusAssign(handler: (T) -> Unit) {
        unsubscribe(handler)
    }

    /**
     * Contains
     *
     * @param handler
     * @receiver
     * @return
     */
    operator fun contains(handler: (T) -> Unit): Boolean {
        return handler in set
    }

    /**
     * Invoke
     *
     * @param data
     */
    operator fun invoke(data: T) {
        set.forEach { it(data) }
    }
}