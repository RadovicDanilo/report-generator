package src.main.kotlin.file.column

/**
 * A class representing a column of numbers.
 *
 * @param header The header text of the column.
 * @param content The content of the column as an array of [Double] values.
 */
internal open class NumberColumn(header: String = "", content: Array<Double>) : Column<Double>(header, content)

