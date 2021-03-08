package org.mifek.wfc.adapters.options

data class AnimationOptions(
    val outputPath: String,
    val fileName: String,
    val outputScale: Int = 1,
    val events: Array<EventType> = arrayOf(EventType.STEP),
    val useEvery: Int = 1,
    val fps: Int = 10,
    val loop: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnimationOptions

        if (outputPath != other.outputPath) return false
        if (fileName != other.fileName) return false
        if (outputScale != other.outputScale) return false
        if (!events.contentEquals(other.events)) return false
        if (useEvery != other.useEvery) return false
        if (fps != other.fps) return false
        if (loop != other.loop) return false

        return true
    }

    override fun hashCode(): Int {
        var result = outputPath.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + outputScale
        result = 31 * result + events.contentHashCode()
        result = 31 * result + useEvery
        result = 31 * result + fps
        result = 31 * result + loop.hashCode()
        return result
    }
}