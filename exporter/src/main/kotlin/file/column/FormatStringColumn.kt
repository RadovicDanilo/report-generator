package src.main.kotlin.file.column

import src.main.kotlin.file.format_options.CellFormatOptions

/**
 * A class representing a formatted string column.
 *
 * @property columnFormatOptions The formatting options for the column cells.
 */
internal class FormatStringColumn(
    header: String = "",
    content: Array<String>,
    var columnFormatOptions: CellFormatOptions
) : StringColumn(header, content)