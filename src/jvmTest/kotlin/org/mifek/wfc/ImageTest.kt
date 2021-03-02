package org.mifek.wfc

import org.mifek.wfc.adapters.ImageAdapterOptions
import org.mifek.wfc.adapters.imitateImageUsingOverlappingModel
import kotlin.test.Test
import kotlin.test.assertTrue

class ImageTest {
    @Test
    fun flowerRatioTest() {
        val tries = 1
        var successes = 0
        for (i in 1..tries) {
            println("Try $i")
            successes += if (imitateImageUsingOverlappingModel(
                    "sources/flowers.png",
                    "outputs/ratio",
                    48,
                    48,
                    ImageAdapterOptions(
                        overlap = 2,
                        periodicInput = false,
                        periodicOutput = false,
                        grounded = true,
                        roofed = false,
                        outputScale = 8,
                        outputPatterns = true,
                        outputStepsAsAnimation = true,
                        useEveryNthStep = 16,
                        repeats = 1,
                        outputEachStep = true
//                        seed = 1
                    )
                )
            ) 1 else 0
        }
        println("Success ratio: ${successes.toDouble() / tries.toDouble()}")
    }

    @Test
    fun redDotTest() {
        for (i in 0..0) {
            if (i % 100 == 0) println(i)
            assertTrue(
                imitateImageUsingOverlappingModel(
                    "sources/red_rooms.png",
                    "outputs/encoder_test",
                    128,
                    72,
                    ImageAdapterOptions(
                        seed = i,
                        outputScale = 8,
                        outputStepsAsAnimation = true,
                        useEveryNthStep = 16,
                        periodicInput = true,
                        periodicOutput = true,
                        grounded = true,
                        roofed = true,
                        sided = true
                    )
                ), "Expected algorithm to be successful."
            )
        }
    }
//
//    @Test
//    fun redDotRectTest() {
//        assertTrue(
//            imitateImageUsingOverlappingModel(
//                "sources/red_rooms.png",
//                "outputs/rect_encoder_test",
//                128,
//                96,
//                seed = 123456789,
//                outputScale = 8,
////                outputStepsAsAnimation = true,
////                useEveryNthStep = 16,
//                outputEachStep = true
//            ), "Expected algorithm to be successful."
//        )
//    }
//
//    @Test
//    fun flowersTest() {
//        assertTrue(
//            imitateImageUsingOverlappingModel(
//                "sources/flowers.png",
//                "outputs/flowers_test",
//                32,
//                32,
//                seed = -12398167,
//                outputScale = 8,
////                outputStepsAsAnimation = true,
////                useEveryNthStep = 16,
//                overlap = 2,
//                allowRotations = false,
//                allowFlips = false,
//            ), "Expected algorithm to be successful."
//        )
//    }

//    @Test
//    fun flowerFailRatio() {
//        val result = imitateImageUsingOverlappingModel(
//            "sources/flowers.png",
//            "outputs/flowers_test_big_3",
//            64,
//            96,
//            repeats = 200,
//            outputScale = 8,
//            useEveryNthStep = 16,
//            outputStepsAsAnimation = true,
//            overlap = 2,
//            allowRotations = false,
//            allowFlips = false,
//        )
//        if (!result) {
//            println("Test unsuccessful")
//        }
//    }
}