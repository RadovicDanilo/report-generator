package src.main.kotlin.file

import src.main.kotlin.column.Column
import src.main.kotlin.format_options.CellFormatOptions
import src.main.kotlin.format_options.TableFormatOptions
import src.main.kotlin.format_options.TitleFormatOptions
import java.util.Collections

class FormatFile(
    title: String = "",
    val titleFormatOptions: TitleFormatOptions = TitleFormatOptions(),
    val tableFormatOptions: TableFormatOptions = TableFormatOptions(),
    val headerFormatOptions: CellFormatOptions = CellFormatOptions(),
    columns: List<Column<Any>>,
    includeRowNumbers: Boolean = false,
    val rowNumberFormat: CellFormatOptions = CellFormatOptions(),
    summary: Map<String, Any> = Collections.emptyMap(),
    val summaryFormatOptions: CellFormatOptions = CellFormatOptions(),
) : File(title, columns, includeRowNumbers, summary)