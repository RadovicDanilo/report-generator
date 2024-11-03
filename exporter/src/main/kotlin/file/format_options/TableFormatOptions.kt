package src.main.kotlin.file.format_options

import java.awt.Color

data class TableFormatOptions(
    val outerBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val outerBorderColor: Color = Color.BLACK,
    val horizontalBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val horizontalBorderColor: Color = Color.BLACK,
    val verticalBorderStyle: BorderStyle = BorderStyle.NORMAL,
    val verticalBorderColor: Color = Color.BLACK,
)