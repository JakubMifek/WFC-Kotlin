package org.mifek.wfc.core

import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.heuristics.LowestEntropyHeuristic
import org.mifek.wfc.models.Patterns
import org.mifek.wfc.models.Pixels
import org.mifek.wfc.topologies.Cartesian3DTopology

/**
 * Cartesian3d wfc algorithm
 *
 * @property topology3D
 * @property patterns
 * @property pixels
 * @constructor
 *
 * @param weights
 * @param propagator
 */
open class Cartesian3DWfcAlgorithm(
    private val topology3D: Cartesian3DTopology,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>,
    private val patterns: Patterns,
    private val pixels: Pixels,
) : WfcAlgorithm(
    topology3D,
    weights,
    propagator,
    LowestEntropyHeuristic(patterns.size, topology3D.totalSize, weights)
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
            if (this.ban(wave, it) == null) {
                return false
            }
        }
        return this.propagate()
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
                if (this.ban(wave, it) == null) {
                    return false
                }
            }
        }
        return this.propagate()
    }

    /**
     * Set wave patterns
     *
     * @param waves
     * @param patterns
     * @return
     */
    fun setWavePatterns(waves: Iterable<Int>, patterns: Iterable<Int>): Boolean {
        waves.forEach { wave ->
            (0 until patternCount).minus(patterns).forEach {
                if (this.ban(wave, it) == null) {
                    return false
                }
            }
        }
        return this.propagate()
    }

    /**
     * Set wave patterns
     *
     * @param wave
     * @param patterns
     * @return
     */
    fun setWavePatterns(wave: Int, patterns: Iterable<Int>): Boolean {
        (0 until patternCount).minus(patterns).forEach {
            if (this.ban(wave, it) == null) {
                return false
            }
        }
        return this.propagate()
    }

    /**
     * Ban coordinate patterns
     *
     * @param x
     * @param y
     * @param z
     * @param patterns
     */
    fun banCoordinatePatterns(x: Int, y: Int, z: Int, patterns: Iterable<Int>) {
        this.banWavePatterns(topology3D.serializeCoordinates(x, y, z), patterns)
    }

    /**
     * Ban coordinate patterns
     *
     * @param coordinates
     * @param patterns
     */
    fun banCoordinatePatterns(coordinates: Iterable<Triple<Int, Int, Int>>, patterns: Iterable<Int>) {
        this.banWavePatterns(
            coordinates.map {
                topology3D.serializeCoordinates(it.first, it.second, it.third)
            }, patterns
        )
    }

    /**
     * Set coordinate patterns
     *
     * @param x
     * @param y
     * @param z
     * @param patterns
     */
    fun setCoordinatePatterns(x: Int, y: Int, z: Int, patterns: Iterable<Int>) {
        this.setWavePatterns(topology3D.serializeCoordinates(x, y, z), patterns)
    }

    /**
     * Set coordinate patterns
     *
     * @param coordinates
     * @param patterns
     */
    fun setCoordinatePatterns(coordinates: Iterable<Triple<Int, Int, Int>>, patterns: Iterable<Int>) {
        this.setWavePatterns(
            coordinates.map {
                topology3D.serializeCoordinates(it.first, it.second, it.third)
            }, patterns
        )
    }

    /**
     * Set wave pixel
     *
     * @param wave
     * @param pixel
     */
    fun setWavePixel(wave: Int, pixel: Int) {
        this.setWavePatterns(wave, pixels[pixel].asIterable())
    }

    /**
     * Set wave pixel
     *
     * @param waves
     * @param pixel
     */
    fun setWavePixel(waves: Iterable<Int>, pixel: Int) {
        this.setWavePatterns(waves, pixels[pixel].asIterable())
    }

    /**
     * Ban wave pixel
     *
     * @param wave
     * @param pixel
     */
    fun banWavePixel(wave: Int, pixel: Int) {
        this.banWavePatterns(wave, pixels[pixel].asIterable())
    }

    /**
     * Ban wave pixel
     *
     * @param waves
     * @param pixel
     */
    fun banWavePixel(waves: Iterable<Int>, pixel: Int) {
        this.banWavePatterns(waves, pixels[pixel].asIterable())
    }

    /**
     * Set coordinate pixel
     *
     * @param x
     * @param y
     * @param z
     * @param pixel
     */
    fun setCoordinatePixel(x: Int, y: Int, z: Int, pixel: Int) {
        this.setCoordinatePatterns(x, y, z, pixels[pixel].asIterable())
    }

    /**
     * Set coordinate pixel
     *
     * @param coordinates
     * @param pixel
     */
    fun setCoordinatePixel(coordinates: Iterable<Triple<Int, Int, Int>>, pixel: Int) {
        this.setCoordinatePatterns(coordinates, pixels[pixel].asIterable())
    }

    /**
     * Ban coordinate pixel
     *
     * @param x
     * @param y
     * @param z
     * @param pixel
     */
    fun banCoordinatePixel(x: Int, y: Int, z: Int, pixel: Int) {
        this.banCoordinatePatterns(x, y, z, pixels[pixel].asIterable())
    }

    /**
     * Ban coordinate pixel
     *
     * @param coordinates
     * @param pixel
     */
    fun banCoordinatePixel(coordinates: Iterable<Triple<Int, Int, Int>>, pixel: Int) {
        this.banCoordinatePatterns(coordinates, pixels[pixel].asIterable())
    }

    /**
     * Set wave pixels
     *
     * @param wave
     * @param pixels
     */
    fun setWavePixels(wave: Int, pixels: Iterable<Int>) {
        this.setWavePatterns(wave,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    /**
     * Set wave pixels
     *
     * @param waves
     * @param pixels
     */
    fun setWavePixels(waves: Iterable<Int>, pixels: Iterable<Int>) {
        this.setWavePatterns(waves,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    /**
     * Ban wave pixels
     *
     * @param wave
     * @param pixels
     */
    fun banWavePixels(wave: Int, pixels: Iterable<Int>) {
        this.banWavePatterns(wave,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    /**
     * Ban wave pixels
     *
     * @param waves
     * @param pixels
     */
    fun banWavePixels(waves: Iterable<Int>, pixels: Iterable<Int>) {
        this.banWavePatterns(waves,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    /**
     * Set coordinate pixels
     *
     * @param x
     * @param y
     * @param z
     * @param pixels
     */
    fun setCoordinatePixels(x: Int, y: Int, z: Int, pixels: Iterable<Int>) {
        this.setCoordinatePatterns(x, y, z,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    /**
     * Set coordinate pixels
     *
     * @param coordinates
     * @param pixels
     */
    fun setCoordinatePixels(coordinates: Iterable<Triple<Int, Int, Int>>, pixels: Iterable<Int>) {
        this.setCoordinatePatterns(coordinates,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    /**
     * Ban coordinate pixels
     *
     * @param x
     * @param y
     * @param z
     * @param pixels
     */
    fun banCoordinatePixels(x: Int, y: Int, z: Int, pixels: Iterable<Int>) {
        this.banCoordinatePatterns(x, y, z,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    /**
     * Ban coordinate pixels
     *
     * @param coordinates
     * @param pixels
     */
    fun banCoordinatePixels(coordinates: Iterable<Triple<Int, Int, Int>>, pixels: Iterable<Int>) {
        this.banCoordinatePatterns(coordinates,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    /**
     * Construct output
     *
     * @return
     */
    open fun constructOutput(): IntArray3D {
        if (!this.hasRun) {
            println("WARNING: Algorithm hasn't run yet.")
        }

        return IntArray3D(topology3D.width, topology3D.height, topology3D.depth) { waveIndex ->
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