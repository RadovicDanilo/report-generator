package src.main.kotlin.format_options

import java.awt.Color

class SummaryFormatOptions(
    keyColor: Color = Color.BLACK,
    isKeyBold: Boolean = false,
    isKeyItalic: Boolean = false,
    isKeyUnderlined: Boolean = false,
    valueColor: Color = Color.BLACK,
    isValueBold: Boolean = false,
    isValueItalic: Boolean = false,
    isValueUnderlined: Boolean = false,
    roundDecimals: Int = 2
)