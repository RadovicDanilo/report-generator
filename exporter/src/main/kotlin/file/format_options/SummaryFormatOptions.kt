package src.main.kotlin.file.format_options

import java.awt.Color

/**
 * A data class used to represent the formating of the summary section of a file
 *
 * @property keyColor key text color
 * @property keyBackgroundColor key background color
 * @property keyStyle style of the key test
 * @property valueColor value text color
 * @property valueBackgroundColor value background color
 * @property valueStyle style of value text
 * @property roundingPrecision round precision for double values
 * @property alignment alignment of the key and value
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