package src.main.kotlin.file

import src.main.kotlin.file.column.CalculatedColumn
import src.main.kotlin.file.column.Calculation
import src.main.kotlin.file.column.Column
import src.main.kotlin.file.column.NumberColumn
import src.main.kotlin.file.column.StringColumn
import java.sql.Connection
import java.sql.ResultSet

open class FileBuilder(
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

    fun addCalculatedColumn(header: String, columns: Array<Array<Number>>, calculation: Calculation) {
        val arr: MutableList<NumberColumn> = mutableListOf()

        for (column in columns) {
            val doubleColumn = column.map { it.toDouble() }.toTypedArray()
            arr.add(NumberColumn(header, doubleColumn))
        }

        val calculatedColumn = CalculatedColumn(columnsForCalculations = arr.toTypedArray(), calculation = calculation)
        this.columns.add(calculatedColumn as Column<Any>)
    }

    fun addCalculatedColumn(columns: Array<Array<Number>>, calculation: Calculation) {
        val arr: MutableList<NumberColumn> = mutableListOf()

        for (column in columns) {
            val doubleColumn = column.map { it.toDouble() }.toTypedArray()
            arr.add(NumberColumn(content = doubleColumn))
        }

        val calculatedColumn = CalculatedColumn(columnsForCalculations = arr.toTypedArray(), calculation = calculation)
        this.columns.add(calculatedColumn as Column<Any>)
    }

    fun setColumnsFromSQL(query: String, connection: Connection) {
        columns = mutableListOf()
        addColumnsFromSQL(query, connection)
    }

    fun addColumnsFromSQL(query: String, connection: Connection) {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(query)
            addColumnsFromResultSet(resultSet)
        }
    }

    fun addColumnsFromResultSet(resultSet: ResultSet) {
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount

        while (resultSet.next()) {
            for (i in 1..columnCount) {
                val header = metaData.getColumnName(i)
                val value = resultSet.getObject(i)

                if (value is String) {
                    addStringColumn(header, arrayOf(value))
                } else if (value is Number) {
                    addNumberColumn(header, arrayOf(value))
                } else {
                    addStringColumn(header, arrayOf(value.toString()))
                    // throw IllegalArgumentException("Unsupported SQL column type for column: $header")
                }
            }
        }
    }

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

    open fun build(): File {
        return File(filename, title, columns, includeRowNumbers, summary)
    }

}