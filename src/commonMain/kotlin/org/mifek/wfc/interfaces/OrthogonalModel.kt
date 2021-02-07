package org.mifek.wfc.interfaces

import org.mifek.wfc.randomIndex
import kotlin.math.log
import kotlin.random.Random

// NOTE: We could have wave in grid - would make things a lot cleaner
// NOTE: We should look into some generic interface for grid - then we could generalize model
// NOTE: Good approach might be model builder class
class OrthogonalModel<T : Coordinate>(
    private val grid: OrthogonalGrid<T>,
    private val patternCount: Int,
    private val weights: DoubleArray,
    private val propagator: Array<Array<IntArray>>
) {
    companion object {
        const val LOG_BASE: Double = 2.0
    }

    private val wave: Array<BooleanArray> = Array(grid.totalSize) { BooleanArray(patternCount) }
    private val compatible: Array<Array<IntArray>> =
        Array(wave.size) { Array(patternCount) { IntArray(grid.neighbours) } }
    private val weightLogWeights = DoubleArray(patternCount) { weights[it] * log(weights[it], 2.0) }

    private val sumOfWeights: Double = weights.sum()
    private val sumOfWeightLogWeights: Double = weightLogWeights.sum()
    private var startingEntropy: Double = log(sumOfWeights, LOG_BASE) - sumOfWeightLogWeights / sumOfWeights
    private var stacksize: Int = 0

    private val stack: Array<Pair<Int, Int>?> =
        arrayOfNulls<Pair<Int, Int>?>(wave.size * patternCount) as  Array<Pair<Int, Int>?>
    private val sumsOfOnes: IntArray = IntArray(wave.size) { weights.size }
    private val sumsOfWeights: DoubleArray = DoubleArray(wave.size) { sumOfWeights }
    private val sumsOfWeightLogWeights: DoubleArray = DoubleArray(wave.size) { sumOfWeightLogWeights }
    private val entropies: DoubleArray = DoubleArray(wave.size) { startingEntropy }

    fun observe(random: Random): Boolean? {
        var min = Double.MAX_VALUE
        var argmin: Int = -1

        // Find wave index with lowest entropy - the best point to observe - TODO: make this into a function so that it can be overloaded with custom heuristic
        for (waveIndex in wave.indices) {
            val amount = sumsOfOnes[waveIndex]
            if (amount == 0) return false

            val entropy = entropies[waveIndex]
            if (amount > 1 && entropy <= min) {
                val noise = 1e-6 * random.nextDouble()
                if (entropy + noise < min) {
                    min = entropy + noise
                    argmin = waveIndex
                }
            }
        }

        if (argmin == -1) {
            for (waveIndex in wave.indices) for (patternIndex in 0 until patternCount) if (wave[waveIndex][patternIndex]) grid[waveIndex] =
                patternIndex
            return true
        }

//        println("Argmin $argmin")

        val wavePatterns = wave[argmin]
        val distribution = DoubleArray(patternCount) { if (wavePatterns[it]) weights[it] else 0.0 }
        val observedPattern = distribution.randomIndex(random)

//        println("Observed pattern $observedPattern")

        for (patternIndex in 0 until patternCount) {
            if (wavePatterns[patternIndex] != (patternIndex == observedPattern)) ban(argmin, patternIndex)
        }

        return null
    }

    fun propagate() {
        var original = stacksize
        while (stacksize > 0) {
            // pop item
            stacksize--
            original--
            val actual = stack[stacksize] ?: continue
            // we have banned patternIndex in waveIndex location
            val (waveIndex, patternIndex) = actual
            val coordinate = grid.deserializeCoordinate(waveIndex)

            coordinate.neighbourIterator(*grid.size).forEach { neighbour ->
                val index = neighbour.first
                val neighbour = neighbour.second
                // neighbourIndex lies next to patternIndex from the side 'index' [N, E, S, W]
                val neighbourIndex = neighbour.serializeCoordinates(*grid.size)
                // from the side 'index' there are following options available to patternIndex
                val options = propagator[index][patternIndex]
                val compatibles = compatible[neighbourIndex]

                for (option in options) {
                    val optionCompatible = compatibles[option]

                    optionCompatible[index]--
                    if (optionCompatible[index] == 0) ban(neighbourIndex, option)
                }
            }

            if(original == 0) {
//                printWave(wave)
                original = stacksize
            }
        }
    }

    fun run(seed: Int = Random.nextInt(), limit: Int = 0): Boolean {
//        println("Setting up random")
        val random = Random(seed)
//        println("clearing")
        clear()

//        println("Iterating")
        var l = 0
        while (l < limit || limit == 0) {
//            println()
//            println("Iteration $l")
//            println("Observe")
            val result = observe(random)

//            printWave(wave)

            if (result != null) return result

//            println("Propagate")
            propagate()

            l++
        }

        return true
    }

    fun ban(waveIndex: Int, patternIndex: Int) {
//        println("Banned $waveIndex $patternIndex")
        wave[waveIndex][patternIndex] = false

        val compatiblePatterns = compatible[waveIndex][patternIndex]
        for (neighbour in 0 until grid.neighbours) compatiblePatterns[neighbour] = 0

        stack[stacksize++] = Pair(waveIndex, patternIndex)

        sumsOfOnes[waveIndex]--
        sumsOfWeights[waveIndex] -= weights[patternIndex]
        sumsOfWeightLogWeights[waveIndex] -= weightLogWeights[patternIndex]
        entropies[waveIndex] =
            log(sumsOfWeights[waveIndex], LOG_BASE) - sumsOfWeightLogWeights[waveIndex] / sumsOfWeights[waveIndex]
    }

    fun clear() {
        for (w in wave.indices) {
            for (p in 0 until patternCount) {
                wave[w][p] = true
                for (n in 0 until grid.neighbours) {
                    compatible[w][p][n] =
                        propagator[(n + grid.neighbours / 2) % grid.neighbours][p].size // opposite direction number of same patterns
                }
            }

            sumsOfOnes[w] = weights.size
            sumsOfWeights[w] = sumOfWeights
            sumsOfWeightLogWeights[w] = sumOfWeightLogWeights
            entropies[w] = startingEntropy
        }
    }
}