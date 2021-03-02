package org.mifek.wfc.models

data class ModelOptions(
    val allowRotations: Boolean = true,
    val allowFlips: Boolean = true,
    val periodicOutput: Boolean = false,
    val periodicInput: Boolean = false,
    val grounded: Boolean = false,
    val roofed: Boolean = false,
    val sided: Boolean = false,
)
