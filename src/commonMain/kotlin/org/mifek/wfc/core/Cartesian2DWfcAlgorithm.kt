package org.mifek.wfc.core

import org.mifek.wfc.topologies.Cartesian2DTopology

class Cartesian2DWfcAlgorithm(
    width: Int,
    height: Int,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>
) : WfcAlgorithm(
    Cartesian2DTopology(width, height),
    weights,
    propagator
)