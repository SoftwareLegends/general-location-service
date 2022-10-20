package com.gateway.core.utils.extenstions

import com.gateway.core.utils.extenstions.RegexPatterns.DIGITS
import com.gateway.core.utils.extenstions.RegexPatterns.EMAIL
import com.gateway.core.utils.extenstions.RegexPatterns.EMOJI

val String.noWhitespace: String get() = filter { it.isWhitespace().not() }

val String.noEmoji: String get() = filter { isEmoji.not() }

fun String.digitOnly(
    isDots: Boolean = false,
    isDash: Boolean = false
): String {
    var text = ""

    forEachIndexed { index, char ->
        text += if (char.isDigit())
            char
        else if (isDots && char == '.' && index != 0 && text.contains(char = '.').not())
            char
        else if (isDash && char == '-' && index == 0)
            char
        else ""
    }
    return text
}

val String.isEmoji: Boolean get() = EMOJI.toRegex().containsMatchIn(this)

val String.isEmail: Boolean get() = EMAIL.toRegex().matches(this)

private var minLengthValue: Int = 8

var String.minLength: Int
    get() = minLengthValue
    set(value) {
        minLengthValue = value
    }

val String.isMinLength: Boolean get() = trim().length >= minLength

val String.isEmpty: Boolean get() = trim().isEmpty()

val String.isPhone: Boolean
    get() = trim().run {
        DIGITS.toRegex().matches(this) && length == 10
    }

fun String?.toStringOrNA() = if (this != null) "$this" else "N/A"

fun String?.toStringOrEmpty() = if (this != null) "$this" else ""

fun String.isInRange(range: IntRange): Boolean =
    runCatching { toIntOrNull() in range }.getOrDefault(false)

fun String.isInRange(range: ClosedFloatingPointRange<Double>): Boolean =
    runCatching { toDouble() in range }.getOrDefault(false)

fun String.isLengthInRange(range: IntRange): Boolean =
    runCatching { length in range }.getOrDefault(false)

fun String.meterToFloat() = replace("m", "").toFloatOrNull() ?: Float.MAX_VALUE

private object RegexPatterns {
    const val DIGITS = "[0-9]+"
    const val EMOJI = "\\p{So}"
    const val EMAIL = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
}
