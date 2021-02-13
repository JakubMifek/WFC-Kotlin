package org.mifek.wfc

import org.mifek.wfc.adapters.imitateImageUsingOverlappingModel
import kotlin.test.Test
import kotlin.test.assertTrue

class ImageTest {
    @Test
    fun redDotTest() {
        assertTrue(imitateImageUsingOverlappingModel(
            "sources/red_rooms.png",
            "outputs/encoder_test",
            64,
            64,
            seed = 123456,
            outputScale = 8,
            useEveryNthStep = 16
        ), "Expected algorithm to be successful.")
    }

    @Test
    fun redDotRectTest() {
        assertTrue(imitateImageUsingOverlappingModel(
            "sources/red_rooms.png",
            "outputs/rect_encoder_test",
            128,
            96,
            seed = 123456789,
            outputScale = 8,
            outputStepsAsAnimation = true,
            useEveryNthStep = 16
        ), "Expected algorithm to be successful.")
    }

    @Test
    fun flowersTest() {
        assertTrue(imitateImageUsingOverlappingModel(
            "sources/flowers.png",
            "outputs/flowers_test",
            32,
            32,
            seed = 123456,
            outputScale = 8,
            outputStepsAsAnimation = true,
            useEveryNthStep = 16,
            overlap = 2,
            allowRotations = false,
            allowFlips = false,
        ), "Expected algorithm to be successful.")
    }

    @Test
    fun flowerFailRatio() {
        val tests = 1000
        for (i in 0 until tests) {
            val result = imitateImageUsingOverlappingModel(
                "sources/flowers.png",
                "outputs/flowers_test_big_2",
                64,
                96,
                outputScale = 8,
                // useEveryNthStep = 16,
                // outputStepsAsAnimation = true,
                overlap = 2,
                allowRotations = false,
                allowFlips = false,
            )
            if(result) {
                return
            } else {
                println("Fail ${i+1}")
            }
        }
    }
}