package src.main.kotlin.file.format_options

import java.awt.Color

/**
 * A class that represents a cell style use for formating columns and rows
 *
 * @property fontStyle the font style of cells text
 * @property fontSize size of cells text
 * @property alignment alignment of cells text
 * @property backgroundColor color of the cells background
 * @property textColor color of the cells text
 */
data class CellFormatOptions(
    var fontStyle: FontStyle = FontStyle.NORMAL,
    var fontSize: Int = 12,
    var alignment: Alignment = Alignment.LEFT,
    var backgroundColor: Color = Color.WHITE,
    var textColor: Color = Color.BLACK
)
