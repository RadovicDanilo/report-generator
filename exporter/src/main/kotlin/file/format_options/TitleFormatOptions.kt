package src.main.kotlin.file.format_options

import java.awt.Color

/**
 * A data class used to represent the formating of the table
 *
 * @property fontStyle the font style of the title
 * @property fontSize the font size of the title
 * @property alignment the alignment of the title
 * @property color the text color of the title
 * @property backgroundColor the background color of the title
 */

data class TitleFormatOptions(
    val fontStyle: FontStyle = FontStyle.BOLD,
    val fontSize: Int = 24,
    val alignment: Alignment = Alignment.CENTER,
    val color: Color = Color.BLACK,
    val backgroundColor: Color = Color.WHITE
)