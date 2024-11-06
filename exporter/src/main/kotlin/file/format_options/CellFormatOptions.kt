package src.main.kotlin.file.format_options

import java.awt.Color

/**
 * A class that represents a cell style used for formatting columns and rows.
 *
 * @property fontStyle The font style of the cell's text.
 * @property fontSize The size of the cell's text.
 * @property alignment The alignment of the cell's text.
 * @property backgroundColor The background color of the cell.
 * @property textColor The text color of the cell.
 */
data class CellFormatOptions(
    var fontStyle: FontStyle = FontStyle.NORMAL,
    var fontSize: Int = 12,
    var alignment: Alignment = Alignment.LEFT,
    var backgroundColor: Color = Color.WHITE,
    var textColor: Color = Color.BLACK
)
