package src.main.kotlin.file

import src.main.kotlin.file.column.Column
import src.main.kotlin.file.column.NumberColumn
import src.main.kotlin.file.column.StringColumn

class FileBuilder(
    private var filename: String
) {
    var title: String = ""
    var columns: MutableList<Column<Any>> = mutableListOf()
    var includeRowNumbers: Boolean = false
    var summary: MutableMap<String, Any> = mutableMapOf()

    fun validateFilename(filename: String): Boolean {
        val windowsFilenameRegex = Regex("^[a-zA-Z0-9_-]+$")
        return windowsFilenameRegex.matches(filename) && !filename.endsWith(" ") && !filename.endsWith(".")
    }

    fun setFileName(filename: String) {
        require(validateFilename(filename)) { "Invalid filename format" }
        this.filename = filename
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun includeRowNumbers(includeRowNumbers: Boolean) {
        this.includeRowNumbers = includeRowNumbers
    }

    fun setColumns(content: Array<Array<Any>>) {
        columns = mutableListOf()
        addColumns(content)
    }

    fun setColumns(headers: Array<String>, content: Array<Array<Any>>) {
        columns = mutableListOf()
        addColumns(headers, content)
    }

    fun setColumns(data: Map<String, Array<Any>>) {
        columns = mutableListOf()
        addColumns(data)
    }

    fun addColumns(content: Array<Array<Any>>) {
        for (column in content) {
            addColumn(column)
        }
    }

    fun addColumns(headers: Array<String>, content: Array<Array<Any>>) {
        require(headers.size == content.size) { "Headers and content arrays must have the same length" }

        for (i in headers.indices) {
            val header = headers[i]
            val column = content[i]
            addColumn(header, column)
        }
    }

    fun addColumns(data: Map<String, Array<Any>>) {
        for ((header, columnData) in data) {
            addColumn(header, columnData)
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

    fun addColumn(header: String, content: Array<Any>) {
        when (content.firstOrNull()) {
            is String -> columns.add(StringColumn(header, content as Array<String>) as Column<Any>)
            is Number -> columns.add(NumberColumn(header, content as Array<Double>) as Column<Any>)
            else -> throw IllegalArgumentException("Unsupported column type")
        }
    }

    fun addColumn(content: Array<Any>) {
        when (content.firstOrNull()) {
            is String -> columns.add(StringColumn(content = content as Array<String>) as Column<Any>)
            is Number -> columns.add(NumberColumn(content = content as Array<Double>) as Column<Any>)
            else -> throw IllegalArgumentException("Unsupported column type")
        }
    }

    // TODO Calculated columns

    fun addSummaryEntry(key: String, value: Any) {
        this.summary[key] = value
    }

    fun addSummaryEntries(entries: Map<String, Any>) {
        this.summary.putAll(entries)
    }

    fun setSummaryEntries(entries: Map<String, Any>) {
        this.summary = entries.toMutableMap()
    }

    //TODO calculations

    fun build(): File {
        return File(filename, title, columns, includeRowNumbers, summary)
    }

}