package org.mifek.wfc.adapters.options

/**
 * Image options
 *
 * @property outputPath
 * @property fileName
 * @property outputScale
 * @property includeSeed
 * @constructor Create empty Image options
 */
data class ImageOptions(
    val outputPath: String,
    val fileName: String,
    val outputScale: Int = 1,
    val includeSeed: Boolean = false,
)
