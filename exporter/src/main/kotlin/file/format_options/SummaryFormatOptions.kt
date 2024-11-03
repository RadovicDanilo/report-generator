package src.main.kotlin.file.format_options

import java.awt.Color

data class SummaryFormatOptions(
    val keyColor: Color = Color.BLACK,
    val keyBackgroundColor: Color = Color.WHITE,
    val keyStyle: FontStyle = FontStyle.BOLD,
    val valueColor: Color = Color.BLACK,
    val valueBackgroundColor: Color = Color.WHITE,
    val valueStyle: FontStyle = FontStyle.NORMAL,
    val roundingPrecision: Int = 2,
    val alignment: Alignment = Alignment.RIGHT
)