package src.main.kotlin.file

import src.main.kotlin.file.column.Column
import src.main.kotlin.file.column.NumberColumn
import src.main.kotlin.file.column.StringColumn

class FileBuilder {
    lateinit var filename: String
    var title: String = ""
    var columns: MutableList<Column<Any>> = mutableListOf()
    var includeRowNumbers: Boolean = false
    var summary: Map<String, Any> = emptyMap()

    fun setFileName(filename: String) {
        this.filename = filename
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setColumns(content: Array<Array<Any>>) {
        for (column in content) {
            if (column.isEmpty()) continue
            when (column[0]) {
                is String -> columns.add(StringColumn(content = column as Array<String>) as Column<Any>)
                is Number -> columns.add(NumberColumn(content = column as Array<Double>) as Column<Any>)
                else -> throw IllegalArgumentException("Unsupported column type")
            }
        }
    }

    fun setColumns(headers: Array<String>, content: Array<Array<Any>>) {
        require(headers.size == content.size) { "Headers and content arrays must have the same length" }

        for (i in headers.indices) {
            val header = headers[i]
            val column = content[i]

            if (column.isEmpty()) continue

            when (column[0]) {
                is String -> columns.add(StringColumn(header, column as Array<String>) as Column<Any>)
                is Number -> columns.add(NumberColumn(header, column as Array<Double>) as Column<Any>)
                else -> throw IllegalArgumentException("Unsupported column type")
            }
        }
    }

    fun addStringColumn(strings: Array<String>) {
        columns.add(StringColumn(content = strings) as Column<Any>)
    }

    fun addStringColumn(header: String, strings: Array<String>) {
        columns.add(StringColumn(header, strings) as Column<Any>)
    }

    fun addNumberColumn(numbers: Array<Number>) {
        columns.add(NumberColumn(content = numbers as Array<Double>) as Column<Any>)
    }

    fun addNumberColumn(header: String, numbers: Array<Number>) {
        columns.add(NumberColumn(header, numbers as Array<Double>) as Column<Any>)
    }


    fun includeRowNumbers(includeRowNumbers: Boolean) {
        this.includeRowNumbers = includeRowNumbers
    }

    fun addSummaryEntry(key: String, value: Any) {

    }

    fun addSummaryEntries(entries: Map<String, Any>) {

    }

    fun setSummaryEntries(entries: Map<String, Any>) {
        this.summary = entries
    }
}