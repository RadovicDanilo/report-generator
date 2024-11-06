package src.main.kotlin.file.column

import src.main.kotlin.file.format_options.CellFormatOptions

class FormatNumberColumn(
    header: String = "",
    content: Array<Double>,
    var columnFormatOptions: CellFormatOptions
) : NumberColumn(header, content)