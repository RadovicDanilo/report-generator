package src.main.kotlin.file.format_options

import java.awt.Color

/**
 * A data class used to represent the formatting of the table.
 *
 * @property outerBorderStyle The style of the outer border.
 * @property outerBorderColor The color of the outer border.
 * @property horizontalBorderStyle The style of the inner horizontal borders.
 * @property horizontalBorderColor The color of the inner horizontal borders.
 * @property verticalBorderStyle The style of the inner vertical borders.
 * @property verticalBorderColor The color of the inner vertical borders.
 */

data class TableFormatOptions(
    val outerBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val outerBorderColor: Color = Color.BLACK,
    val horizontalBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val horizontalBorderColor: Color = Color.BLACK,
    val verticalBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val verticalBorderColor: Color = Color.BLACK,
)