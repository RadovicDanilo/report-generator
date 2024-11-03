package src.main.kotlin.file.format_options

import java.awt.Color

data class CellFormatOptions(
    val fontStyle: FontStyle = FontStyle.NORMAL,
    val fontSize: Int = 12,
    val alignment: Alignment = Alignment.LEFT,
    val backgroundColor: Color = Color.WHITE,
    val textColor: Color = Color.BLACK
)
