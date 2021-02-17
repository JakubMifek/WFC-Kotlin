package org.mifek.wfc.utils

/**
 * Formats waves and prints to standard output
 */
fun formatWaves(waves: Array<BooleanArray>): String {
    return " " + waves.indices.joinToString(" ") { idx ->
        val s = waves[idx].sumBy {
            when (it) {
                true -> 1
                else -> 0
            }
        }
        val r = when (s) {
            0 -> "[XX]"
            1 -> "[" + (if (waves[idx].withIndex().find { it.value }!!.index < 10) "0" + waves[idx].withIndex()
                .find { it.value }!!.index.toString() else waves[idx].withIndex()
                .find { it.value }!!.index.toString()) + "]"
            else -> " " + (if (s < 10) " $s" else s.toString()) + " "
        }
        if ((idx + 1) % 10 == 0) (r + "\n") else r
    }
}

/**
 * Formats patterns and prints to standard output
 */
fun formatPatterns(patterns: Array<IntArray>): String {
    return patterns.mapIndexed { index, it ->
        "$index:\n${it.joinToString(" ")}"
    }.joinToString("\n\n")
}

/**
 * Formats propagator and prints to standard output
 */
fun formatPropagator(propagator: Array<Array<IntArray>>): String {
    val x = "   "
    var result = propagator[0].indices.joinToString(" ") {
        x.subSequence(0, x.length - it.toString().length).toString() + it.toString()
    } + "\n"
    val template = "00"
    for (dir in propagator.indices) {
        result += propagator[dir].joinToString(" ") {
                when {
                    it.isEmpty() -> "[X]"
                    it.size > 1 -> "[?]"
                    else -> template.slice(
                        0 until (3 - it[0].toString().length)
                    ) + it[0].toString()
                }
            } + "\n"
    }
    return result
}