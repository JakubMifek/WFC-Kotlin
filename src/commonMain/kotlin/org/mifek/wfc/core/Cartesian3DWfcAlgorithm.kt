package org.mifek.wfc.core

import org.mifek.wfc.datastructures.IntArray3D
import org.mifek.wfc.heuristics.LowestEntropyHeuristic
import org.mifek.wfc.models.Patterns
import org.mifek.wfc.models.Pixels
import org.mifek.wfc.topologies.Cartesian3DTopology

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
    fun banWavePatterns(wave: Int, patterns: Iterable<Int>) {
        patterns.forEach {
            this.ban(wave, it)
        }
        this.propagate()
    }

    fun banWavePatterns(waves: Iterable<Int>, patterns: Iterable<Int>) {
        waves.forEach { wave ->
            patterns.forEach {
                this.ban(wave, it)
            }
        }
        this.propagate()
    }

    fun setWavePatterns(waves: Iterable<Int>, patterns: Iterable<Int>) {
        waves.forEach { wave ->
            (0 until patternCount).minus(patterns).forEach {
                this.ban(wave, it)
            }
        }
        this.propagate()
    }

    fun setWavePatterns(wave: Int, patterns: Iterable<Int>) {
        (0 until patternCount).minus(patterns).forEach {
            this.ban(wave, it)
        }
        this.propagate()
    }

    fun banCoordinatePatterns(x: Int, y: Int, z: Int, patterns: Iterable<Int>) {
        this.banWavePatterns(topology3D.serializeCoordinates(x, y, z), patterns)
    }

    fun banCoordinatePatterns(coordinates: Iterable<Triple<Int, Int, Int>>, patterns: Iterable<Int>) {
        this.banWavePatterns(
            coordinates.map {
                topology3D.serializeCoordinates(it.first, it.second, it.third)
            }, patterns
        )
    }

    fun setCoordinatePatterns(x: Int, y: Int, z: Int, patterns: Iterable<Int>) {
        this.setWavePatterns(topology3D.serializeCoordinates(x, y, z), patterns)
    }

    fun setCoordinatePatterns(coordinates: Iterable<Triple<Int, Int, Int>>, patterns: Iterable<Int>) {
        this.setWavePatterns(
            coordinates.map {
                topology3D.serializeCoordinates(it.first, it.second, it.third)
            }, patterns
        )
    }

    fun setWavePixel(wave: Int, pixel: Int) {
        this.setWavePatterns(wave, pixels[pixel].asIterable())
    }

    fun setWavePixel(waves: Iterable<Int>, pixel: Int) {
        this.setWavePatterns(waves, pixels[pixel].asIterable())
    }

    fun banWavePixel(wave: Int, pixel: Int) {
        this.banWavePatterns(wave, pixels[pixel].asIterable())
    }

    fun banWavePixel(waves: Iterable<Int>, pixel: Int) {
        this.banWavePatterns(waves, pixels[pixel].asIterable())
    }

    fun setCoordinatePixel(x: Int, y: Int, z: Int, pixel: Int) {
        this.setCoordinatePatterns(x, y, z, pixels[pixel].asIterable())
    }

    fun setCoordinatePixel(coordinates: Iterable<Triple<Int, Int, Int>>, pixel: Int) {
        this.setCoordinatePatterns(coordinates, pixels[pixel].asIterable())
    }

    fun banCoordinatePixel(x: Int, y: Int, z: Int, pixel: Int) {
        this.banCoordinatePatterns(x, y, z, pixels[pixel].asIterable())
    }

    fun banCoordinatePixel(coordinates: Iterable<Triple<Int, Int, Int>>, pixel: Int) {
        this.banCoordinatePatterns(coordinates, pixels[pixel].asIterable())
    }

    fun setWavePixels(wave: Int, pixels: Iterable<Int>) {
        this.setWavePatterns(wave,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    fun setWavePixels(waves: Iterable<Int>, pixels: Iterable<Int>) {
        this.setWavePatterns(waves,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    fun banWavePixels(wave: Int, pixels: Iterable<Int>) {
        this.banWavePatterns(wave,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    fun banWavePixels(waves: Iterable<Int>, pixels: Iterable<Int>) {
        this.banWavePatterns(waves,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    fun setCoordinatePixels(x: Int, y: Int, z: Int, pixels: Iterable<Int>) {
        this.setCoordinatePatterns(x, y, z,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    fun setCoordinatePixels(coordinates: Iterable<Triple<Int, Int, Int>>, pixels: Iterable<Int>) {
        this.setCoordinatePatterns(coordinates,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    fun banCoordinatePixels(x: Int, y: Int, z: Int, pixels: Iterable<Int>) {
        this.banCoordinatePatterns(x, y, z,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    fun banCoordinatePixels(coordinates: Iterable<Triple<Int, Int, Int>>, pixels: Iterable<Int>) {
        this.banCoordinatePatterns(coordinates,
            this.pixels.filter { entry -> entry.key in pixels }
                .fold(emptySequence<Int>()) { acc, entry -> acc.plus(entry.value) }.asIterable()
        )
    }

    /**
     * Constructs output from a wave for overlapping model, returns averages when multiple patterns available
     */
    open fun constructOutput(): IntArray3D {
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