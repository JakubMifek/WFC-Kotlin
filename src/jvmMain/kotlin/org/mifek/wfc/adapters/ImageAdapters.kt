package org.mifek.wfc.adapters

import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.models.ModelOptions
import org.mifek.wfc.models.OverlappingCartesian2DModel
import org.mifek.wfc.models.OverlappingImageModel
import org.mifek.wfc.utils.formatPatterns
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream
import kotlin.random.Random

fun savePatternImages(
    model: OverlappingImageModel,
    outputPath: String = "outputs/",
    scale: Int = 1
) {
    for ((index, pattern) in model.patterns.withIndex()) {
        var path = "$outputPath/${index}.png"
        val outputFile = File(path)
        outputFile.mkdirs()
        ImageIO.write(pattern.upScaled(scale).toBufferedImage(), "png", outputFile)
    }
}

fun saveCurrentOutputImage(
    algorithm: Cartesian2DWfcAlgorithm,
    model: OverlappingImageModel,
    outputPath: String = "outputs/tmp.png",
    scale: Int = 1
) {
    val output = model.constructOutput(algorithm).upScaled(scale)
    var path = outputPath
    if (!outputPath.endsWith(".png"))
        path += ".png"
    val outputFile = File(path)
    outputFile.mkdirs()

    ImageIO.write(output.toBufferedImage(), "png", outputFile)
}


fun printGrid(grid: IntArray2D) {
    for (i in 0 until grid.height) {
        println(grid.data.slice(i * grid.width until (i + 1) * grid.width).map {
            when (it) {
                -123456789 -> "0"
                -1 -> "1"
                -16777216 -> "2"
                -65536 -> "3"
                else -> "?"
            }
        }.joinToString(" "))
    }
}

fun imitateImageUsingOverlappingModel(
    sourcePath: String,
    outputPath: String,
    outputWidth: Int,
    outputHeight: Int,
    options: ImageAdapterOptions,
): Boolean {
    val source = ImageIO.read(File(sourcePath))

    val model = OverlappingImageModel(
        source.toIntArray2D(),
        options.overlap,
        outputWidth,
        outputHeight,
        options.toModelOptions()
    )
    val algorithm = model.build()

    if (options.outputPatterns) {
        savePatternImages(
            model,
            "$outputPath/patterns",
            options.outputScale
        )
    }

    if (options.outputEachStep) {
        var imageNumber = 0
        var stepNumber = 0
        algorithm.onStart += {
            saveCurrentOutputImage(
                algorithm,
                model,
                outputPath + "/${imageNumber++}",
                options.outputScale
            )
        }
        algorithm.onPropagationStep += {
            if (stepNumber++ % options.useEveryNthStep == 0) {
                saveCurrentOutputImage(
                    algorithm,
                    model,
                    outputPath + "/${imageNumber++}",
                    options.outputScale
                )
            }
        }
        algorithm.onObserve += {
            if (stepNumber++ % options.useEveryNthStep == 0) {
                saveCurrentOutputImage(
                    algorithm,
                    model,
                    outputPath + "/${imageNumber++}",
                    options.outputScale
                )
            }
        }

        // TODO: Remove images if fail and outputOnlySuccess true
    }

    var repeat = 1
    var result = false

    if (options.outputStepsAsAnimation) {
        var stepNumber = 0
        val outputFile = File("$outputPath.gif")
        val outputStream = FileImageOutputStream(outputFile)
        val writer = GifSequenceWriter(
            outputStream,
            BufferedImage.TYPE_INT_ARGB,
            100,
            false
        )
        writer.writeToSequence(
            model.constructOutput(algorithm)
                .upScaled(options.outputScale)
                .toBufferedImage()
        )
        algorithm.onStep += {
            if (++stepNumber % options.useEveryNthStep == 0) {
                writer.writeToSequence(
                    model.constructOutput(algorithm)
                        .upScaled(options.outputScale)
                        .toBufferedImage()
                )
            }
        }
        algorithm.onFinished += {
            if (repeat >= options.repeats || result) {
                writer.writeToSequence(
                    model.constructOutput(algorithm)
                        .upScaled(options.outputScale)
                        .toBufferedImage()
                )
                writer.close()
                outputStream.close()
            }
        }

        // TODO: Remove animation if fail and outputOnlySuccess true
    }

    val random = Random(options.seed)
    var actualSeed = random.nextInt()
    result = algorithm.run(actualSeed)
    while (!result && repeat++ < options.repeats) {
        println(
            "Failed for ${repeat - 1}${
                (when (repeat) {
                    2 -> "st"
                    3 -> "nd"
                    4 -> "rd"
                    else -> "th"
                })
            } time."
        )
        algorithm.clear()
        actualSeed = random.nextInt()
        result = algorithm.run(actualSeed)
    }

    if (!result) return false

    saveCurrentOutputImage(
        algorithm,
        model,
        outputPath,
        options.outputScale
    )

    return true
}