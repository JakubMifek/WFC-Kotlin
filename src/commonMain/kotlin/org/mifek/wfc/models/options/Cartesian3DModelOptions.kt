package org.mifek.wfc.models.options

data class Cartesian3DModelOptions(
    override val allowRotations: Boolean = false,
    override val allowFlips: Boolean = false,
    override val periodicOutput: Boolean = false,
    override val periodicInput: Boolean = false,
): ModelOptions
