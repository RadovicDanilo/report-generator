package src.main.kotlin.format_options

import java.awt.Color

data class ColumnFormatOptions(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val alignment: Alignment = Alignment.CENTER,
    val backgroundColor: Color = Color.WHITE,
    val textColor: Color = Color.BLACK
)