package src.main.kotlin.file.column

/**
 * A class representing a column of strings.
 *
 * @param header The header text of the column.
 * @param content The content of the column as an array of [String] values.
 */
internal open class StringColumn(header: String = "", content: Array<String>) : Column<String>(header, content)