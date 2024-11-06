package src.main.kotlin.file

import src.main.kotlin.file.column.Column
import java.util.Collections.emptyMap

/**
 * A class that represents a non-formatted file
 *
 * @property filename The name of the exported file.
 * @property title The title of the file.
 * @property columns List of columns.
 * @property includeRowNumbers Include row numbers as the first column.
 * @property summary Map of key-value pairs used for summaries at the bottom of the report.
 */
open class File(
    val filename: String,
    val title: String = "",
    val columns: List<Column<Any>>,
    val includeRowNumbers: Boolean = false,
    val summary: Map<String, Any> = emptyMap()
)