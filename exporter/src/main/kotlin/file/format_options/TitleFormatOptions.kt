package src.main.kotlin.file.format_options

import java.awt.Color

data class TitleFormatOptions(
    val fontStyle: FontStyle = FontStyle.BOLD,
    val fontSize: Int = 24,
    val alignment: Alignment = Alignment.CENTER,
    val color: Color = Color.BLACK,
    val backgroundColor: Color = Color.WHITE
)