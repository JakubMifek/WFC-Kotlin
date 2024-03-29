package org.mifek.wfc.datastructures

import org.mifek.wfc.datatypes.Axis3D
import org.mifek.wfc.utils.toCoordinates
import org.mifek.wfc.utils.toIndex

/**
 * Int array3d
 *
 * @property width
 * @property height
 * @property depth
 * @constructor
 *
 * @param init
 */
class IntArray3D(val width: Int, val height: Int, val depth: Int, init: (Int) -> Int = { 0 }) : Iterable<Int> {
    val data = IntArray(width * height * depth, init)
    val size = data.size
    val lastIndex = data.lastIndex
    val indices = data.indices
    private val sizes = intArrayOf(width, height, depth)

    override fun iterator(): IntIterator {
        return data.iterator()
    }

    /**
     * Get
     *
     * @param index
     * @return
     */
    operator fun get(index: Int): Int {
        return data[index]
    }

    /**
     * Set
     *
     * @param index
     * @param value
     */
    operator fun set(index: Int, value: Int) {
        data[index] = value
    }

    /**
     * Get
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    operator fun get(x: Int, y: Int, z: Int): Int {
        return data[(z * height + y) * width + x]
    }

    /**
     * Get
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    operator fun get(x: IntRange?, y: Int, z: Int): Sequence<Int> {
        val index = (z * height + y) * width
        var X = x
        if (X == null) X = 0 until width
        return sequence { for (it in X) yield(data[index + it]) }
    }

    /**
     * Get
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    operator fun get(x: Int, y: IntRange?, z: Int): Sequence<Int> {
        val index = z * height * width + x
        var Y = y
        if (Y == null) Y = 0 until height
        return sequence { for (it in Y) yield(data[index + it * width]) }
    }

    /**
     * Get
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    operator fun get(x: Int, y: Int, z: IntRange?): Sequence<Int> {
        val index = y * width + x
        val multiplier = width * height
        var Z = z
        if (Z == null) Z = 0 until depth
        return sequence { for (it in Z) yield(data[index + it * multiplier]) }
    }

    /**
     * Get
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    operator fun get(x: IntRange?, y: IntRange?, z: Int): Sequence<Sequence<Int>> {
        val i_z = z * height * width
        var Y = y
        var X = x
        if (X == null) X = 0 until width
        if (Y == null) Y = 0 until height
        return sequence {
            for (i_y in Y) {
                val index = i_z + i_y * width
                yield(sequence {
                    for (i_x in X) {
                        yield(data[index + i_x])
                    }
                })
            }
        }
    }

    /**
     * Get
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    operator fun get(x: IntRange?, y: Int, z: IntRange?): Sequence<Sequence<Int>> {
        val i_y = y * width
        val multiplier = width * height
        var Z = z
        var X = x
        if (X == null) X = 0 until width
        if (Z == null) Z = 0 until depth
        return sequence {
            for (i_z in Z) {
                val index = i_z * multiplier + i_y
                yield(sequence {
                    for (i_x in X) {
                        yield(data[index + i_x])
                    }
                })
            }
        }
    }

    /**
     * Get
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    operator fun get(x: Int, y: IntRange?, z: IntRange?): Sequence<Sequence<Int>> {
        val multiplier = width * height
        var Z = z
        var Y = y
        if (Y == null) Y = 0 until height
        if (Z == null) Z = 0 until depth
        return sequence {
            for (i_z in Z) {
                val index = i_z * multiplier + x
                yield(sequence {
                    for (i_y in Y) {
                        yield(data[index + i_y * width])
                    }
                })
            }
        }
    }

    /**
     * Get
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    operator fun get(x: IntRange?, y: IntRange?, z: IntRange?): Sequence<Sequence<Sequence<Int>>> {
        var Z = z
        var Y = y
        var X = x
        if (X == null) X = 0 until width
        if (Y == null) Y = 0 until height
        if (Z == null) Z = 0 until depth
        val multiplier = width * height
        return sequence {
            for (i_z in Z) {
                val preIndex = i_z * multiplier
                yield(sequence {
                    for (i_y in Y) {
                        val index = i_y * width + preIndex
                        yield(sequence {
                            for (i_x in X) {
                                yield(data[index + i_x])
                            }
                        })
                    }
                })
            }
        }
    }

    /**
     * Set
     *
     * @param x
     * @param y
     * @param z
     * @param value
     */
    operator fun set(x: Int, y: Int, z: Int, value: Int) {
        data[(z * height + y) * width + x] = value
    }

