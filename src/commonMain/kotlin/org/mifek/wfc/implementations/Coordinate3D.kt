package org.mifek.wfc.implementations
//
//class Coordinate3D(val x: Int, val y: Int, val z: Int): Coordinate(3, x, y, z) {
//    inline fun left(): Coordinate3D? { return if (x == 0) null else Coordinate3D(x-1, y, z) }
//    inline fun right(width: Int = Int.MAX_VALUE): Coordinate3D? { return if (x == width-1) null else Coordinate3D(x+1, y, z) }
//    inline fun under(): Coordinate3D? {  return if (y == 0) null else Coordinate3D(x, y-1, z) }
//    inline fun above(height: Int = Int.MAX_VALUE): Coordinate3D? { return if (y == height-1) null else Coordinate3D(x, y+1, z) }
//    inline fun behind(): Coordinate3D? { return if (z == 0) null else Coordinate3D(x, y, z-1) }
//    inline fun before(depth: Int = Int.MAX_VALUE): Coordinate3D? { return if (z == depth-1) null else Coordinate3D(x, y, z+1) }
//
//    override fun serialize(vararg size: Int): Int {
//        if(size.size != dimension) {
//            throw Error("Number of parameters (${size.size}) must equal set dimension ($dimension).")
//        }
//
//        return (z * size[1] + y) * size[0] + x
//    }
//
//    override fun onBoundary(vararg size: Int): Boolean {
//        return x == 0 || y == 0 || z == 0 || x == size[0]-1 || y == size[1]-1 || z == size[2]-1
//    }
//
//    override fun neighbourIterator(vararg size: Int): Sequence<Coordinate> {
//        if(size.size != dimension) {
//            throw Error("Mismatch in number of dimensions. Expected $dimension sizes but got ${size.size}")
//        }
//
//        return neighbourIterator3D(size[0], size[1], size[2])
//    }
//
//    fun neighbourIterator3D(width: Int = Int.MAX_VALUE, height: Int = Int.MAX_VALUE, depth: Int = Int.MAX_VALUE): Sequence<Coordinate3D> {
//        return sequence {
//            val a = above(height)
//            if (a != null) yield(a)
//            val r = right(width)
//            if (r != null) yield(r)
//            val bd = behind()
//            if (bd != null) yield(bd)
//            val u = under()
//            if(u != null) yield(u)
//            val l = left()
//            if(l != null) yield(l)
//            val be = before(depth)
//            if(be != null) yield(be)
//        }
//    }
//}