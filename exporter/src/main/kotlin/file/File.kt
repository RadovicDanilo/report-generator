package src.main.kotlin.file

import src.main.kotlin.column.Column
import java.util.Collections.emptyMap

open class File(
    val title: String = "",
    val columns: List<Column<*>>,
    includeRowNumbers: Boolean = false,
    summary: Map<String, Any> = emptyMap()
) {
}