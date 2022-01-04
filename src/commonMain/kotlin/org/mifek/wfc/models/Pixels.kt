package org.mifek.wfc.models

/**
 * Pixels
 *
 * @property pixels
 * @constructor Create empty Pixels
 */
class Pixels(private val pixels: Map<Int, IntArray>): Iterable<Pixels.Entry> {
    val size = pixels.size
    val keys = pixels.keys
    val entries = pixels.entries
    val values = pixels.values

    /**
     * Entry
     *
     * @property key
     * @property value
     * @constructor Create empty Entry
     */
    data class Entry(val key: Int, val value: Sequence<Int>)

    /**
     * Get
     *
     * @param index
     * @return
     */
    operator fun get(index: Int): Sequence<Int> {
        return pixels[index]?.asSequence() ?: emptySequence()
    }

    override fun iterator(): Iterator<Entry> {
        return iterator {
            for(item in pixels) {
                yield(Entry(item.key, item.value.asSequence()))
            }
        }
    }
}