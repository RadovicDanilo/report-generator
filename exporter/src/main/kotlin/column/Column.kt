package src.main.kotlin.column

import src.main.kotlin.format_options.FormatOptions

abstract class Column<T : Any>(val header: String = "", val content: Array<T>)
open class NumberColumn<T : Number>(header: String = "", content: Array<T>) : Column<T>(header, content)
open class StringColumn(header: String = "", content: Array<String>) : Column<String>(header, content)

class FormatNumberColumn<T : Number>(
    header: String = "",
    content: Array<T>,
    val columnFormatOptions: FormatOptions
) : NumberColumn<T>(header, content)

class FormatStringColumn(
    header: String = "",
    content: Array<String>,
    val columnFormatOptions: FormatOptions
) : StringColumn(header, content)