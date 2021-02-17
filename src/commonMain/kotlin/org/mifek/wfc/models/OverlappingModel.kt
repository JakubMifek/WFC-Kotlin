package org.mifek.wfc.models

interface OverlappingModel : Model {
    /**
     * Array where for each index representing a pattern is saved serialized pixel value (Int)
     */
    val patterns: Patterns

    /**
     * Map where for each key representing a pixel value (Int) is saved IntArray of all pattern indices that are to be resolved to the pixel
     */
    val pixels: Pixels
}