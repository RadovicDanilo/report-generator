package src.main.kotlin.file.format_options

import java.awt.Color

/**
 * A data class used to represent the formating of the table
 *
 * @property outerBorderColor the style of the outer border
 * @property outerBorderColor the color of the outer border
 * @property outerBorderColor the style of the inner horizontal border
 * @property outerBorderColor the color of the inner horizontal border
 * @property outerBorderColor the style of the inner vertical border
 * @property outerBorderColor the color of the inner vertical border
 */
data class TableFormatOptions(
    val outerBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val outerBorderColor: Color = Color.BLACK,
    val horizontalBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val horizontalBorderColor: Color = Color.BLACK,
    val verticalBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val verticalBorderColor: Color = Color.BLACK,
)