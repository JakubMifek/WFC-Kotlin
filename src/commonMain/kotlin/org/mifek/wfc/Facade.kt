package org.mifek.wfc

import org.mifek.wfc.implementations.Overlapping2DModel

fun createImageModel(
    inputData: IntArray,
    width: Int,
    outputWidth: Int,
    outputHeight: Int,
    overlap: Int,
    seed: Int = 123456
): IntArray {
    val model = Overlapping2DModel(inputData, width, overlap, outputWidth, outputHeight)
    val network = model.build()

//    network.onObserve += { printWave(it) }
//    network.onPropagationStep += { printWave(it)}

    val finished = network.run(seed)
    if (finished == true) {
        val ret = network.constructOutput(model.patterns)!!
        return IntArray(ret.size) { ret[it] ?: 0 }
    } else {
        println("ugh, fail.")
    }
    return IntArray(outputHeight * outputWidth)
}
