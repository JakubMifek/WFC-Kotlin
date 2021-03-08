package org.mifek.wfc.datastructures

class PatternsArrayBuilder : MutableList<IntArray> {
    private val patternsList = mutableListOf<Pair<IntArray, IntHolder>>()
    private val indices = mutableMapOf<Int, MutableList<Int>>()
    val patterns: List<Pair<IntArray, IntHolder>>
        get() = patternsList

    class PabIterator(private val pab: PatternsArrayBuilder, private var index: Int) : MutableListIterator<IntArray> {
        val underlyingIterator = pab.patternsList.listIterator()
        override fun hasNext(): Boolean {
            return underlyingIterator.hasNext()
        }

        override fun hasPrevious(): Boolean {
            return underlyingIterator.hasPrevious()
        }

        override fun next(): IntArray {
            return underlyingIterator.next().first
        }

        override fun nextIndex(): Int {
            return underlyingIterator.nextIndex()
        }

        override fun previous(): IntArray {
            return underlyingIterator.previous().first
        }

        override fun previousIndex(): Int {
            return underlyingIterator.previousIndex()
        }

        override fun add(element: IntArray) {
            underlyingIterator.add(Pair(element, IntHolder()))
        }

        override fun remove() {
            underlyingIterator.remove()
        }

        override fun set(element: IntArray) {
            underlyingIterator.set(Pair(element, IntHolder()))
        }

    }

    override val size: Int
        get() = patternsList.size

    private fun hash(element: IntArray): Int = element.contentHashCode()

    override fun contains(element: IntArray): Boolean {
        val hash = hash(element)
        if (hash !in indices) return false
        return indices[hash]!!.any { patternsList[it].first.contentEquals(element) }
    }

    override fun containsAll(elements: Collection<IntArray>): Boolean {
        return elements.all { contains(it) }
    }

    override fun get(index: Int): IntArray {
        return patternsList[index].first
    }

    override fun indexOf(element: IntArray): Int {
        val hash = hash(element)
        if (hash !in indices) return -1
        return indices[hash]!!.first { patternsList[it].first.contentEquals(element) }
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun iterator(): MutableIterator<IntArray> {
        return PabIterator(this, 0)
    }

    override fun lastIndexOf(element: IntArray): Int {
        val hash = hash(element)
        if (hash !in indices) return -1
        return indices[hash]!!.reversed().first { patternsList[it].first.contentEquals(element) }
    }

    override fun listIterator(): MutableListIterator<IntArray> {
        return PabIterator(this, 0)
    }

    override fun listIterator(index: Int): MutableListIterator<IntArray> {
        return PabIterator(this, index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<IntArray> {
        return patternsList.map { it.first }.subList(fromIndex, toIndex).toMutableList()
    }

    override fun add(element: IntArray): Boolean {
        val hash = hash(element)

        // Check whether pattern exists
        if (indices.containsKey(hash)) {

            // If so, try to find it among hashed arguments
            val candidate = indices[hash]!!.find { patternsList[it].first.contentEquals(element) }
            if (candidate != null) {
                // Increase counter if found [We expect this to be the case most often]
                patternsList[candidate].second.item++
                return true
            }

            // Add the pattern if not found
            indices[hash]!!.add(patternsList.size)
            patternsList.add(Pair(element, IntHolder(1)))
            return true
        }

        // Add the pattern and create hash table for
        indices[hash] = arrayListOf(patternsList.size)
        patternsList.add(Pair(element, IntHolder(1)))
        return true
    }

    override fun add(index: Int, element: IntArray) {
        throw NotImplementedError("This function is not available yet. Sorry.")
    }

    override fun addAll(index: Int, elements: Collection<IntArray>): Boolean {
        throw NotImplementedError("This function is not available yet. Sorry.")
    }

    override fun addAll(elements: Collection<IntArray>): Boolean {
        return elements.all { add(it) }
    }

    override fun clear() {
        patternsList.clear()
        indices.clear()
    }

    override fun remove(element: IntArray): Boolean {
        throw NotImplementedError("This function is not available yet. Sorry.")
    }

    override fun removeAll(elements: Collection<IntArray>): Boolean {
        return elements.all { remove(it) }
    }

    override fun removeAt(index: Int): IntArray {
        throw NotImplementedError("This function is not available yet. Sorry.")
    }

    override fun retainAll(elements: Collection<IntArray>): Boolean {
        throw NotImplementedError("This function is not available yet. Sorry.")
    }

    override fun set(index: Int, element: IntArray): IntArray {
        throw NotImplementedError("This function is not available yet. Sorry.")
    }
}