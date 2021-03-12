package org.mifek.wfc.datatypes

enum class Axis3D(private val value: Int) {
    X(0),
    Y(1),
    Z(2);

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