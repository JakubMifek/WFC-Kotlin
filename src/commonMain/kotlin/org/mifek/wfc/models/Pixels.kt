package org.mifek.wfc.models

class Pixels(private val pixels: Map<Int, IntArray>): Iterable<Pixels.Entry> {
    val size = pixels.size
    val keys = pixels.keys
    val entries = pixels.entries
    val values = pixels.values

    data class Entry(val key: Int, val value: Sequence<Int>)

    operator fun get(index: Int): Sequence<Int> {
//        println("Obtaining patterns for pixel $index.")
//        for(key in pixels.keys) {
//            println("$key: ${pixels[key]?.joinToString(", ")}")
//        }
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