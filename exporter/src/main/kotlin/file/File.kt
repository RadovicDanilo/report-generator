package src.main.kotlin.file

import src.main.kotlin.column.Column
import src.main.kotlin.format_options.FormatOptions
import java.util.Collections.emptyMap

open class File(
    val title: String = "",
    val columns: List<Column<Any>>,
    val includeRowNumbers: Boolean = false,
    val summary: Map<String, Any> = emptyMap()
)

class FormatFile(
    title: String = "",
    val headerFormatOptions: FormatOptions = FormatOptions(),
    columns: List<Column<Any>>,
    includeRowNumbers: Boolean = false,
    val rowNumberFormat: FormatOptions = FormatOptions(),
    summary: Map<String, Any> = emptyMap()
) : File(title, columns, includeRowNumbers, summary)
