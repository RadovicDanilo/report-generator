package src.main.kotlin.column

import src.main.kotlin.format_options.CellFormatOptions

class FormatNumberColumn(
    header: String = "",
    content: Array<Double>,
    val columnFormatOptions: CellFormatOptions
) : NumberColumn(header, content)