package org.mifek.wfc.adapters.options

data class ImageOptions(
    val outputPath: String,
    val fileName: String,
    val outputScale: Int = 1,
    val includeSeed: Boolean = false,
)
