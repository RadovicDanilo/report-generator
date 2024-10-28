package src.main.kotlin.file.format_options

import java.awt.Color

data class TitleFormatOptions(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderlined: Boolean = false,
    val fontSize: Int = 36,
    val alignment: Alignment = Alignment.CENTER,
    val color: Color = Color.BLACK
)