    /**
     * Set
     *
     * @param x
     * @param y
     * @param z
     * @param value
     */
    operator fun set(x: IntRange?, y: Int, z: Int, value: Int) {
        val index = (z * height + y) * width
        var X = x
        if (X == null) X = 0 until width
        for (it in X) data[index + it] = value
    }

    /**
     * Set
     *
     * @param x
     * @param y
     * @param z
     * @param value
     */
    operator fun set(x: Int, y: IntRange?, z: Int, value: Int) {
        val index = z * height * width + x
        var Y = y
        if (Y == null) Y = 0 until height
        for (it in Y) data[index + it * width] = value
    }

    /**
     * Set
     *
     * @param x
     * @param y
     * @param z
     * @param value
     */
    operator fun set(x: Int, y: Int, z: IntRange?, value: Int) {
        val index = y * width + x
        val multiplier = width * height
        var Z = z
        if (Z == null) Z = 0 until depth
        for (it in Z) data[index + it * multiplier] = value
    }

    /**
     * Set
     *
     * @param x
     * @param y
     * @param z
     * @param value
     */
    operator fun set(x: IntRange?, y: IntRange?, z: Int, value: Int) {
        val i_z = z * height * width
        var Y = y
        var X = x
        if (X == null) X = 0 until width
        if (Y == null) Y = 0 until height
        for (i_y in Y) {
            val index = i_z + i_y * width
            for (i_x in X) {
                data[index + i_x] = value
            }
        }
    }

    /**
     * Set
     *
     * @param x
     * @param y
     * @param z
     * @param value
     */
    operator fun set(x: IntRange?, y: Int, z: IntRange?, value: Int) {
        val i_y = y * width
        val multiplier = width * height
        var Z = z
        var X = x
        if (X == null) X = 0 until width
        if (Z == null) Z = 0 until depth
        for (i_z in Z) {
            val index = i_z * multiplier + i_y
            for (i_x in X) {
                data[index + i_x] = value
            }
        }
    }

    /**
     * Set
     *
     * @param x
     * @param y
     * @param z
     * @param value
     */
    operator fun set(x: Int, y: IntRange?, z: IntRange?, value: Int) {
        val multiplier = width * height
        var Z = z
        var Y = y
        if (Y == null) Y = 0 until height
        if (Z == null) Z = 0 until depth
        for (i_z in Z) {
            val index = i_z * multiplier + x
            for (i_y in Y) {
                data[index + i_y * width] = value
            }
        }
    }

    /**
     * Set
     *
     * @param x
     * @param y
     * @param z
     * @param value
     */
    operator fun set(x: IntRange?, y: IntRange?, z: IntRange?, value: Int) {
        var Z = z
        var Y = y
        var X = x
        if (X == null) X = 0 until width
        if (Y == null) Y = 0 until height
        if (Z == null) Z = 0 until depth
        val multiplier = width * height
        for (i_z in Z) {
            val preIndex = i_z * multiplier
            for (i_y in Y) {
                val index = i_y * width + preIndex
                for (i_x in X) {
                    data[index + i_x] = value
                }
            }
        }
    }

    /**
     * Content hash code
     *
     * @return
     */
    fun contentHashCode(): Int {
        return data.contentHashCode()
    }

    /**
     * Content equals
     *
     * @param other
     * @return
     */
    fun contentEquals(other: IntArray3D): Boolean {
        return data.contentEquals(other.data)
    }

    /**
     * As int array
     *
     * @return
     */
    fun asIntArray(): IntArray {
        return data
    }

    /**
     * To int array
     *
     * @return
     */
    fun toIntArray(): IntArray {
        return IntArray(size) { data[it] }
    }

    /**
     * Copy of
     *
     * @return
     */
    fun copyOf(): IntArray3D {
        return IntArray3D(width, height, depth) { data[it] }
    }

    /**
     * Slice
     *
     * @param startIndex
     * @param xRange
     * @param yRange
     * @param zRange
     * @return
     */
    fun slice(startIndex: Int, xRange: IntRange?, yRange: IntRange?, zRange: IntRange?): IntArray3D {
        val xRange2 = (xRange ?: 0 until width).iterator().asSequence().toList()
        val yRange2 = (yRange ?: 0 until height).iterator().asSequence().toList()
        val zRange2 = (zRange ?: 0 until depth).iterator().asSequence().toList()

        val slice = IntArray3D(xRange2.size, yRange2.size, zRange2.size)
        var sliceIndex = 0
        val coords = startIndex.toCoordinates(sizes)
        for (z in xRange2) {
            val Z = (coords[2] + z) % depth
            for (y in yRange2) {
                val Y = (coords[1] + y) % height
                for (x in xRange2) {
                    slice[sliceIndex++] = data[intArrayOf((coords[0] + x) % width, Y, Z).toIndex(sizes)]
                }
            }
        }

        return slice
    }

