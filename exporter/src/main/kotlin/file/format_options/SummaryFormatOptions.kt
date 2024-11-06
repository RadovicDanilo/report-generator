package src.main.kotlin.file.format_options

import java.awt.Color

/**
 * A data class used to represent the formatting of the summary section of a file.
 *
 * @property keyColor The color of the key text.
 * @property keyBackgroundColor The background color of the key.
 * @property keyStyle The style of the key text.
 * @property valueColor The color of the value text.
 * @property valueBackgroundColor The background color of the value.
 * @property valueStyle The style of the value text.
 * @property roundingPrecision The rounding precision for double values.
 * @property alignment The alignment of the key and value.
 */

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