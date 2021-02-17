package org.mifek.wfc.core

import org.mifek.wfc.utils.EventHandler
import org.mifek.wfc.randomIndex
import org.mifek.wfc.topologies.Topology
import org.mifek.wfc.utils.LOG_BASE
import org.mifek.wfc.utils.RANDOM
import kotlin.math.log
import kotlin.random.Random

open class WfcAlgorithm(
    val topology: Topology,
    protected val weights: DoubleArray,
    protected val propagator: Array<Array<IntArray>>
) {
    protected val patternCount = weights.size

    /**
     * Contains option-based boolean array for each pixel
     */
    protected val wavesArray = Array(topology.totalSize) { BooleanArray(patternCount) }
    val waves = Waves(wavesArray)

    /**
     * Used for banning patterns at pixels, for each pattern on each pixel we remember how many compatible pairs are in
     * each direction. If any of the directions gets to 0, we ban the pattern since it becomes incompatible with that
     * neighbour.
     */
    protected val compatible = Array(wavesArray.size) { Array(patternCount) { IntArray(topology.maxDegree) } }

    /**
     * Used for minimum entropy selection.
     */
    protected val weightLogWeights = DoubleArray(patternCount) { weights[it] * log(weights[it], LOG_BASE) }

    /**
     * Used for minimum entropy selection.
     */
    protected val sumOfWeights: Double = weights.sum()

    /**
     * Used for minimum entropy selection.
     */
    protected val sumOfWeightLogWeights: Double = weightLogWeights.sum()

    /**
     * Used for clearing
     */
    protected var startingEntropy: Double = log(sumOfWeights, LOG_BASE) - sumOfWeightLogWeights / sumOfWeights

    /**
     * Help variable for propagate
     */
    protected var stacksize: Int = 0

    /**
     * Pre-initialized stack, there may never be more then waves * patterns elements.
     */
    protected val stack = arrayOfNulls<Pair<Int, Int>?>(wavesArray.size * patternCount)

    /**
     * Used for minimum entropy selection.
     */
    protected val sumsOfOnes = IntArray(wavesArray.size) { weights.size }

    /**
     * Used for minimum entropy selection.
     */
    protected val sumsOfWeights = DoubleArray(wavesArray.size) { sumOfWeights }

    /**
     * Used for minimum entropy selection.
     */
    protected val sumsOfWeightLogWeights = DoubleArray(wavesArray.size) { sumOfWeightLogWeights }

    /**
     * Used for minimum entropy selection.
     */
    protected val entropies = DoubleArray(wavesArray.size) { startingEntropy }

    // EVENTS

    /**
     * Triggered *after* core cleanup.
     */
    val onClear = EventHandler<WfcAlgorithm>()

    /**
     * Triggered *after* wave observation
     */
    val onObserve = EventHandler<WfcAlgorithm>()

    /**
     * Triggered *after* the algorithm fails
     */
    val onFail = EventHandler<WfcAlgorithm>()

    /**
     * Triggered *after* each propagation step (when original stack-size reaches 0)
     */
    val onPropagationStep = EventHandler<WfcAlgorithm>()

    /**
     * Triggered *after* each step of the algorithm (observation + propagation)
     */
    val onStep = EventHandler<WfcAlgorithm>()

    /**
     * Triggered at the end of algorithm run (whatever the result)
     */
    val onFinished = EventHandler<WfcAlgorithm>()

    /**
     * Clears the calculations made in the network
     */
    open fun clear() {
        for (w in wavesArray.indices) {
            for (p in 0 until patternCount) {
                wavesArray[w][p] = true
                for (n in 0 until topology.maxDegree) {
                    compatible[w][p][n] =
                        propagator[(n + topology.maxDegree / 2) % topology.maxDegree][p].size // opposite direction number of same patterns
                }
            }

            sumsOfOnes[w] = weights.size
            sumsOfWeights[w] = sumOfWeights
            sumsOfWeightLogWeights[w] = sumOfWeightLogWeights
            entropies[w] = startingEntropy
        }

        onClear(this)
    }

    /**
     * Bans the pattern in selected wave
     */
    open fun ban(wave: Int, pattern: Int) {
        wavesArray[wave][pattern] = false

        val compatiblePatterns = compatible[wave][pattern]
        for (neighbour in 0 until topology.maxDegree) compatiblePatterns[neighbour] = 0

        stack[stacksize++] = Pair(wave, pattern)

        sumsOfOnes[wave]--
        sumsOfWeights[wave] -= weights[pattern]
        sumsOfWeightLogWeights[wave] -= weightLogWeights[pattern]
        entropies[wave] =
            log(sumsOfWeights[wave], LOG_BASE) - sumsOfWeightLogWeights[wave] / sumsOfWeights[wave]
    }

    /**
     * Returns wave index with lowest entropy
     */
    open fun selectWave(random: Random = RANDOM): Int? {
        var min = Double.MAX_VALUE
        var argmin: Int = -1

        for (waveIndex in wavesArray.indices) {
            val amount = sumsOfOnes[waveIndex]
            if (amount == 0) return null

            val entropy = entropies[waveIndex]
            if (amount > 1 && entropy <= min) {
                val noise = 1e-6 * random.nextDouble()
                if (entropy + noise < min) {
                    min = entropy + noise
                    argmin = waveIndex
                }
            }
        }

        return argmin
    }

    protected fun observePatternUsingWeights(patterns: BooleanArray, random: Random = RANDOM): Int {
        val distribution = DoubleArray(patternCount) { if (patterns[it]) weights[it] else 0.0 }
        return distribution.randomIndex(random)
    }

    /**
     * Observes a pattern in a wave using given random
     */
    open fun observe(random: Random = RANDOM): Boolean? {
        // TODO: Better heuristic integration
        // TODO: Reset bounds to patterns from original image
        val selectedWave = selectWave(random)

        if (selectedWave == null) {
            onFail(this)
            return false
        } else if (selectedWave == -1) {
            return true
        }

        val wavePatterns = wavesArray[selectedWave]
        val observedPattern = observePatternUsingWeights(wavePatterns, random)

        for (patternIndex in 0 until patternCount) {
            if (wavePatterns[patternIndex] != (patternIndex == observedPattern)) {
                ban(selectedWave, patternIndex)
            }
        }

        onObserve(this)

        return null
    }

    /**
     * Forces pattern observation to given index.
     *
     * This function is intended for user-interaction.
     */
    open fun forceObserve(index: Int, pattern: Int): Boolean? {
        val wavePatterns = wavesArray[index]

        if (!wavePatterns[pattern]) return false

        for (patternIndex in 0 until patternCount) {
            if (wavePatterns[patternIndex] != (patternIndex == pattern)) {
                ban(index, patternIndex)
            }
        }

        return null
    }

    /**
     * Propagates consequences of bans
     */
    open fun propagate() {
        var original = stacksize
        while (stacksize > 0) {
            // pop item
            stacksize--
            original--
            val actual = stack[stacksize] ?: continue

            // TODO: Add support for 'periodic' parameter

            // we have banned patternIndex in waveIndex location
            val (waveIndex, patternIndex) = actual
            topology.neighbourIterator(waveIndex).forEach { neighbour ->
                val direction = neighbour.first
                val neighbourIndex = neighbour.second
                val neighbourPatterns = propagator[direction][patternIndex]
                val compatibles = compatible[neighbourIndex]

                for (neighbourPatternIndex in neighbourPatterns) {
                    val optionCompatible = compatibles[neighbourPatternIndex]

                    optionCompatible[direction]--
                    if (optionCompatible[direction] == 0) {
                        ban(neighbourIndex, neighbourPatternIndex)
                    }
                }
            }

            if (original == 0) {
                original = stacksize
                onPropagationStep(this)
            }
        }
    }


    /**
     * Performs single step to the WFC
     */
    open fun step(random: Random = RANDOM): Boolean? {
        val result = observe(random)
        if (result != null) return result
        propagate()
        onStep(this)
        return null
    }

    /**
     * Main loop of WFC algorithm
     */
    open fun run(seed: Int = Random.nextInt(), limit: Int = 0, backtrackLimit: Int = 0): Boolean {
        val random = Random(seed)
        clear()

        // TODO: Allow backtracking if bigger than 0
        if (limit != 0) {
            var l = 0
            while (l < limit) {
                val result = step(random)
                if (result != null) {
                    onFinished(this)
                    return result
                }
                l++
            }
        } else {
            while (true) {
                val result = step(random)
                if (result != null) {
                    onFinished(this)
                    return result
                }
            }
        }

        onFinished(this)
        return true
    }

//    /**
//     * Decodes waves into output patterns, leaves null if not determined yet. Returns null if any wave has no options left.
//     */
//    fun constructOutput(): Array<Int?>? {
//        return Array(topology.totalSize) {
//            val candidates = wavesArray[it]
//            val a = 1
//            val b = 0
//            val sum: Int = candidates.sumOf { item ->
//                when (item) {
//                    true -> a
//                    false -> b
//                }
//            }
//            if (sum == 0) return null
//            if (sum > 1) return null
//            candidates.indexOf(true)
//        }
//    }
}