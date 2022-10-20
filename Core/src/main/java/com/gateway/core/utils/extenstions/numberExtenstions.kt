package com.gateway.core.utils.extenstions

import kotlin.math.round


fun Double?.toStringOrZero() = if (this != null) "${this.toDouble()}" else "0.0"

fun Long?.toStringOrNA() = if (this != null) "$this" else "N/A"

fun Int?.toStringOrNA() = if (this != null) "$this" else "N/A"
fun Int?.toStringOrEmpty() = if (this != null) "$this" else ""
fun Double?.toStringOrNA() = if (this != null) "$this" else "N/A"
fun Double?.toStringOrEmpty() = if (this != null) "$this" else ""
fun Long?.toStringOrEmpty() = if (this != null) "$this" else ""
fun String?.toIntOrZero() = runCatching { this?.toInt() ?: 0 }.getOrDefault(0)

fun Double.toStringFraction(fractions: Int = 2) = "%.${fractions}f".format(this)

fun Double?.round(decimals: Int = 5): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round((this ?: 0.0) * multiplier) / multiplier
}

fun Float.isInRange(range: ClosedFloatingPointRange<Float>): Boolean = this in range
