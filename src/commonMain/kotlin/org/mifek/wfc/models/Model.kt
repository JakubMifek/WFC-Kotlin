package org.mifek.wfc.models

import org.mifek.wfc.core.WfcAlgorithm

interface Model {
    /**
     * Builds the WFC Core
     */
    fun build(): WfcAlgorithm
}