package org.mifek.wfc.utils

import org.mifek.wfc.datastructures.Quadruple

/**
 * Rgb to int
 *
 * @param r
 * @param g
 * @param b
 * @return
 */
@ExperimentalUnsignedTypes
fun rgbToInt(r: UByte, g: UByte, b: UByte): Int {
    return 255.shl(8).or(r.toInt()).shl(8).or(g.toInt()).shl(8).or(b.toInt())
}

/**
 * Rgb to int
 *
 * @param triple
 * @return
 */
@ExperimentalUnsignedTypes
fun rgbToInt(triple: Triple<UByte, UByte, UByte>): Int {
    return 255.shl(8).or(triple.first.toInt()).shl(8).or(triple.second.toInt()).shl(8).or(triple.third.toInt())
}

/**
 * Rgba to int
 *
 * @param r
 * @param g
 * @param b
 * @param a
 * @return
 */
@ExperimentalUnsignedTypes
fun rgbaToInt(r: UByte, g: UByte, b: UByte, a: UByte): Int {
    return a.toInt().shl(8).or(r.toInt()).shl(8).or(g.toInt()).shl(8)
        .or(b.toInt()) // Convert 'a' first so later intToRgb works
}

/**
 * Rgba to int
 *
 * @param quadruple
 * @return
 */
@ExperimentalUnsignedTypes
fun rgbaToInt(quadruple: Quadruple<UByte, UByte, UByte, UByte>): Int {
    val res = quadruple.fourth.toInt().shl(8).or(quadruple.first.toInt()).shl(8).or(quadruple.second.toInt()).shl(8)
        .or(quadruple.third.toInt()) // Convert 'a' first so later intToRgb works
    return res
}

/**
 * Int to rgb
 *
 * @param data
 * @return
 */
@ExperimentalUnsignedTypes
fun intToRgb(data: Int): Triple<UByte, UByte, UByte> {
    return Triple(data.shr(16).toUByte(), data.shr(8).toUByte(), data.toUByte())
}

/**
 * Int to rgba
 *
 * @param data
 * @return
 */
@ExperimentalUnsignedTypes
fun intToRgba(data: Int): Quadruple<UByte, UByte, UByte, UByte> {
    val res = Quadruple(data.shr(16).toUByte(), data.shr(8).toUByte(), data.toUByte(), data.shr(24).toUByte())
    return res
}
