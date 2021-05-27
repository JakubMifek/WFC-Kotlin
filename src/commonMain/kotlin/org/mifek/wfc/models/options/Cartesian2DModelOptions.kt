package org.mifek.wfc.models.options

data class Cartesian2DModelOptions(
    val allowRotations: Boolean = false,
    val allowHorizontalFlips: Boolean = false,
    val allowVerticalFlips: Boolean = false,
    val grounded: Boolean = false,
    /**
     * Bans ground patterns everywhere but last row
     */
    val banGroundElsewhere: Boolean = false,
    val roofed: Boolean = false,
    /**
     * Bans roof patterns everywhere but first row
     */
    val banRoofElsewhere: Boolean = false,

    val leftSided: Boolean = false,
    val banLeftSideElsewhere: Boolean = false,

    val rightSided: Boolean = false,
    val banRightSideElsewhere: Boolean = false,
    /**
     * Bans side patterns everywhere but sides
     */
    val banSidesElsewhere: Boolean = false,

    override val periodicOutput: Boolean = false,
    override val periodicInput: Boolean = false,
    override val weightPower: Double = 1.0
) : ModelOptions
