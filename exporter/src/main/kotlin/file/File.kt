package src.main.kotlin.file

import src.main.kotlin.column.Column
import src.main.kotlin.format_options.CellFormatOptions
import java.util.Collections.emptyMap

open class File(
    val title: String = "",
    val columns: List<Column<Any>>,
    val includeRowNumbers: Boolean = false,
    val summary: Map<String, Any> = emptyMap()
)

class FormatFile(
    title: String = "",
    val headerFormatOptions: CellFormatOptions = CellFormatOptions(),
    columns: List<Column<Any>>,
    includeRowNumbers: Boolean = false,
    val rowNumberFormat: CellFormatOptions = CellFormatOptions(),
    summary: Map<String, Any> = emptyMap()
) : File(title, columns, includeRowNumbers, summary)
