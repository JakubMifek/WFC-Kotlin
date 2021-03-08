package org.mifek.wfc.adapters.options

data class DebugOptions(
    val outputPath: String,
    val outputSource: Boolean = false,
    val sourceScale: Int = 1,
    val outputPatterns: Boolean = false,
    val patternsScale: Int = 1,
    val outputAnimation: AnimationOptions? = null,
    val outputResultImage: Boolean = false,
    val outputImagesFromEvents: Array<EventType> = arrayOf(),
    val outputScale: Int = 1,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DebugOptions

        if (outputPath != other.outputPath) return false
        if (outputSource != other.outputSource) return false
        if (outputPatterns != other.outputPatterns) return false
        if (outputAnimation != other.outputAnimation) return false
        if (outputResultImage != other.outputResultImage) return false
        if (!outputImagesFromEvents.contentEquals(other.outputImagesFromEvents)) return false
        if (outputScale != other.outputScale) return false
        if (sourceScale != other.sourceScale) return false
        if (patternsScale != other.patternsScale) return false

        return true
    }

    override fun hashCode(): Int {
        var result = outputPath.hashCode()
        result = 31 * result + outputPatterns.hashCode()
        result = 31 * result + outputAnimation.hashCode()
        result = 31 * result + outputResultImage.hashCode()
        result = 31 * result + outputImagesFromEvents.contentHashCode()
        result = 31 * result + outputScale
        result = 31 * result + patternsScale
        return result
    }
}
