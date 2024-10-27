package src.main.kotlin.column

import src.main.kotlin.format_options.ColumnFormatOptions
import src.main.kotlin.item.Item

class FormatColumn<T : Any>(header: String = "", content: Array<Item<T>>, columnFormatOptions: ColumnFormatOptions) :
    Column<T>(header, content) {
}