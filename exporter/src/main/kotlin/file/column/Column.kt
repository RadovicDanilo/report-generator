package src.main.kotlin.file.column

/**
 * An abstract class representing a generic column.
 *
 * @param T The type of content in the column, constrained to non-nullable types.
 * @property header The header text of the column.
 * @property content The content of the column as an array of type [T].
 */
internal abstract class Column<T : Any>(val header: String = "", val content: Array<T>)
