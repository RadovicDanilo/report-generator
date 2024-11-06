package src.main.kotlin.file.format_options

import java.awt.Color

data class CellFormatOptions(
    var fontStyle: FontStyle = FontStyle.NORMAL,
    var fontSize: Int = 12,
    var alignment: Alignment = Alignment.LEFT,
    var backgroundColor: Color = Color.WHITE,
    var textColor: Color = Color.BLACK
)
