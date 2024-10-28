package src.main.kotlin.file

import src.main.kotlin.file.column.Column
import src.main.kotlin.file.format_options.CellFormatOptions
import src.main.kotlin.file.format_options.TableFormatOptions
import src.main.kotlin.file.format_options.TitleFormatOptions
import java.util.Collections

class FormatFile(
    fileName: String,
    title: String = "",
    val titleFormatOptions: TitleFormatOptions = TitleFormatOptions(),
    val tableFormatOptions: TableFormatOptions = TableFormatOptions(),
    val headerFormatOptions: CellFormatOptions = CellFormatOptions(),
    columns: List<Column<Any>>,
    includeRowNumbers: Boolean = false,
    val rowNumberFormat: CellFormatOptions = CellFormatOptions(),
    summary: Map<String, Any> = Collections.emptyMap(),
    val summaryFormatOptions: CellFormatOptions = CellFormatOptions(),
) : File(fileName, title, columns, includeRowNumbers, summary)