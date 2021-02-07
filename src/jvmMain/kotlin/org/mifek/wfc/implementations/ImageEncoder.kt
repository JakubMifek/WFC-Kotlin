package org.mifek.wfc.implementations
//
//import org.mifek.wfc.interfaces.Encoder
//import org.mifek.wfc.interfaces.OrthogonalGrid
//import java.awt.image.BufferedImage
//
//class ImageEncoder : Encoder<BufferedImage, Coordinate2D> {
//    override fun encode(source: BufferedImage): Grid2D {
//        return Grid2D(source.width, source.height, IntArray(source.width * source.height) {
//            source.getRGB(it % source.width, it / source.width)
//        })
//    }
//
//    override fun decode(grid: OrthogonalGrid<Coordinate2D>): BufferedImage {
//        val ret = BufferedImage(grid.size[0], grid.size[1], BufferedImage.TYPE_INT_ARGB)
//        for (i in 0 until grid.totalSize) {
//            ret.setRGB(i % grid.size[0], i / grid.size[0], grid[i])
//        }
//        return ret
//    }
//}