    /**
     * X rotated
     *
     * @return
     */
    fun xRotated(): IntArray3D {
        val res = IntArray3D(width, depth, height)

        for (z in 0 until depth) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    res[x, y, z] = this[x, z, height - 1 - y]
                }
            }
        }

        return res
    }

    /**
     * X neg rotated
     *
     * @return
     */
    fun xNegRotated(): IntArray3D {
        val res = IntArray3D(width, depth, height)

        for (z in 0 until depth) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    res[x, y, z] = this[x, depth - 1 - z, y]
                }
            }
        }

        return res
    }

    /**
     * Y rotated
     *
     * @return
     */
    fun yRotated(): IntArray3D {
        val res = IntArray3D(depth, height, width)

        for (z in 0 until depth) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    res[x, y, z] = this[depth - 1 - z, y, x]
                }
            }
        }

        return res
    }

    /**
     * Y neg rotated
     *
     * @return
     */
    fun yNegRotated(): IntArray3D {
        val res = IntArray3D(depth, height, width)

        for (z in 0 until depth) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    res[x, y, z] = this[z, y, width - 1 - x]
                }
            }
        }

        return res
    }

    /**
     * Z rotated
     *
     * @return
     */
    fun zRotated(): IntArray3D {
        val res = IntArray3D(height, width, depth)

        for (z in 0 until depth) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    res[x, y, z] = this[y, width - 1 - x, z]
                }
            }
        }

        return res
    }

    /**
     * Z neg rotated
     *
     * @return
     */
    fun zNegRotated(): IntArray3D {
        val res = IntArray3D(height, width, depth)

        for (z in 0 until depth) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    res[x, y, z] = this[height - 1 - y, x, z]
                }
            }
        }

        return res
    }

    /**
     * Rotated
     *
     * @param axis
     * @param positive
     * @return
     */
    fun rotated(axis: Axis3D, positive: Boolean = true): IntArray3D {
        return when (axis) {
            Axis3D.X -> if (positive) xRotated() else xNegRotated()
            Axis3D.Y -> if (positive) yRotated() else yNegRotated()
            Axis3D.Z -> if (positive) zRotated() else zNegRotated()
        }
    }

    /**
     * Flipped z
     *
     * @return
     */
    fun flippedZ(): IntArray3D {
        val res = IntArray3D(height, width, depth)

        for (z in 0 until depth) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    res[x, y, z] = this[x, y, depth - 1 - z]
                }
            }
        }

        return res
    }

    /**
     * Flipped y
     *
     * @return
     */
    fun flippedY(): IntArray3D {
        val res = IntArray3D(height, width, depth)

        for (z in 0 until depth) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    res[x, y, z] = this[x, height - 1 - y, z]
                }
            }
        }

        return res
    }

    /**
     * Flipped x
     *
     * @return
     */
    fun flippedX(): IntArray3D {
        val res = IntArray3D(height, width, depth)

        for (z in 0 until depth) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    res[x, y, z] = this[width - 1 - x, y, z]
                }
            }
        }

        return res
    }

    /**
     * Flipped
     *
     * @param axis
     * @return
     */
    fun flipped(axis: Axis3D): IntArray3D {
        return when (axis) {
            Axis3D.Z -> flippedZ()
            Axis3D.Y -> flippedY()
            Axis3D.X -> flippedX()
        }
    }

    /**
     * Clone
     *
     * @return
     */
    fun clone(): IntArray3D {
        return IntArray3D(width, height, depth) { data[it] }
    }

    /**
     * Up scaled
     *
     * @param scale
     * @return
     */
    fun upScaled(scale: Int): IntArray3D {
        if (scale < 1) {
            throw NumberFormatException("Scale must be >= 1")
        }
        if (scale == 1) return clone()

        val scaledWidth = width * scale
        val scaledHeight = height * scale
        val scaledDepth = depth * scale
        return IntArray3D(scaledWidth, scaledHeight, scaledDepth) {
            val x = it % scaledWidth
            val rest = it / scaledWidth
            val y = rest % scaledHeight
            val z = rest / scaledHeight
            val oriX = x / scale
            val oriY = y / scale
            val oriZ = z / scale
            this[(oriZ * height + oriY) * width + oriX]
        }
    }
}