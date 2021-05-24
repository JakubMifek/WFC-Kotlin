package org.mifek.wfc.models.options

data class Cartesian3DModelOptions(
    val allowXRotations: Boolean = false,
    val allowYRotations: Boolean = false,
    val allowZRotations: Boolean = false,
    val allowXFlips: Boolean = false,
    val allowYFlips: Boolean = false,
    val allowZFlips: Boolean = false,
    override val periodicOutput: Boolean = false,
    override val periodicInput: Boolean = false,
): ModelOptions
