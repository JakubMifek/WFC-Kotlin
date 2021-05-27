package org.mifek.wfc

import org.mifek.wfc.adapters.ImageAdapter
import org.mifek.wfc.adapters.options.AnimationOptions
import org.mifek.wfc.adapters.options.EventType
import org.mifek.wfc.adapters.options.ImageAdapterOptions
import org.mifek.wfc.adapters.options.ImageOptions
import org.mifek.wfc.models.options.Cartesian2DModelOptions
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

class ImageTest {
    @ExperimentalUnsignedTypes
    @Test
    fun bricks() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/bricks.png", 64, 64,
                ImageAdapterOptions(
                    overlap = 2,
//                    outputImageOptions = ImageOptions(
//                        "outputs/bricks",
//                        "0_result",
//                        8
//                    ),
                    seed = 0,
//                    repeats = 5,
//                        debugOptions = DebugOptions(
//                            outputPath = "outputs/bricks/debug",
//                            outputSource = true,
//                            sourceScale = 4,
//                            outputPatterns = true,
//                            patternsScale = 4,
//                        )
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun cat() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/cat.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 3,
                    modelOptions = Cartesian2DModelOptions(
                        periodicInput = true
                    ),
//                    debugOptions = DebugOptions(
//                        "outputs/cat/debug",true,16
//                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/cat",
//                        "0_result",
//                        8,
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/cat",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.OBSERVATION, EventType.PROPAGATION_STEP),
//                        16,
//                        16,
//                        true
//                    ),
                    repeats = 5,
                    seed = 123151
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun dungeon() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/dungeon.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicInput = true,
                        periodicOutput = true,
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        allowRotations = true
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/dungeon",
//                        "0_result",
//                        8
//                    ),
                    seed = 1123890487
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun flowers() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/flowers.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        grounded = true,
                        banGroundElsewhere = true,
                        roofed = true
                    ),
//                    debugOptions = DebugOptions(
//                        "outputs/flowers/debug",true,16
//                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/flowers",
//                        "0_result",
//                        8
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/flowers",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.OBSERVATION, EventType.PROPAGATION_STEP),
//                        16,
//                        16,
//                        true
//                    ),
                    seed = 321123123
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun knot() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/knot.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicInput = true,
                        periodicOutput = true,
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/knot",
//                        "0_result",
//                        8
//                    ),
                    seed = 123414
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun lake() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/lake.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
//                    outputImageOptions = ImageOptions(
//                        "outputs/lake",
//                        "0_result",
//                        8
//                    ),
                    seed = 2
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun lessRooms() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/less_rooms.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
//                    outputImageOptions = ImageOptions(
//                        "outputs/less_rooms",
//                        "0_result",
//                        8
//                    ),
                    seed = 1123890487
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun link() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/link.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicInput = true,
                        periodicOutput = true,
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        allowRotations = true,
                    ),
//                    debugOptions = DebugOptions(
//                        "outputs/link/debug",true,16
//                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/link",
//                        "0_result",
//                        8
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/link",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.STEP),
//                        1,
//                        200,
//                        true
//                    ),
                    seed = 515987
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun moreFlowers() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/more_flowers.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicOutput = true,
                        periodicInput = true,
                        banGroundElsewhere = true,
                        grounded = true,
                        roofed = true
                    ),
//                    debugOptions = DebugOptions(
//                        "outputs/more_flowers/debug",true,16
//                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/more_flowers",
//                        "0_result",
//                        8
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/more_flowers",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.COLLAPSE),
//                        1,
//                        64,
//                        true
//                    ),
                    seed = 12345678
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun moreSkyline() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/more_skyline.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
//                    outputImageOptions = ImageOptions(
//                        "outputs/more_skyline",
//                        "0_result",
//                        8
//                    ),
//                    debugOptions = DebugOptions(
//                        "outputs/more_skyline/debug",true,16
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/more_skyline",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.OBSERVATION, EventType.PROPAGATION_STEP),
//                        16,
//                        16,
//                        true
//                    ),
                    seed = 1123890487
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun redRooms() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/red_rooms.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 1,
                    modelOptions = Cartesian2DModelOptions(
                        allowRotations = true,
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        periodicInput = true,
                        periodicOutput = true
                    ),
