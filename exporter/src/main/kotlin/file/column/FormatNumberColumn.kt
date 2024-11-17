package src.main.kotlin.file.column

import src.main.kotlin.file.format_options.CellFormatOptions

/**
 * A class representing a formatted number column.
 *
 * @property columnFormatOptions The formatting options for the column cells.
 */
internal class FormatNumberColumn(
    header: String = "",
    content: Array<Double>,
    var columnFormatOptions: CellFormatOptions
) : NumberColumn(header, content)