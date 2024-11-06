package src.main.kotlin.file.column

import src.main.kotlin.file.format_options.CellFormatOptions

class FormatStringColumn(
    header: String = "",
    content: Array<String>,
    var columnFormatOptions: CellFormatOptions
) : StringColumn(header, content)