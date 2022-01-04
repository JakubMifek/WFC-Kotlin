package org.mifek.wfc.adapters

import java.awt.image.RenderedImage
import java.io.IOException
import javax.imageio.*
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.ImageOutputStream

/**
 * Gif sequence writer
 *
 * @constructor
 *
 * @param outputStream
 * @param imageType
 * @param timeBetweenFramesMS
 * @param loopContinuously
 */
open class GifSequenceWriter @Throws(IOException::class) constructor(
    outputStream: ImageOutputStream,
    imageType: Int,
    timeBetweenFramesMS: Int,
    loopContinuously: Boolean
) {
    companion object {
        fun getNode(rootNode: IIOMetadataNode, nodeName: String): IIOMetadataNode {
            val nNodes = rootNode.length
            for (i in 0 until nNodes) {
                if (rootNode.item(i).nodeName.compareTo(nodeName, true) == 0) {
                    return rootNode.item(i) as IIOMetadataNode
                }
            }
            val node = IIOMetadataNode(nodeName)
            rootNode.appendChild(node)
            return node
        }
    }

    protected val gifWriter: ImageWriter = ImageIO.getImageWritersBySuffix("gif").next()
    protected val imageWriteParam: ImageWriteParam = gifWriter.defaultWriteParam
    protected val imageMetaData: IIOMetadata

    init {
        val imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType)
        imageMetaData = gifWriter.getDefaultImageMetadata(
            imageTypeSpecifier,
            imageWriteParam
        )
        val metaFormatName = imageMetaData.nativeMetadataFormatName
        val root = imageMetaData.getAsTree(metaFormatName) as IIOMetadataNode
        val graphicsControlExtensionNode = GifSequenceWriter.getNode(root, "GraphicControlExtension")
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none")
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE")
        graphicsControlExtensionNode.setAttribute(
            "transparentColorFlag",
            "FALSE"
        )
        graphicsControlExtensionNode.setAttribute(
            "delayTime",
            (timeBetweenFramesMS / 10).toString()
        )
        graphicsControlExtensionNode.setAttribute(
            "transparentColorIndex",
            "0"
        )
        val commentsNode = getNode(root, "CommentExtensions")
        commentsNode.setAttribute("CommentExtension", "Created by MAH")
        val appExtensionsNode = getNode(
            root,
            "ApplicationExtensions"
        )
        val child = IIOMetadataNode("ApplicationExtension")
        child.setAttribute("applicationID", "NETSCAPE")
        child.setAttribute("authenticationCode", "2.0")
        val loop = if (loopContinuously) 0 else 1
        child.userObject = byteArrayOf(0x1, (loop and 0xFF).toByte(), (loop shr 8 and 0xFF).toByte())
        appExtensionsNode.appendChild(child)
        imageMetaData.setFromTree(metaFormatName, root)
        gifWriter.output = outputStream
        gifWriter.prepareWriteSequence(null)
    }

    /**
     * Write to sequence
     *
     * @param img
     */
    @Throws(IOException::class)
    fun writeToSequence(img: RenderedImage) {
        gifWriter.writeToSequence(
            IIOImage(
                img,
                null,
                imageMetaData
            ),
            imageWriteParam
        )
    }

    /**
     * Close
     *
     */
    @Throws(IOException::class)
    open fun close() {
        gifWriter.endWriteSequence()
    }
}