package org.mifek.wfc.adapters

import org.mifek.wfc.models.ModelOptions
import kotlin.random.Random

data class ImageAdapterOptions(
    val repeats: Int = 1,
    val overlap: Int = 1,
    val seed: Int = Random.nextInt(),
    val outputScale: Int = 1,
    val outputEachStep: Boolean = false,
    val outputStepsAsAnimation: Boolean = false,
    val outputSuccessOnly: Boolean = false,
    val outputPatterns: Boolean = false,
    val useEveryNthStep: Int = 1,
    val allowRotations: Boolean = false,
    val allowFlips: Boolean = false,
    val periodicOutput: Boolean = false,
    val periodicInput: Boolean = false,
    val grounded: Boolean = false,
    val roofed: Boolean = false,
    val sided: Boolean = false,
) {
    fun toModelOptions(): ModelOptions {
        return ModelOptions(allowRotations, allowFlips, periodicOutput, periodicInput, grounded, roofed, sided)
    }
}
