package src.main.kotlin.file

import src.main.kotlin.file.column.Column
import src.main.kotlin.file.format_options.CellFormatOptions
import src.main.kotlin.file.format_options.SummaryFormatOptions
import src.main.kotlin.file.format_options.TableFormatOptions
import src.main.kotlin.file.format_options.TitleFormatOptions
import java.util.Collections

/**
 * A class that represents a formatted file.
 *
 * @property filename The name of the exported file.
 * @property title The title of the file.
 * @property titleFormatOptions Formatting options for the title.
 * @property tableFormatOptions Formatting options for the table.
 * @property headerFormatOptions Formatting options for the header cells.
 * @property columns List of columns.
 * @property includeRowNumbers Include row numbers as the first column.
 * @property rowNumberFormat Formatting options for the row number column.
 * @property summary Map of key-value pairs used for summaries at the bottom of the report.
 * @property summaryFormatOptions Formatting options for the summary section.
 */

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
    val summaryFormatOptions: SummaryFormatOptions = SummaryFormatOptions(),
) : File(fileName, title, columns, includeRowNumbers, summary)