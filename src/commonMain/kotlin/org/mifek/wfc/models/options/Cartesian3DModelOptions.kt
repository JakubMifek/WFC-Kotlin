package org.mifek.wfc.models.options

import org.mifek.wfc.datatypes.Direction3D

/**
 * Cartesian3d model options
 *
 * @property allowXRotations
 * @property allowYRotations
 * @property allowZRotations
 * @property allowXFlips
 * @property allowYFlips
 * @property allowZFlips
 * @property setPlanes
 * @property banPlanesElsewhere
 * @property periodicOutput
 * @property periodicInput
 * @property weightPower
 * @constructor Create empty Cartesian3d model options
 */
data class Cartesian3DModelOptions(
    val allowXRotations: Boolean = false,
    val allowYRotations: Boolean = false,
    val allowZRotations: Boolean = false,
    val allowXFlips: Boolean = false,
    val allowYFlips: Boolean = false,
    val allowZFlips: Boolean = false,
    val setPlanes: Set<Direction3D> = emptySet(),
    val banPlanesElsewhere: Set<Direction3D> = emptySet(),
    override val periodicOutput: Boolean = false,
    override val periodicInput: Boolean = false,
    override val weightPower: Double = 1.0,
) : ModelOptions
