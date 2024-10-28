package src.main.kotlin.column

import src.main.kotlin.format_options.CellFormatOptions

class FormatStringColumn(
    header: String = "",
    content: Array<String>,
    val columnFormatOptions: CellFormatOptions
) : StringColumn(header, content)