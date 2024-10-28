package src.main.kotlin.file.column

abstract class Column<T : Any>(val header: String = "", val content: Array<T>)

