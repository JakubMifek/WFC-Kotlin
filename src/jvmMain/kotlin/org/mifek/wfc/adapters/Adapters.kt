package org.mifek.wfc.adapters

import org.mifek.wfc.core.WfcAlgorithm
import org.mifek.wfc.models.Model
import org.mifek.wfc.models.OverlappingCartesian2DModel
import org.mifek.wfc.scale2D
import org.mifek.wfc.utils.overlapping.constructOutput
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream
import kotlin.random.Random

fun saveCurrentOutputImage(
    algorithm: WfcAlgorithm,
    model: Model,
    stride: Int,
    outputPath: String = "outputs/tmp.png",
    scale: Int = 1
) {
    val output = constructOutput(algorithm.waves, model.patterns).scale2D(stride, scale)
    var path = outputPath
    if (!outputPath.endsWith(".png"))
        path += ".png"
    val outputFile = File(path)
    outputFile.mkdirs()

    ImageIO.write(output.toBufferedImage(stride * scale), "png", outputFile)
}

fun imitateImageUsingOverlappingModel(
    sourcePath: String,
    outputPath: String,
    outputWidth: Int,
    outputHeight: Int,
    overlap: Int = 1,
    seed: Int = Random.nextInt(),
    outputScale: Int = 1,
    outputEachStep: Boolean = false,
    outputStepsAsAnimation: Boolean = false,
    useEveryNthStep: Int = 1,
    allowRotations: Boolean = true,
    allowFlips: Boolean = true,
): Boolean {
    val source = ImageIO.read(File(sourcePath))

    val model = OverlappingCartesian2DModel(
        source.toIntArray(),
        source.width,
        overlap,
        outputWidth,
        outputHeight,
        allowRotations = allowRotations,
        allowFlips = allowFlips
    )

    val algorithm = model.build()

    if (outputEachStep) {
        var imageNumber = 0
        var stepNumber = 0
        algorithm.onStep += {
            if (stepNumber++ % useEveryNthStep == 0) {
                saveCurrentOutputImage(
                    algorithm,
                    model,
                    outputWidth,
                    outputPath + "${imageNumber++}",
                    outputScale
                )
            }
        }
    }

    if (outputStepsAsAnimation) {
        var stepNumber = 0
        val outputFile = File("$outputPath.gif")
        val outputStream = FileImageOutputStream(outputFile)
        val writer = GifSequenceWriter(
            outputStream,
            BufferedImage.TYPE_INT_ARGB,
            16,
            true
        )
        algorithm.onStep += {
            if (stepNumber++ % useEveryNthStep == 0) {
                writer.writeToSequence(
                    constructOutput(algorithm.waves, model.patterns).scale2D(outputWidth, outputScale)
                        .toBufferedImage(outputWidth * outputScale)
                )
            }
        }
        algorithm.onFinished += {
            writer.close()
            outputStream.close()
        }
    }

    val result = algorithm.run(seed ?: Random.nextInt())
    if (!result) return false

    saveCurrentOutputImage(algorithm, model, outputWidth, outputPath, outputScale)

    return true
}