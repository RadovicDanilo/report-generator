package src.main.kotlin.file

import src.main.kotlin.column.Column
import src.main.kotlin.column.FormatColumn
import src.main.kotlin.format_options.ColumnFormatOptions
import java.util.*

class FormatFIle(
    title: String = "",
    columns: List<FormatColumn<*>>,
    includeRowNumbers: Boolean = false,
    val RowNumberFormat: ColumnFormatOptions = ColumnFormatOptions(),
    summary: Map<String, Any> = Collections.emptyMap()
) : File(title, columns, includeRowNumbers, summary) {
}