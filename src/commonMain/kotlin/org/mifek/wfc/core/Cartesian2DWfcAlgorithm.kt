package org.mifek.wfc.core

import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.heuristics.LowestEntropyHeuristic
import org.mifek.wfc.models.Patterns
import org.mifek.wfc.models.Pixels
import org.mifek.wfc.topologies.Cartesian2DTopology

/**
 * Cartesian2d wfc algorithm
 *
 * @property topology2D
 * @property patterns
 * @property pixels
 * @constructor
 *
 * @param weights
 * @param propagator
 */
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
    /**
     * Ban wave patterns
     *
     * @param wave
     * @param patterns
     * @return
     */
    fun banWavePatterns(wave: Int, patterns: Iterable<Int>): Boolean {
        patterns.forEach {
            if (ban(wave, it) == null) return false
        }
        return if (isBatchUpdate) true else propagate()
    }

    /**
     * Ban wave patterns
     *
     * @param waves
     * @param patterns
     * @return
     */
    fun banWavePatterns(waves: Iterable<Int>, patterns: Iterable<Int>): Boolean {
        waves.forEach { wave ->
            patterns.forEach {
                if (ban(wave, it) == null) return false
            }
        }
        return if (isBatchUpdate) true else propagate()
    }

    /**
     * Set wave patterns
     *
     * @param wave
     * @param patterns
     */
    fun setWavePatterns(wave: Int, patterns: Iterable<Int>) =
        this.banWavePatterns(
            wave,
            (0 until this.patterns.size)
                .minus(patterns)
        )

    /**
     * Set wave patterns
     *
     * @param waves
     * @param patterns
     */
    fun setWavePatterns(waves: Iterable<Int>, patterns: Iterable<Int>) =
        this.banWavePatterns(
            waves,
            (0 until this.patterns.size)
                .minus(patterns)
        )

    /**
     * Ban coordinate patterns
     *
     * @param x
     * @param y
     * @param patterns
     */
    fun banCoordinatePatterns(x: Int, y: Int, patterns: Iterable<Int>) =
        this.banWavePatterns(topology2D.serializeCoordinates(x, y), patterns)

    /**
     * Ban coordinate patterns
     *
     * @param waves
     * @param patterns
     */
    fun banCoordinatePatterns(waves: Iterable<Pair<Int, Int>>, patterns: Iterable<Int>) =
        this.banWavePatterns(waves.map { topology2D.serializeCoordinates(it.first, it.second) }, patterns)

    /**
     * Set coordinate patterns
     *
     * @param x
     * @param y
     * @param patterns
     */
    fun setCoordinatePatterns(x: Int, y: Int, patterns: Iterable<Int>) =
        this.setWavePatterns(topology2D.serializeCoordinates(x, y), patterns)

    /**
     * Set coordinate patterns
     *
     * @param waves
     * @param patterns
     */
    fun setCoordinatePatterns(waves: Iterable<Pair<Int, Int>>, patterns: Iterable<Int>) =
        this.setWavePatterns(waves.map { topology2D.serializeCoordinates(it.first, it.second) }, patterns)

    /**
     * Ban wave pixel
     *
     * @param wave
     * @param pixel
     */
    fun banWavePixel(wave: Int, pixel: Int) =
        banWavePatterns(wave, pixels[pixel].asIterable())

    /**
     * Ban wave pixel
     *
     * @param waves
     * @param pixel
     */
    fun banWavePixel(waves: Iterable<Int>, pixel: Int) =
        banWavePatterns(waves, pixels[pixel].asIterable())

    /**
     * Ban coordinate pixel
     *
     * @param x
     * @param y
     * @param pixel
     */
    fun banCoordinatePixel(x: Int, y: Int, pixel: Int) =
        banWavePatterns(topology2D.serializeCoordinates(x, y), pixels[pixel].asIterable())

    /**
     * Ban coordinate pixels
     *
     * @param coordinates
     * @param pixel
     */
    fun banCoordinatePixels(coordinates: Iterable<Pair<Int, Int>>, pixel: Int) =
        banWavePatterns(
            coordinates.map { topology2D.serializeCoordinates(it.first, it.second) },
            pixels[pixel].asIterable()
        )

    /**
     * Set wave pixel
     *
     * @param wave
     * @param pixel
     */
    fun setWavePixel(wave: Int, pixel: Int) =
        setWavePatterns(wave, pixels[pixel].asIterable())

    /**
     * Set wave pixels
     *
     * @param waves
     * @param pixel
     */
    fun setWavePixels(waves: Iterable<Int>, pixel: Int) =
        setWavePatterns(waves, pixels[pixel].asIterable())

    /**
     * Set coordinate pixel
     *
     * @param x
     * @param y
     * @param pixel
     */
    fun setCoordinatePixel(x: Int, y: Int, pixel: Int) =
        setWavePatterns(topology2D.serializeCoordinates(x, y), pixels[pixel].asIterable())

    /**
     * Set coordinate pixels
     *
     * @param coordinates
     * @param pixel
     */
    fun setCoordinatePixels(coordinates: Iterable<Pair<Int, Int>>, pixel: Int) =
        setWavePatterns(
            coordinates.map { topology2D.serializeCoordinates(it.first, it.second) },
            pixels[pixel].asIterable()
        )

    /**
     * Ban wave pixels
     *
     * @param wave
     * @param pixels
     */
    fun banWavePixels(wave: Int, pixels: Iterable<Int>) =
        banWavePatterns(
            wave,
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )

    /**
     * Ban wave pixels
     *
     * @param waves
     * @param pixels
     */
    fun banWavePixels(waves: Iterable<Int>, pixels: Iterable<Int>) =
        banWavePatterns(
            waves,
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )

    /**
     * Ban coordinate pixels
     *
     * @param x
     * @param y
     * @param pixels
     */
    fun banCoordinatePixels(x: Int, y: Int, pixels: Iterable<Int>) =
        banWavePatterns(
            topology2D.serializeCoordinates(x, y),
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )

    /**
     * Ban coordinate pixels
     *
     * @param coordinates
     * @param pixels
     */
    fun banCoordinatePixels(coordinates: Iterable<Pair<Int, Int>>, pixels: Iterable<Int>) =
        banWavePatterns(
            coordinates.map { topology2D.serializeCoordinates(it.first, it.second) },
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )

    /**
     * Set wave pixels
     *
     * @param wave
     * @param pixels
     */
    fun setWavePixels(wave: Int, pixels: Iterable<Int>) =
        setWavePatterns(
            wave,
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )

    /**
     * Set wave pixels
     *
     * @param waves
     * @param pixels
     */
    fun setWavePixels(waves: Iterable<Int>, pixels: Iterable<Int>) =
        setWavePatterns(
            waves,
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )

    /**
     * Set coordinate pixels
     *
     * @param x
     * @param y
     * @param pixels
     */
    fun setCoordinatePixels(x: Int, y: Int, pixels: Iterable<Int>) =
        setWavePatterns(
            topology2D.serializeCoordinates(x, y),
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )

    /**
     * Set coordinate pixels
     *
     * @param coordinates
     * @param pixels
     */
    fun setCoordinatePixels(coordinates: Iterable<Pair<Int, Int>>, pixels: Iterable<Int>) =
        setWavePatterns(
            coordinates.map { topology2D.serializeCoordinates(it.first, it.second) },
            pixels.map { this.pixels[it] }.fold(emptySequence<Int>()) { acc, sequence -> acc.plus(sequence) }
                .asIterable()
        )

    /**
     * Construct output
     *
     * @return
     */
    open fun constructOutput(): IntArray2D {
        if (!this.hasRun) {
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
                        .sumOf { patterns.pixels[it] } / sum
                }
            }
        }
    }
}