package src.main.kotlin.file.format_options

import java.awt.Color

data class CellFormatOptions(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val alignment: Alignment = Alignment.CENTER,
    val backgroundColor: Color = Color.WHITE,
    val textColor: Color = Color.BLACK
)