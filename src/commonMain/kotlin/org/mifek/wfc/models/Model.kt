package org.mifek.wfc.models

import org.mifek.wfc.core.WfcAlgorithm

/**
 * Model
 *
 * @constructor Create empty Model
 */
interface Model {
    /**
     * Build
     *
     * @return
     */
    fun build(): WfcAlgorithm
}