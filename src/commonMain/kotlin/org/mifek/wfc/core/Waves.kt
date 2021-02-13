package org.mifek.wfc.core

/**
 * Read-only wrapper around waves array
 */
class Waves(private val waves: Array<BooleanArray>): Iterable<Sequence<Boolean>> {
    val size = waves.size
    val indices = waves.indices
    val lastIndex = waves.lastIndex

    operator fun get(waveIndex: Int, patternIndex: Int): Boolean {
        return waves[waveIndex][patternIndex]
    }

    operator fun get(waveIndex: Int): Sequence<Boolean> {
        return waves[waveIndex].asSequence()
    }

    override fun iterator(): Iterator<Sequence<Boolean>> {
        return iterator {
            for (wave in waves) {
                yield(wave.asSequence())
            }
        }
    }
}