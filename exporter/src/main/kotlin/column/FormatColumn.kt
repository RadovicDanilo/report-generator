package src.main.kotlin.column

import src.main.kotlin.format_options.FormatOptions
import src.main.kotlin.item.Item

class FormatColumn<T : Any>(header: String = "", content: Array<Item<T>>, columnFormatOptions: FormatOptions) :
    Column<T>(header, content) {
}