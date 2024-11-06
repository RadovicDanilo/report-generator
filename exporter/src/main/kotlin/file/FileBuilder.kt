package src.main.kotlin.file

import ColumnCalculationType
import ColumnContentCalculator
import SummaryCalculationType
import SummaryEntryCalculator
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

    fun updateTitle(title: String) {
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
        val doubleArray = numbers.map { it.toDouble() }.toTypedArray()
        columns.add(NumberColumn(header, doubleArray) as Column<Any>)
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

    fun removeColumnWithIndex(index: Int) {
        if (columns.indices.contains(index))
            columns.removeAt(index)
    }

    fun addCalculatedColumn(columns: Array<Array<Double>>, calculationType: ColumnCalculationType) {
        val result = ColumnContentCalculator.calculateColumnContent(columns, calculationType)
        addNumberColumn(result as Array<Number>)
    }

    fun addCalculatedColumn(columns: Array<Array<Double>>, calculationType: ColumnCalculationType, header: String) {
        val result = ColumnContentCalculator.calculateColumnContent(columns, calculationType)
        addNumberColumn(header, result as Array<Number>)
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

        val columnsData = mutableMapOf<String, MutableList<Any>>()

        for (i in 1..columnCount) {
            val header = metaData.getColumnName(i)
            columnsData[header] = mutableListOf()
        }

        while (resultSet.next()) {
            for (i in 1..columnCount) {
                val header = metaData.getColumnName(i)
                val value = resultSet.getObject(i)

                columnsData[header]?.add(value ?: "")
            }
        }

        for ((header, values) in columnsData) {
            if (values.all { it is String }) {
                addStringColumn(header, values.map { it.toString() }.toTypedArray() as Array<String>)
            } else if (values.all { it is Number }) {
                addNumberColumn(header, values.map { (it as Number).toDouble() }.toTypedArray() as Array<Number>)
            } else {
                addStringColumn(header, values.map { it.toString() }.toTypedArray() as Array<String>)
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

    fun addSummaryEntry(
        key: String,
        values: Array<Double>,
        summaryType: SummaryCalculationType,
        condition: (Double) -> Boolean = { true }
    ) {
        val summaryValue = SummaryEntryCalculator.calculateSummaryEntry(values, summaryType, condition)
        this.summary[key] = summaryValue
    }

    open fun build(): File {
        return File(filename, title, columns, includeRowNumbers, summary)
    }

}