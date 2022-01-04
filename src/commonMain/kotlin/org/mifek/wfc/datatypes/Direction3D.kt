package org.mifek.wfc.datatypes

/**
 * Direction3d
 *
 * @property value
 * @constructor Create empty Direction3d
 */
enum class Direction3D(private val value: Int) {
    /**
     * U p
     *
     * @constructor Create empty U p
     */
    UP(0),

    /**
     * R i g h t
     *
     * @constructor Create empty R i g h t
     */
    RIGHT(1),

    /**
     * F o r w a r d
     *
     * @constructor Create empty F o r w a r d
     */
    FORWARD(2),

    /**
     * D o w n
     *
     * @constructor Create empty D o w n
     */
    DOWN(3),

    /**
     * L e f t
     *
     * @constructor Create empty L e f t
     */
    LEFT(4),

    /**
     * B a c k w a r d
     *
     * @constructor Create empty B a c k w a r d
     */
    BACKWARD(5);

    /**
     * To int
     *
     * @return
     */
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
