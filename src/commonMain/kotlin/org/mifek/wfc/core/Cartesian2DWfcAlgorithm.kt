package org.mifek.wfc.core

import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.heuristics.LowestEntropyHeuristic
import org.mifek.wfc.models.Patterns
import org.mifek.wfc.models.Pixels
import org.mifek.wfc.topologies.Cartesian2DTopology

open class Cartesian2DWfcAlgorithm(
    private val topology2D: Cartesian2DTopology,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>,
    private val patterns: Patterns,
    private val pixels: Pixels,
) : WfcAlgorithm(
    topology2D,
    weights,
    propagator,
    LowestEntropyHeuristic(patterns.size, topology2D.totalSize, weights)
) {
    fun banWavePatterns(wave: Int, patterns: Iterable<Int>) {
        patterns.forEach {
            ban(wave, it)
        }
        propagate()
    }

    fun banWavePatterns(waves: Iterable<Int>, patterns: Iterable<Int>) {
        waves.forEach { wave ->
            patterns.forEach {
                ban(wave, it)
            }
        }
        propagate()
    }

    fun setWavePatterns(wave: Int, patterns: Iterable<Int>) {
        this.banWavePatterns(
            wave,
            (0 until this.patterns.size)
                .minus(patterns)
        )
    }

    fun setWavePatterns(waves: Iterable<Int>, patterns: Iterable<Int>) {
        this.banWavePatterns(
            waves,
            (0 until this.patterns.size)
                .minus(patterns)
        )
    }

    fun banCoordinatePatterns(x: Int, y: Int, patterns: Iterable<Int>) {
        this.banWavePatterns(topology2D.serializeCoordinates(x, y), patterns)
    }

    fun banCoordinatePatterns(waves: Iterable<Pair<Int, Int>>, patterns: Iterable<Int>) {
        this.banWavePatterns(waves.map { topology2D.serializeCoordinates(it.first, it.second) }, patterns)
    }

    fun setCoordinatePatterns(x: Int, y: Int, patterns: Iterable<Int>) {
        this.setWavePatterns(topology2D.serializeCoordinates(x, y), patterns)
    }

    fun setCoordinatePatterns(waves: Iterable<Pair<Int, Int>>, patterns: Iterable<Int>) {
        this.setWavePatterns(waves.map { topology2D.serializeCoordinates(it.first, it.second) }, patterns)
    }

    fun banWavePixel(wave: Int, pixel: Int) {
        banWavePatterns(wave, pixels[pixel].asIterable())
    }

    fun banWavePixel(waves: Iterable<Int>, pixel: Int) {
        banWavePatterns(waves, pixels[pixel].asIterable())
    }

    fun banCoordinatePixel(x: Int, y: Int, pixel: Int) {
        banWavePatterns(topology2D.serializeCoordinates(x, y), pixels[pixel].asIterable())
    }

    fun banCoordinatePixels(coordinates: Iterable<Pair<Int, Int>>, pixel: Int) {
        banWavePatterns(
            coordinates.map { topology2D.serializeCoordinates(it.first, it.second) },
            pixels[pixel].asIterable()
        )
    }

    /**
     * Bans all patterns that do not contain given pixel
     */
    fun setWavePixel(wave: Int, pixel: Int) {
        setWavePatterns(wave, pixels[pixel].asIterable())
    }

    /**
     * Bans all patterns that do not contain any of given pixels
     */
    fun setWavePixels(waves: Iterable<Int>, pixel: Int) {
        setWavePatterns(waves, pixels[pixel].asIterable())
    }

    /**
     * Bans all patterns that do not contain given pixel
     */
    fun setCoordinatePixel(x: Int, y: Int, pixel: Int) {
        setWavePatterns(topology2D.serializeCoordinates(x, y), pixels[pixel].asIterable())
    }

    /**
     * Bans all patterns that do not contain any of given pixels
     */
    fun setCoordinatePixels(coordinates: Iterable<Pair<Int, Int>>, pixel: Int) {
        setWavePatterns(
            coordinates.map { topology2D.serializeCoordinates(it.first, it.second) },
            pixels[pixel].asIterable()
        )
    }

    fun banWavePixels(wave: Int, pixels: Iterable<Int>) {
        banWavePatterns(
            wave,
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )
    }

    fun banWavePixels(waves: Iterable<Int>, pixels: Iterable<Int>) {
        banWavePatterns(
            waves,
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )
    }

    fun banCoordinatePixels(x: Int, y: Int, pixels: Iterable<Int>) {
        banWavePatterns(
            topology2D.serializeCoordinates(x, y),
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )
    }

    fun banCoordinatePixels(coordinates: Iterable<Pair<Int, Int>>, pixels: Iterable<Int>) {
        banWavePatterns(
            coordinates.map { topology2D.serializeCoordinates(it.first, it.second) },
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )
    }

    /**
     * Bans all patterns that do not contain any of given pixels
     */
    fun setWavePixels(wave: Int, pixels: Iterable<Int>) {
        setWavePatterns(
            wave,
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )
    }

    /**
     * Bans all patterns that do not contain any of given pixels
     */
    fun setWavePixels(waves: Iterable<Int>, pixels: Iterable<Int>) {
        setWavePatterns(
            waves,
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )
    }

    /**
     * Bans all patterns that do not contain any of given pixels
     */
    fun setCoordinatePixels(x: Int, y: Int, pixels: Iterable<Int>) {
        setWavePatterns(
            topology2D.serializeCoordinates(x, y),
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )
    }

    /**
     * Bans all patterns that do not contain any of given pixels
     */
    fun setCoordinatePixels(coordinates: Iterable<Pair<Int, Int>>, pixels: Iterable<Int>) {
        setWavePatterns(
            coordinates.map { topology2D.serializeCoordinates(it.first, it.second) },
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )
    }

    /**
     * Constructs output from a wave for overlapping model, returns averages when multiple patterns available
     */
    open fun constructOutput(): IntArray2D {
        if(!this.hasRun) {
            println("WARNING: Algorithm hasn't run yet.")
        }

        return IntArray2D(topology2D.width, topology2D.height) { waveIndex ->
            val a = 0
            val b = 1
            val sum = waves[waveIndex].sumOf {
                when (it) {
                    false -> a
                    true -> b
                }
            }
            when (sum) {
                0 -> -123456789
                1 -> patterns.pixels[patterns.pixels.indices.filter { waves[waveIndex, it] }[0]]
                else -> {
                    patterns.indices
                        .filter { waves[waveIndex, it] }
                        .map { patterns.pixels[it] }
                        .sum() / sum
                }
            }
        }
    }
}