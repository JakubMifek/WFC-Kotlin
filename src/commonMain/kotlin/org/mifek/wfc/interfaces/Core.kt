package org.mifek.wfc.interfaces

import org.mifek.wfc.randomIndex
import org.mifek.wfc.utils.LOG_BASE
import org.mifek.wfc.utils.RANDOM
import kotlin.math.log
import kotlin.random.Random

abstract class Core(
    override val totalSize: Int,
    override val maxDegree: Int,
    protected val weights: DoubleArray,
    protected val propagator: Array<Array<IntArray>>
) : Network {
    protected  val patternCount = weights.size
    protected val waves = Array(totalSize) { BooleanArray(patternCount) }
    protected val compatible = Array(waves.size) { Array(patternCount) { IntArray(maxDegree) } }
    protected val weightLogWeights = DoubleArray(patternCount) { weights[it] * log(weights[it], LOG_BASE) }

    protected val sumOfWeights: Double = weights.sum()
    protected val sumOfWeightLogWeights: Double = weightLogWeights.sum()
    protected var startingEntropy: Double = log(sumOfWeights, LOG_BASE) - sumOfWeightLogWeights / sumOfWeights
    protected var stacksize: Int = 0

    protected val stack = arrayOfNulls<Pair<Int, Int>?>(waves.size * patternCount)
    protected val sumsOfOnes = IntArray(waves.size) { weights.size }
    protected val sumsOfWeights = DoubleArray(waves.size) { sumOfWeights }
    protected val sumsOfWeightLogWeights = DoubleArray(waves.size) { sumOfWeightLogWeights }
    protected val entropies = DoubleArray(waves.size) { startingEntropy }

    val onClear = EventHandler<Array<BooleanArray>>()
    val onObserve = EventHandler<Array<BooleanArray>>()
    val onFail = EventHandler<Array<BooleanArray>>()
    val onPropagationStep = EventHandler<Array<BooleanArray>>()
    val onStep = EventHandler<Array<BooleanArray>>()

    /**
     * Clears the calculations made in the network
     */
    open fun clear() {
        for (w in waves.indices) {
            for (p in 0 until patternCount) {
                waves[w][p] = true
                for (n in 0 until maxDegree) {
                    compatible[w][p][n] =
                        propagator[(n + maxDegree / 2) % maxDegree][p].size // opposite direction number of same patterns
                }
            }

            sumsOfOnes[w] = weights.size
            sumsOfWeights[w] = sumOfWeights
            sumsOfWeightLogWeights[w] = sumOfWeightLogWeights
            entropies[w] = startingEntropy
        }

        onClear(waves)
    }

    /**
     * Bans the pattern in selected wave
     */
    open fun ban(wave: Int, pattern: Int) {
        waves[wave][pattern] = false

        val compatiblePatterns = compatible[wave][pattern]
        for (neighbour in 0 until maxDegree) compatiblePatterns[neighbour] = 0

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
    open fun lowestEntropy(random: Random = RANDOM): Int? {
        var min = Double.MAX_VALUE
        var argmin: Int = -1

        for (waveIndex in waves.indices) {
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

    open fun observePattern(patterns: BooleanArray, random: Random = RANDOM): Int {
        val distribution = DoubleArray(patternCount) { if (patterns[it]) weights[it] else 0.0 }
        return distribution.randomIndex(random)
    }

    /**
     * Observes a pattern in a wave using given random
     */
    open fun observe(random: Random = RANDOM): Boolean? {
        val argmin = lowestEntropy(random)

        if (argmin == null) {
            onFail(waves)
            return false
        } else if (argmin == -1) {
//            for (wave in waves.indices) for (patternIndex in 0 until patternCount) if (waves[wave][patternIndex]) network[wave] =
//                patternIndex
            return true
        }

        val wavePatterns = waves[argmin]
        val observedPattern = observePattern(wavePatterns, random)

        for (patternIndex in 0 until patternCount) {
            if (wavePatterns[patternIndex] != (patternIndex == observedPattern)) {
                ban(argmin, patternIndex)
            }
        }

        onObserve(waves)

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

            // we have banned patternIndex in waveIndex location
            val (waveIndex, pattern) = actual
            neighbourIterator(waveIndex).forEach { neighbour ->
                val direction = neighbour.first
                val neighbour = neighbour.second
                val options = propagator[direction][pattern]
                val compatibles = compatible[neighbour]

                for (option in options) {
                    val optionCompatible = compatibles[option]

                    optionCompatible[direction]--
                    if (optionCompatible[direction] == 0) {
                        ban(neighbour, option)
                    }
                }
            }

            if (original == 0) {
                original = stacksize
                onPropagationStep(waves)
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
        onStep(waves)
        return null
    }

    /**
     * Main loop of WFC algorithm
     */
    open fun run(seed: Int = Random.nextInt(), limit: Int = 0, backstepLimit: Int = 0): Boolean? {
        val random = Random(seed)
        clear()

        // TODO: Allow backstepping if bigger than 0
        if (limit != 0) {
            var l = 0
            while (l < limit) {
                val result = step(random)
                if (result != null) return result
                l++
            }
        } else {
            while (true) {
                val result = step(random)
                if (result != null) return result
            }
        }

        return true
    }

    /**
     * Decodes waves into output pixels, leaves null if not determined yet. Returns null if any wave has no options left.
     */
    open fun constructOutput(patterns: Array<IntArray>): Array<Int?>? {
        return Array(totalSize) {
            val candidates = waves[it]
            val a = 1
            val b = 0
            val sum: Int = candidates.sumOf { item ->
                when (item) {
                    true -> a
                    false -> b
                }
            }
            if (sum == 0) return null
            if (sum > 1) null
            patterns[candidates.indexOf(true)][0]
        }
    }
}