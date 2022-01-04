package org.mifek.wfc.datatypes

/**
 * Axis3d
 *
 * @property value
 * @constructor Create empty Axis3d
 */
enum class Axis3D(private val value: Int) {
    /**
     * X
     *
     * @constructor Create empty X
     */
    X(0),

    /**
     * Y
     *
     * @constructor Create empty Y
     */
    Y(1),

    /**
     * Z
     *
     * @constructor Create empty Z
     */
    Z(2);

    /**
     * To int
     *
     * @return
     */
    fun toInt(): Int {
        return value
    }

    companion object {
        fun fromInt(value: Int): Axis3D {
            return when (value) {
                0 -> X
                1 -> Y
                2 -> Z
                else -> throw Error("Wrong value. Accepts 0..2.")
            }
        }
    }
}