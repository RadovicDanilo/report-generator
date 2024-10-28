package src.main.kotlin.column

abstract class Column<T : Any>(val header: String = "", val content: Array<T>)

