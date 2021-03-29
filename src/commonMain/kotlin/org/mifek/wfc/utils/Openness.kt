package org.mifek.wfc.utils

import org.mifek.wfc.core.*
import org.mifek.wfc.datastructures.*
import org.mifek.wfc.models.*
import org.mifek.wfc.models.options.*
import org.mifek.wfc.topologies.*

open class Cartesian3DWfcAlgorithmChild(
    topology3D: Cartesian3DTopology,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>,
    patterns: Patterns
) : Cartesian3DWfcAlgorithm(topology3D, weights, propagator, patterns)

open class Cartesian2DWfcAlgorithmChild(
    topology2D: Cartesian2DTopology,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>,
    patterns: Patterns,
    pixels: Pixels,
) : Cartesian2DWfcAlgorithm(topology2D, weights, propagator, patterns, pixels)

open class CartesianNDWfcAlgorithmChild(
    topologyND: CartesianNDTopology,
    weights: DoubleArray,
    propagator: Array<Array<IntArray>>,
    patterns: Patterns,
    pixels: Pixels,
) : CartesianNDWfcAlgorithm(topologyND, weights, propagator, patterns, pixels)

open class OverlappingCartesian2DModelChild(
    input: IntArray2D,
    overlap: Int,
    outputWidth: Int,
    outputHeight: Int,
    options: Cartesian2DModelOptions = Cartesian2DModelOptions()
) : OverlappingCartesian2DModel(input, overlap, outputWidth, outputHeight, options)

open class OverlappingImageModelChild(
    input: IntArray2D,
    overlap: Int,
    outputWidth: Int,
    outputHeight: Int,
    options: Cartesian2DModelOptions = Cartesian2DModelOptions()
) : OverlappingImageModel(input, overlap, outputWidth, outputHeight, options)

open class OverlappingCartesian3DModelChild(
    input: IntArray3D,
    overlap: Int,
    outputWidth: Int,
    outputHeight: Int,
    outputDepth: Int,
    options: Cartesian3DModelOptions = Cartesian3DModelOptions()
) : OverlappingCartesian3DModel(input, overlap, outputWidth, outputHeight, outputDepth, options)

open class Cartesian2DTopologyChild(width: Int, height: Int, periodic: Boolean = false) :
    Cartesian2DTopology(width, height, periodic)

open class Cartesian3DTopologyChild(width: Int, height: Int, depth: Int, periodic: Boolean = false) :
    Cartesian3DTopology(width, height, depth, periodic)

open class CartesianNDTopologyChild(sizes: IntArray, periodic: Boolean = false) :
    CartesianNDTopology(sizes, periodic)