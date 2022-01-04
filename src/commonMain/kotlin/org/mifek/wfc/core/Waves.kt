package org.mifek.wfc.core

/**
 * Waves
 *
 * @property waves
 * @constructor Create empty Waves
 */
class Waves(private val waves: Array<BooleanArray>) : Iterable<Sequence<Boolean>> {
    val size = waves.size
    val indices = waves.indices
    val lastIndex = waves.lastIndex

    /**
     * Get
     *
     * @param waveIndex
     * @param patternIndex
     * @return
     */
    operator fun get(waveIndex: Int, patternIndex: Int): Boolean {
        return waves[waveIndex][patternIndex]
    }

    /**
     * Get
     *
     * @param waveIndex
     * @return
     */
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