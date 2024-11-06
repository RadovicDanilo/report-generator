package src.main.kotlin.file.format_options

import java.awt.Color

/**
 * A data class used to represent the formatting of the title.
 *
 * @property fontStyle The font style of the title.
 * @property fontSize The font size of the title.
 * @property alignment The alignment of the title text.
 * @property color The color of the title text.
 * @property backgroundColor The background color of the title.
 */

data class TitleFormatOptions(
    val fontStyle: FontStyle = FontStyle.BOLD,
    val fontSize: Int = 24,
    val alignment: Alignment = Alignment.CENTER,
    val color: Color = Color.BLACK,
    val backgroundColor: Color = Color.WHITE
)