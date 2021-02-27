package org.mifek.wfc.adapters

import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.models.OverlappingCartesian2DModel
import org.mifek.wfc.utils.formatPatterns
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream
import kotlin.random.Random

fun saveCurrentOutputImage(
    algorithm: Cartesian2DWfcAlgorithm,
    outputPath: String = "outputs/tmp.png",
    scale: Int = 1
) {
    val output = algorithm.constructOutput().upScaled(scale)
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
            when(it) {
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
    repeats: Int = 1,
    overlap: Int = 1,
    seed: Int = Random.nextInt(),
    outputScale: Int = 1,
    outputEachStep: Boolean = false,
    outputStepsAsAnimation: Boolean = false,
    outputSuccessOnly: Boolean = false,
    useEveryNthStep: Int = 1,
    allowRotations: Boolean = true,
    allowFlips: Boolean = true,
): Boolean {
    val source = ImageIO.read(File(sourcePath))

    val model = OverlappingCartesian2DModel(
        source.toIntArray2D(),
        overlap,
        outputWidth,
        outputHeight,
        allowRotations = allowRotations,
        allowFlips = allowFlips
    )

    // TODO: model by mel jit jen do WIDTH-N a HEIGHT-N, zbytek se bere z posledniho patternu..
//    println("Patterns: ${model.patterns.size}")
//    println(formatPatterns(model.patternsArray.map { it.asIntArray() }.toTypedArray()))

    val algorithm = model.build()

//    algorithm.onObserve += {
//        printGrid(algorithm.constructOutput())
//    }
//    algorithm.onPropagationStep += {
//        println()
//        printGrid(algorithm.constructOutput())
//    }
//    algorithm.onStep += {
//        println("\n\n")
//    }

//    for(x in 0 until outputWidth) {
//        algorithm.setPixel(x, outputHeight-1, model.patterns[model.patterns.lastIndex])
//    }

    if (outputEachStep) {
        var imageNumber = 0
        var stepNumber = 0
        algorithm.onStep += {
            if (stepNumber++ % useEveryNthStep == 0) {
                saveCurrentOutputImage(
                    algorithm,
                    outputPath + "${imageNumber++}",
                    outputScale
                )
            }
        }

        // TODO: Remove images if fail and outputOnlySuccess true
    }

    var repeat = 1
    var result = false

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
                    algorithm.constructOutput()
                        .upScaled(outputScale)
                        .toBufferedImage()
                )
            }
        }
        algorithm.onFinished += {
            if (repeat >= repeats || result) {
                writer.close()
                outputStream.close()
            }
        }

        // TODO: Remove animation if fail and outputOnlySuccess true
    }

    val random = Random(seed)
    var actualSeed = random.nextInt()
    result = algorithm.run(actualSeed)
    while (!result && repeat++ < repeats) {
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

//    println("Success seed: $actualSeed")
    saveCurrentOutputImage(algorithm, outputPath, outputScale)

    return true
}