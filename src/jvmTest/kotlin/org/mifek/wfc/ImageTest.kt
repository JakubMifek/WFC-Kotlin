package org.mifek.wfc

import org.mifek.wfc.adapters.imitateImageUsingOverlappingModel
import kotlin.test.Test

class ImageTest {
    @Test
    fun flowerRatioTest() {
        val tries = 100
        var successes = 0
        for (i in 1..tries) {
            successes += if (imitateImageUsingOverlappingModel(
                    "sources/flowers.png",
                    "outputs/ratio",
                    16,
                    16,
                    outputScale = 8,
                    overlap = 2,
                    allowRotations = false,
                    allowFlips = false
                )
            ) 1 else 0
        }
        println("Success ratio: ${successes.toDouble() / tries.toDouble()}")
    }

//    @Test
//    fun redDotTest() {
//        assertTrue(
//            imitateImageUsingOverlappingModel(
//                "sources/red_rooms.png",
//                "outputs/encoder_test",
//                64,
//                64,
//                seed = 123456,
//                outputScale = 8,
////                useEveryNthStep = 16
//            ), "Expected algorithm to be successful."
//        )
//    }
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
////                useEveryNthStep = 16
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