//                    debugOptions = DebugOptions(
//                        "outputs/red_rooms/debug",true,16
//                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/red_rooms",
//                        "0_result",
//                        8
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/red_rooms",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.OBSERVATION, EventType.PROPAGATION_STEP),
//                        16,
//                        16,
//                        true
//                    ),
                    seed = 123456
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun rooms() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/rooms.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        allowRotations = true,
                        periodicOutput = true,
                        periodicInput = true
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/rooms",
//                        "0_result",
//                        8
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/rooms",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.OBSERVATION, EventType.PROPAGATION_STEP),
//                        16,
//                        16,
//                        true
//                    ),
//                    debugOptions = DebugOptions(
//                        "outputs/rooms/debug",
//                        true,
//                        16,
//                    ),
                    seed = 1123890487
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun simpleKnot() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/simple_knot.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        allowRotations = true,
                        periodicOutput = true,
                        periodicInput = true
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/simple_knot",
//                        "0_result",
//                        8
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/simple_knot",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.OBSERVATION, EventType.PROPAGATION_STEP),
//                        16,
//                        16,
//                        true
//                    ),
                    seed = 12341521
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun simpleWall() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/simple_wall.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        allowRotations = true,
                        periodicOutput = true,
                        periodicInput = true
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/simple_wall",
//                        "0_result",
//                        8
//                    ),
                    seed = 31241243
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun skyline() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/skyline.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicOutput = true,
                        periodicInput = true,
                        grounded = true,
                        roofed = true
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/skyline",
//                        "0_result",
//                        8
//                    ),
                    seed = 21515123
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun town() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/town.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicInput = true,
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/town",
//                        "0_result",
//                        8
//                    ),
                    seed = 1123890487
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun trickKnot() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/trick_knot.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 3,
                    modelOptions = Cartesian2DModelOptions(
                        periodicOutput = true,
                        periodicInput = true,
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        allowRotations = true,
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/trick_knot",
//                        "0_result",
//                        8
//                    ),
                    seed = 1123890487
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun village() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/village.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicOutput = true,
                        periodicInput = true,
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/village",
//                        "0_result",
//                        8
//                    ),
                    seed = 1123890487
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun wall() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/wall.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
//                    outputImageOptions = ImageOptions(
//                        "outputs/wall",
//                        "0_result",
//                        8
//                    ),
                    repeats = 5,
                    seed = 1123890487
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun water() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/water.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicOutput = true,
                        periodicInput = true
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/water",
//                        "0_result",
//                        8
//                    ),
                    seed = 1123890487
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun office() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/office.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicOutput = true,
                        periodicInput = true
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/office",
//                        "0_result",
//                        8
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/office",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.OBSERVATION, EventType.PROPAGATION_STEP),
//                        16,
//                        16,
//                        true
//                    ),
//                    debugOptions = DebugOptions(
//                        "outputs/office/debug",
//                        true,
//                        16,
//                    ),
                    seed = 12431512
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun fabric() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/fabric.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicOutput = true,
                        periodicInput = true,
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        allowRotations = true
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/fabric",
//                        "0_result",
//                        8
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/fabric",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.OBSERVATION, EventType.PROPAGATION_STEP),
//                        16,
//                        16,
//                        true
//                    ),
                    seed = 21315
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun fabricSmall() {
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/fabric_small.png", 48, 48,
                ImageAdapterOptions(
                    overlap = 2,
                    modelOptions = Cartesian2DModelOptions(
                        periodicOutput = false,
                        periodicInput = true,
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        allowRotations = true
                    ),
//                    outputImageOptions = ImageOptions(
//                        "outputs/fabric_small",
//                        "0_result",
//                        8
//                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/fabric_small",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.OBSERVATION, EventType.PROPAGATION_STEP),
//                        16,
//                        16,
//                        true
//                    ),
                    seed = 123151
                ),
            )
        )
    }

    @ExperimentalUnsignedTypes
    @Test
    fun floorPlan() {
        val w = 32
        val h = 32
        assertTrue(
            ImageAdapter.imitateImageUsingOverlappingModel(
                "sources/floor_plan.png", w, h,
                ImageAdapterOptions(
                    overlap = 2,
                    repeats = 5,
                    modelOptions = Cartesian2DModelOptions(
                        periodicOutput = false,
                        periodicInput = false,
                        allowHorizontalFlips = true,
                        allowVerticalFlips = true,
                        allowRotations = true,
                        roofed = true,
                        grounded = true,
                        leftSided = true,
                        rightSided = true,
                        weightPower = 1.0 / 3.0
                    ),
                    outputImageOptions = ImageOptions(
                        "outputs/floor_plan",
                        "0_result",
                        8
                    ),
//                    outputAnimationOptions = AnimationOptions(
//                        "outputs/floor_plan",
//                        "1_animation",
//                        8,
//                        arrayOf(EventType.COLLAPSE),
//                        1,
//                        64,
//                        true
//                    ),
//                    setPixels = Iterable {
//                        iterator {
//                            val wallPixel = org.mifek.wfc.utils.rgbToInt(0u, 0u, 0u)
//                            yield(Pair(Pair(2, 2), wallPixel))
//                            yield(Pair(Pair(w - 3, 2), wallPixel))
//                            yield(Pair(Pair(2, h - 3), wallPixel))
//                            yield(Pair(Pair(w - 3, h - 3), wallPixel))
//                        }
//                    },
                    banPixels = Iterable {
                        iterator {
                            val exteriorPixel = org.mifek.wfc.utils.rgbToInt(255u, 255u, 255u)
                            for (x in w / 4 until 3 * w / 4) {
                                for (y in h / 4 until 3 * h / 4) {
                                    yield(Pair(Pair(x, y), exteriorPixel))
                                }
                            }
                        }
                    },
                    seed = 12345,
                ),
            )
        )
    }
}