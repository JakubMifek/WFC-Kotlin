package org.mifek.wfc.utils

import org.mifek.wfc.datastructures.Quadruple

/**
 * Converts RGB triple to Int
 */
@ExperimentalUnsignedTypes
inline fun rgbToInt(r: UByte, g: UByte, b: UByte): Int {
    return 255.shl(8).or(r.toInt()).shl(8).or(g.toInt()).shl(8).or(b.toInt())
}

/**
 * Converts RGB triple to Int
 */
@ExperimentalUnsignedTypes
inline fun rgbToInt(triple: Triple<UByte, UByte, UByte>): Int {
    return 255.shl(8).or(triple.first.toInt()).shl(8).or(triple.second.toInt()).shl(8).or(triple.third.toInt())
}

/**
 * Serializes RGBA quadruple to Int
 */
@ExperimentalUnsignedTypes
inline fun rgbaToInt(r: UByte, g: UByte, b: UByte, a: UByte): Int {
    return a.toInt().shl(8).or(r.toInt()).shl(8).or(g.toInt()).shl(8)
        .or(b.toInt()) // Convert 'a' first so later intToRgb works
}

/**
 * Serializes RGBA quadruple to Int
 */
@ExperimentalUnsignedTypes
inline fun rgbaToInt(quadruple: Quadruple<UByte, UByte, UByte, UByte>): Int {
    val res = quadruple.fourth.toInt().shl(8).or(quadruple.first.toInt()).shl(8).or(quadruple.second.toInt()).shl(8)
        .or(quadruple.third.toInt()) // Convert 'a' first so later intToRgb works
    return res
}

/**
 * Converts serialized color to RGB triple
 */
@ExperimentalUnsignedTypes
inline fun intToRgb(data: Int): Triple<UByte, UByte, UByte> {
    return Triple(data.shr(16).toUByte(), data.shr(8).toUByte(), data.toUByte())
}

/**
 * Converts serialized color to RGBA quadruple
 */
@ExperimentalUnsignedTypes
inline fun intToRgba(data: Int): Quadruple<UByte, UByte, UByte, UByte> {
    val res = Quadruple(data.shr(16).toUByte(), data.shr(8).toUByte(), data.toUByte(), data.shr(24).toUByte())
    return res
}
