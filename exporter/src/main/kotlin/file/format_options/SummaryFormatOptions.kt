package src.main.kotlin.file.format_options

import java.awt.Color

data class SummaryFormatOptions(
    val keyColor: Color = Color.BLACK,
    val isKeyBold: Boolean = false,
    val isKeyItalic: Boolean = false,
    val isKeyUnderlined: Boolean = false,
    val valueColor: Color = Color.BLACK,
    val isValueBold: Boolean = false,
    val isValueItalic: Boolean = false,
    val isValueUnderlined: Boolean = false,
    val roundDecimals: Int = 2
)