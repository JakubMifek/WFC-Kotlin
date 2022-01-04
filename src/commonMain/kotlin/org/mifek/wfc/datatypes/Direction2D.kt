package org.mifek.wfc.datatypes

/**
 * Direction2d
 *
 * @property value
 * @constructor Create empty Direction2d
 */
enum class Direction2D(private val value: Int) {
    /**
     * N o r t h
     *
     * @constructor Create empty N o r t h
     */
    NORTH(0),

    /**
     * E a s t
     *
     * @constructor Create empty E a s t
     */
    EAST(1),

    /**
     * S o u t h
     *
     * @constructor Create empty S o u t h
     */
    SOUTH(2),

    /**
     * W e s t
     *
     * @constructor Create empty W e s t
     */
    WEST(3);

    /**
     * To int
     *
     * @return
     */
    fun toInt(): Int {
        return value
    }

    companion object {
        fun fromInt(value: Int): Direction2D {
            return when (value) {
                0 -> NORTH
                1 -> EAST
                2 -> SOUTH
                3 -> WEST
                else -> throw Error("Wrong value. Accepts 0..3.")
            }
        }
    }
}
