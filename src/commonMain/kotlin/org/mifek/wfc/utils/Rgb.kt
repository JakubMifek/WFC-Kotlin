package org.mifek.wfc.utils

import org.mifek.wfc.datastructures.Quadruple

/**
 * Converts RGB triple to Int
 */
inline fun rgbToInt(r: UByte, g: UByte, b: UByte): Int {
    return 255.shl(8).or(r.toInt()).shl(8).or(g.toInt()).shl(8).or(b.toInt())
}

/**
 * Serializes RGBA quadruple to Int
 */
inline fun rgbaToInt(r: UByte, g: UByte, b: UByte, a: UByte): Int {
    return a.toInt().shl(8).or(r.toInt()).shl(8).or(g.toInt()).shl(8)
        .or(b.toInt()) // Convert 'a' first so later intToRgb works
}

/**
 * Converts serialized color to RGB triple
 */
inline fun intToRgb(data: Int): Triple<UByte, UByte, UByte> {
    return Triple(data.shr(16).toUByte(), data.shr(8).toUByte(), data.toUByte())
}

/**
 * Converts serialized color to RGBA quadruple
 */
inline fun intToRgba(data: Int): Quadruple<UByte, UByte, UByte, UByte> {
    return Quadruple(data.shr(16).toUByte(), data.shr(8).toUByte(), data.toUByte(), data.shr(24).toUByte())
}
