package org.mifek.wfc.datatypes

enum class Directions2D(private val value: Int) {
    NORTH(0),
    EAST(1),
    SOUTH(2),
    WEST(3);

    fun toInt(): Int {
        return value
    }

    companion object {
        fun fromInt(value: Int): Directions2D {
            return when(value) {
                0 -> NORTH
                1 -> EAST
                2 -> SOUTH
                3 -> WEST
                else -> throw Error("Wrong value. Accepts 1..4.")
            }
        }
    }
}
