package src.main.kotlin.column

import src.main.kotlin.item.Item

open class Column<T : Any>(val header: String = "", val content: Array<Item<T>>)