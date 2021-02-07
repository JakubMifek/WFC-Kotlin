package org.mifek.wfc.datastructures

open class Dequeue<T> : Iterable<T> {
    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var count = 0
    val size get() = count
    val isEmpty get() = count == 0

    constructor(init: Iterable<T>) {
        for(item in init) {
            this.enqueue(item)
        }
    }

    fun enqueue(item: T) {
        if(head == null) {
            head = Node(item)
            tail = head
            count++
            return
        }

        if(head == tail) {
            tail = Node(item)
            tail!!.pred = head
            head!!.succ = tail
            count++
            return
        }

        val node = Node(item)
        tail!!.succ = node
        node.pred = tail
        tail = node
        count++
    }

    fun pushFront(item: T) {
        if(head == null) {
            head = Node(item)
            tail = head
            count++
            return
        }

        if(head == tail) {
            head = Node(item)
            head!!.succ = tail
            tail!!.pred = head
            count++
            return
        }

        val node = Node(item)
        head!!.pred = node
        node.succ = head
        head = node
        count++
    }

    fun dequeue(): T? {
        if(head == null) return null

        val ret = head!!.value

        if(head == tail) {
            head = null
            tail = null
            count--
            return ret
        }

        head = head!!.succ
        count--
        return ret
    }

    fun pop(): T? {
        if(tail == null) return null

        val ret = tail!!.value

        if(head == tail) {
            head = null
            tail = null
            count--
            return ret
        }

        tail = tail!!.pred
        count--
        return ret
    }

    /**
     * Beware O(N) complexity!
     */
    fun remove(item: T): Boolean {
        var curr = head
        while(curr != null) {
            if(curr.value == item) {
                // Extract from line
                if(curr.pred != null) {
                    curr.pred!!.succ = curr.succ
                }
                if(curr.succ != null) {
                    curr.succ!!.pred = curr.pred
                }

                // Fix pointers
                if(curr == head) {
                    head = curr.succ
                }
                if(curr == tail) {
                    tail = curr.pred
                }

                // Remove successful
                count--
                return true
            }

            curr = curr.succ
        }

        return false
    }

    override fun iterator(): Iterator<T> {
        return iterator {
            var next = head
            while (next != null) {
                yield(next.value)
                next = next.succ
            }
        }
    }
}