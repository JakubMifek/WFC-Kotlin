package org.mifek.wfc.adapters

import java.awt.image.BufferedImage

fun BufferedImage.toIntArray(): IntArray {
    var idx = 0
    var idy = 0
    return IntArray(width * height) {
        val r = getRGB(idx, idy)
        idx++
        if(idx == width) {
            idx = 0
            idy++
        }
        r
    }
}

fun IntArray.toBufferedImage(stride: Int): BufferedImage {
    val height = size / stride
    val ret = BufferedImage(stride, height, BufferedImage.TYPE_INT_ARGB)
    var i = 0
    for(y in 0 until height) {
        for (x in 0 until stride) {
            ret.setRGB(x, y, this[i++])
        }
    }
    return ret
}