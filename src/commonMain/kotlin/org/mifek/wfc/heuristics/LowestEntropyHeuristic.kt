package org.mifek.wfc.heuristics

import org.mifek.wfc.core.WfcAlgorithm
import org.mifek.wfc.utils.LOG_BASE
import kotlin.math.log
import kotlin.random.Random

class LowestEntropyHeuristic(
    patternCount: Int,
    wavesCount: Int,
    private val weights: DoubleArray
) : SelectionHeuristic {
    protected val weightLogWeights = DoubleArray(patternCount) { weights[it] * log(weights[it], LOG_BASE) }
    protected val sumOfWeights: Double = weights.sum()
    protected val sumOfWeightLogWeights: Double = weightLogWeights.sum()
    protected var startingEntropy: Double = log(sumOfWeights, LOG_BASE) - sumOfWeightLogWeights / sumOfWeights

    protected val sumsOfOnes = IntArray(wavesCount) { weights.size }
    protected val sumsOfWeights = DoubleArray(wavesCount) { sumOfWeights }
    protected val sumsOfWeightLogWeights = DoubleArray(wavesCount) { sumOfWeightLogWeights }
    protected val entropies = DoubleArray(wavesCount) { startingEntropy }
    protected val onBanLambda = { triple: Triple<WfcAlgorithm, Int, Int> -> onBan(triple.second, triple.third) }
    protected val onClearLambda = { _: WfcAlgorithm -> onClear() }
    protected var random: Random = Random.Default

    override fun initialize(algorithm: WfcAlgorithm, random: Random) {
        this.random = random
        if(onBanLambda !in algorithm.afterBan) {
            algorithm.afterBan += onBanLambda
        }
        if(onClearLambda !in algorithm.afterClear) {
            algorithm.beforeClear += onClearLambda
        }
    }

    override fun select(): Int? {
        var min = Double.MAX_VALUE
        var argmin: Int = -1

        for (waveIndex in sumsOfOnes.indices) {
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

    private fun onBan(wave: Int, pattern: Int) {
        sumsOfOnes[wave]--
        sumsOfWeights[wave] -= weights[pattern]
        sumsOfWeightLogWeights[wave] -= weightLogWeights[pattern]
        entropies[wave] =
            log(sumsOfWeights[wave], LOG_BASE) - sumsOfWeightLogWeights[wave] / sumsOfWeights[wave]
    }

    private fun onClear() {
        for (w in sumsOfOnes.indices) {
            sumsOfOnes[w] = weights.size
            sumsOfWeights[w] = sumOfWeights
            sumsOfWeightLogWeights[w] = sumOfWeightLogWeights
            entropies[w] = startingEntropy
        }
    }
}