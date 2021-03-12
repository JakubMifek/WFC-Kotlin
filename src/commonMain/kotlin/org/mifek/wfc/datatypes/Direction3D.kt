package org.mifek.wfc.datatypes

enum class Direction3D(private val value: Int) {
    UP(0),
    RIGHT(1),
    FORWARD(2),
    DOWN(3),
    LEFT(4),
    BACKWARD(5);

    fun toInt(): Int {
        return value
    }

    companion object {
        fun fromInt(value: Int): Direction3D {
            return when (value) {
                0 -> UP
                1 -> RIGHT
                2 -> FORWARD
                3 -> DOWN
                4 -> LEFT
                5 -> BACKWARD
                else -> throw Error("Wrong value. Accepts 0..5.")
            }
        }
    }
}
