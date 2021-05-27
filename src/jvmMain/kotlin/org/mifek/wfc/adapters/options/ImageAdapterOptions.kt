package org.mifek.wfc.adapters.options

import org.mifek.wfc.models.options.Cartesian2DModelOptions
import org.mifek.wfc.models.options.ModelOptions
import kotlin.random.Random

data class ImageAdapterOptions(
    val debugOptions: DebugOptions? = null,
    val modelOptions: Cartesian2DModelOptions = Cartesian2DModelOptions(),
    val outputAnimationOptions: AnimationOptions? = null,
    val outputImageOptions: ImageOptions? = null,
    val overlap: Int = 1,
    val repeats: Int = 1,
    val seed: Int = Random.nextInt(),
    val setPixels: Iterable<Pair<Pair<Int, Int>, Int>>? = null,
    val banPixels: Iterable<Pair<Pair<Int, Int>, Int>>? = null
)
