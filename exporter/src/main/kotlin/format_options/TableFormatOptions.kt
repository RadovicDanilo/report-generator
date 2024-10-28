package src.main.kotlin.format_options

import java.awt.Color

enum class BorderStyle {
    NORMAL, BOLD, DASHED
}

data class TableFormatOptions(
    val outerBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val outerBorderColumn: Color = Color.BLACK,
    val horizontalBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val horizontalBorderColor: Color = Color.BLACK,
    val verticalBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val verticalBorderColor: Color = Color.BLACK,
)