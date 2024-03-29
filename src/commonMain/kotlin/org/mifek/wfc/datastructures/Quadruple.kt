package org.mifek.wfc.datastructures

/**
 * Quadruple
 *
 * @param A
 * @param B
 * @param C
 * @param D
 * @property first
 * @property second
 * @property third
 * @property fourth
 * @constructor Create empty Quadruple
 */
data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) {
    /**
     * Returns string representation of the [Quadruple] including its [first], [second], [third] and [fourth] values.
     */
    override fun toString(): String = "($first, $second, $third, $fourth)"
}
