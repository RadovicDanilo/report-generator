package src.main.kotlin.file

import src.main.kotlin.column.Column
import java.util.Collections.emptyMap

open class File(
    val title: String = "",
    val columns: List<Column<Any>>,
    val includeRowNumbers: Boolean = false,
    val summary: Map<String, Any> = emptyMap()
)

