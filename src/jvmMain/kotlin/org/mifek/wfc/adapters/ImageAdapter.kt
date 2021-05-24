package org.mifek.wfc.adapters

import org.mifek.wfc.adapters.options.EventType
import org.mifek.wfc.adapters.options.ImageAdapterOptions
import org.mifek.wfc.adapters.utils.toBufferedImage
import org.mifek.wfc.adapters.utils.toIntArray2D
import org.mifek.wfc.core.Cartesian2DWfcAlgorithm
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.models.OverlappingImageModel
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.stream.ImageOutputStream
import kotlin.random.Random

class ImageAdapter {
    companion object {
        fun saveImage(
            image: BufferedImage,
            outputPath: String = "outputs/",
            fileName: String = "test",
        ) {
            var path = "$outputPath/${fileName}.png"
            val outputFile = File(path)
            outputFile.mkdirs()
            ImageIO.write(image, "png", outputFile)
        }

        fun savePatternImages(
            model: OverlappingImageModel,
            outputPath: String = "outputs/",
            scale: Int = 1
        ) {
            for ((index, pattern) in model.patterns.withIndex()) {
                var path = "$outputPath/${index}.png"
                val outputFile = File(path)
                outputFile.mkdirs()
                ImageIO.write(
                    IntArray2D(model.overlap + 1, model.overlap + 1) { pattern[it] }.upScaled(scale).toBufferedImage(),
                    "png",
                    outputFile
                )
            }
        }

        @ExperimentalUnsignedTypes
        fun saveCurrentOutputImage(
            algorithm: Cartesian2DWfcAlgorithm,
            model: OverlappingImageModel,
            outputPath: String = "outputs/tmp.png",
            scale: Int = 1,
            highlight: Int? = null
        ) {
            val data = model.constructOutput(algorithm)
            if (highlight != null) {
                data[highlight] = Color.RED.rgb
            }
            val output = data.upScaled(scale)
            var path = outputPath
            if (!outputPath.endsWith(".png"))
                path += ".png"
            val outputFile = File(path)
            outputFile.mkdirs()

            ImageIO.write(output.toBufferedImage(), "png", outputFile)
        }

        @ExperimentalUnsignedTypes
        private fun setupDebug(
            source: IntArray2D,
            algorithm: Cartesian2DWfcAlgorithm,
            model: OverlappingImageModel,
            options: ImageAdapterOptions
        ) {
            if (options.debugOptions == null) return

            if (options.debugOptions.outputSource) {
                saveImage(
                    source.upScaled(options.debugOptions.sourceScale).toBufferedImage(),
                    options.debugOptions.outputPath,
                    "0_source"
                )
            }

            if (options.debugOptions.outputPatterns) {
                savePatternImages(
                    model,
                    "${options.debugOptions.outputPath}/patterns",
                    options.debugOptions.patternsScale
                )
            }

            var imageNumber = 0
            var animationNumber = 0
            var startNumber = 0
            var finishNumber = 0
            var stepNumber = 0
            var propNumber = 0
            var observeNumber = 0
            var banNumber = 0
            var writer: GifSequenceWriter? = null
            var outputStream: ImageOutputStream? = null
            var lock = true

            if (options.debugOptions.outputAnimation != null) {
                File(options.debugOptions.outputPath).mkdirs()
                val outputFile = File("${options.debugOptions.outputPath}/0_animation.gif")
                outputStream = FileImageOutputStream(outputFile)
                writer = GifSequenceWriter(
                    outputStream,
                    BufferedImage.TYPE_INT_ARGB,
                    1000 / options.debugOptions.outputAnimation.fps,
                    options.debugOptions.outputAnimation.loop
                )

                algorithm.afterFail += {
                    lock = true
                }
                algorithm.beforeStart += {
                    lock = false
                }

                for (event in options.debugOptions.outputAnimation.events) {
                    when (event) {
                        EventType.STEP ->
                            algorithm.afterStep += {
                                if (++animationNumber % options.debugOptions.outputAnimation.useEvery == 0) {
                                    writer.writeToSequence(
                                        model.constructOutput(algorithm)
                                            .upScaled(options.debugOptions.outputAnimation.outputScale)
                                            .toBufferedImage()
                                    )
                                }
                            }
                        EventType.PROPAGATION_STEP ->
                            algorithm.afterPropagationStep += {
                                if (++animationNumber % options.debugOptions.outputAnimation.useEvery == 0) {
                                    writer.writeToSequence(
                                        model.constructOutput(algorithm)
                                            .upScaled(options.debugOptions.outputAnimation.outputScale)
                                            .toBufferedImage()
                                    )
                                }
                            }
                        EventType.OBSERVATION ->
                            algorithm.afterObserve += {
                                if (++animationNumber % options.debugOptions.outputAnimation.useEvery == 0) {
                                    writer.writeToSequence(
                                        model.constructOutput(algorithm)
                                            .upScaled(options.debugOptions.outputAnimation.outputScale)
                                            .toBufferedImage()
                                    )
                                }
                            }
                        EventType.BAN ->
                            algorithm.afterBan += {
                                if (++animationNumber % options.debugOptions.outputAnimation.useEvery == 0) {
                                    writer.writeToSequence(
                                        model.constructOutput(algorithm)
                                            .upScaled(options.debugOptions.outputAnimation.outputScale)
                                            .toBufferedImage()
                                    )
                                }
                            }
                    }
                }
            }

            algorithm.afterClear += {
                saveCurrentOutputImage(
                    algorithm,
                    model,
                    "${options.debugOptions.outputPath}/${++imageNumber}_start_${++startNumber}",
                    options.debugOptions.outputScale
                )
                writer?.writeToSequence(
                    model.constructOutput(algorithm)
                        .upScaled(
                            options.debugOptions.outputAnimation?.outputScale ?: 1
                        )
                        .toBufferedImage()
                )
            }

            algorithm.afterFinished += {
                saveCurrentOutputImage(
                    algorithm,
                    model,
                    "${options.debugOptions.outputPath}/${++imageNumber}_finished_${++finishNumber}",
                    options.debugOptions.outputScale
                )
                writer?.writeToSequence(
                    model.constructOutput(algorithm)
                        .upScaled(
                            options.debugOptions.outputAnimation?.outputScale ?: 1
                        )
                        .toBufferedImage()
                )
                if (!lock) {
                    writer?.close()
                    outputStream?.close()
                }
            }

            for (event in options.debugOptions.outputImagesFromEvents) {
                when (event) {
                    EventType.STEP ->
                        algorithm.afterStep += {
                            saveCurrentOutputImage(
                                algorithm,
                                model,
                                "${options.debugOptions.outputPath}/${++imageNumber}_step_${++stepNumber}",
                                options.debugOptions.outputScale
                            )
                        }
                    EventType.PROPAGATION_STEP ->
                        algorithm.afterPropagationStep += {
                            saveCurrentOutputImage(
                                algorithm,
                                model,
                                "${options.debugOptions.outputPath}/${++imageNumber}_prop_${++propNumber}",
                                options.debugOptions.outputScale
                            )
                        }
                    EventType.OBSERVATION ->
                        algorithm.afterObserve += {
                            saveCurrentOutputImage(
                                algorithm,
                                model,
                                "${options.debugOptions.outputPath}/${++imageNumber}_observe_${++observeNumber}",
                                options.debugOptions.outputScale,
                                it.second + if (options.modelOptions.periodicOutput) 0 else model.overlap * (it.second / (model.outputWidth - model.overlap))
                            )
                        }
                    EventType.BAN ->
                        algorithm.afterBan += {
                            saveCurrentOutputImage(
                                algorithm,
                                model,
                                "${options.debugOptions.outputPath}/${++imageNumber}_ban_${++banNumber}",
                                options.debugOptions.outputScale,
                            )
                        }
                }
            }
        }

        @ExperimentalUnsignedTypes
        fun imitateImageUsingOverlappingModel(
            sourcePath: String,
            outputWidth: Int,
            outputHeight: Int,
            options: ImageAdapterOptions,
        ): Boolean {
            val source = ImageIO.read(File(sourcePath)).toIntArray2D()

            val model = OverlappingImageModel(
                source,
                options.overlap,
                outputWidth,
                outputHeight,
                options.modelOptions
            )
            val algorithm = model.build()

            setupDebug(source, algorithm, model, options)

            var repeat = 1
            var result: Boolean
            if (options.outputAnimationOptions != null) {
                File("${options.outputAnimationOptions.outputPath}").mkdirs()
                val outputFile =
                    File("${options.outputAnimationOptions.outputPath}/${options.outputAnimationOptions.fileName}.gif")
                var imageNumber = 0
                var lock = true
                val outputStream = FileImageOutputStream(outputFile)
                val writer = GifSequenceWriter(
                    outputStream,
                    BufferedImage.TYPE_INT_ARGB,
                    1000 / options.outputAnimationOptions.fps,
                    options.outputAnimationOptions.loop
                )

                algorithm.afterFail += {
                    lock = true
                }

                algorithm.beforeStart += {
                    writer.writeToSequence(
                        model.constructOutput(algorithm)
                            .upScaled(options.outputAnimationOptions.outputScale)
                            .toBufferedImage()
                    )
                    lock = false
                }

                algorithm.afterFinished += {
                    writer.writeToSequence(
                        model.constructOutput(algorithm)
                            .upScaled(options.outputAnimationOptions.outputScale)
                            .toBufferedImage()
                    )
                    if (!lock) {
                        writer.close()
                        outputStream.close()
                    }
                }

                for (event in options.outputAnimationOptions.events) {
                    when (event) {
                        EventType.STEP ->
                            algorithm.afterStep += {
                                if (++imageNumber % options.outputAnimationOptions.useEvery == 0) {
                                    writer.writeToSequence(
                                        model.constructOutput(algorithm)
                                            .upScaled(options.outputAnimationOptions.outputScale)
                                            .toBufferedImage()
                                    )
                                }
                            }
                        EventType.PROPAGATION_STEP ->
                            algorithm.afterPropagationStep += {
                                if (++imageNumber % options.outputAnimationOptions.useEvery == 0) {
                                    writer.writeToSequence(
                                        model.constructOutput(algorithm)
                                            .upScaled(options.outputAnimationOptions.outputScale)
                                            .toBufferedImage()
                                    )
                                }
                            }
                        EventType.OBSERVATION ->
                            algorithm.afterObserve += {
                                if (++imageNumber % options.outputAnimationOptions.useEvery == 0) {
                                    writer.writeToSequence(
                                        model.constructOutput(algorithm)
                                            .upScaled(options.outputAnimationOptions.outputScale)
                                            .toBufferedImage()
                                    )
                                }
                            }
                        EventType.BAN ->
                            algorithm.afterBan += {
                                if (++imageNumber % options.outputAnimationOptions.useEvery == 0) {
                                    writer.writeToSequence(
                                        model.constructOutput(algorithm)
                                            .upScaled(options.outputAnimationOptions.outputScale)
                                            .toBufferedImage()
                                    )
                                }
                            }
                    }
                }
            }

            val random = Random(options.seed)
            var actualSeed = random.nextInt()
            result = algorithm.run(actualSeed)
            println("Using custom generated seed $actualSeed.")

            while (!result && repeat++ < options.repeats) {
                println(
                    "Failed for ${repeat - 1}${
                        (when (repeat % 10) {
                            2 -> if (repeat != 12) "st" else "th"
                            3 -> if (repeat != 13) "nd" else "th"
                            4 -> if (repeat != 14) "rd" else "th"
                            else -> "th"
                        })
                    } time."
                )
                algorithm.clear()
                actualSeed = random.nextInt()
                println("Using custom generated seed $actualSeed.")

                result = algorithm.run(actualSeed)
            }

            if (!result) return false

            if (options.outputImageOptions != null) {
                saveCurrentOutputImage(
                    algorithm,
                    model,
                    "${options.outputImageOptions.outputPath}/${options.outputImageOptions.fileName + if (!options.outputImageOptions.includeSeed) "" else "_$actualSeed"}",
                    options.outputImageOptions.outputScale
                )
            }

            return true
        }
    }
}