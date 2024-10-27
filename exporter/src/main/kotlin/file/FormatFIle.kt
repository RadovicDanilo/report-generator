package src.main.kotlin.file

import src.main.kotlin.column.FormatColumn
import src.main.kotlin.format_options.FormatOptions
import java.util.*

class FormatFIle(
    title: String = "",
    val headerFormatOptions: FormatOptions,
    columns: List<FormatColumn<*>>,
    includeRowNumbers: Boolean = false,
    val rowNumberFormat: FormatOptions = FormatOptions(),
    summary: Map<String, Any> = Collections.emptyMap()
) : File(title, columns, includeRowNumbers, summary) {
}