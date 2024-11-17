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

/**
 * A builder class for creating instances of [File] with various configurations.
 *
 * @param filename The name of the file to be created.
 */
open class FileBuilder(
    private var filename: String
) {
    /** The title of the file. */
    var title: String = ""

    /** The list of columns in the file. */
    var columns: MutableList<Column<Any>> = mutableListOf()

    /** Flag indicating whether to include row numbers as the first column. */
    var includeRowNumbers: Boolean = false

    /** Summary entries as a map of key-value pairs for the bottom of the report. */
    var summary: MutableMap<String, Any> = mutableMapOf()

    /**
     * Validates the file name to meet specific formatting rules. Ensures the filename:
     * - Contains only alphanumeric characters, underscores, and dashes.
     * - Does not end with spaces or periods.
     *
     * This done as to comply with windows' filename requirements
     *
     * @param filename The filename to validate.
     * @return `true` if the filename is valid, `false` otherwise.
     */
    private fun validateFilename(filename: String): Boolean {
        val windowsFilenameRegex = Regex("^[a-zA-Z0-9_-]+$")
        return windowsFilenameRegex.matches(filename) && !filename.endsWith(" ") && !filename.endsWith(".")
    }

    /**
     * Sets the filename if it passes validation.
     *
     * @param filename The new filename.
     * @throws IllegalArgumentException If the filename is invalid.
     */
    fun setFileName(filename: String) {
        require(validateFilename(filename)) { "Invalid filename format" }
        this.filename = filename
    }

    /**
     * Sets the title of the file.
     *
     * @param title The new title for the file.
     */
    fun updateTitle(title: String) {
        this.title = title
    }

    /**
     * Specifies whether to include row numbers as the first column.
     *
     * @param includeRowNumbers `true` to include row numbers, `false` otherwise.
     */
    fun includeRowNumbers(includeRowNumbers: Boolean) {
        this.includeRowNumbers = includeRowNumbers
    }

    /**
     * Sets the columns of the file based on a 2D array of content.
     *
     * @param content 2D array of data, where each inner array represents a column's data.
     */
    fun setColumns(content: Array<Array<Any>>) {
        columns = mutableListOf()
        addColumns(content)
    }

    /**
     * Sets columns with specific headers and content.
     *
     * @param headers Array of headers for the columns.
     * @param content 2D array of data, where each inner array represents a column's data.
     * @throws IllegalArgumentException If headers and content arrays are of different lengths.
     */
    fun setColumns(headers: Array<String>, content: Array<Array<Any>>) {
        columns = mutableListOf()
        addColumns(headers, content)
    }

    /**
     * Sets columns based on a map of headers to column content.
     *
     * @param data Map of column headers to arrays of data.
     */
    fun setColumns(data: Map<String, Array<Any>>) {
        columns = mutableListOf()
        addColumns(data)
    }

    /**
     * Adds columns based on a 2D array of content. Each inner array represents a column.
     *
     * @param content 2D array of data, where each inner array represents a column's content.
     */
    fun addColumns(content: Array<Array<Any>>) {
        for (column in content) {
            addColumn(column)
        }
    }

    /**
     * Adds columns with specific headers and content.
     *
     * @param headers Array of headers for the columns.
     * @param content 2D array of data, where each inner array represents a column's content.
     * @throws IllegalArgumentException If headers and content arrays are of different lengths.
     */
    fun addColumns(headers: Array<String>, content: Array<Array<Any>>) {
        require(headers.size == content.size) { "Headers and content arrays must have the same length" }

        for (i in headers.indices) {
            val header = headers[i]
            val column = content[i]
            addColumn(header, column)
        }
    }

    /**
     * Adds columns from a map of headers to column data.
     *
     * @param data Map of column headers to arrays of data.
     */
    fun addColumns(data: Map<String, Array<Any>>) {
        for ((header, columnData) in data) {
            addColumn(header, columnData)
        }
    }

    /**
     * Adds a column containing string data.
     *
     * @param strings Array of strings to be added as a column.
     */
    fun addStringColumn(strings: Array<String>) {
        columns.add(StringColumn(content = strings) as Column<Any>)
    }

    /**
     * Adds a column containing string data with a specific header.
     *
     * @param header The header for the column.
     * @param strings Array of strings to be added as a column.
     */
    fun addStringColumn(header: String, strings: Array<String>) {
        columns.add(StringColumn(header, strings) as Column<Any>)
    }

    /**
     * Adds a column containing numeric data.
     *
     * @param numbers Array of numbers to be added as a column.
     */
    fun addNumberColumn(numbers: Array<Number>) {
        columns.add(NumberColumn(content = numbers as Array<Double>) as Column<Any>)
    }

    /**
     * Adds a column containing numeric data with a specific header.
     *
     * @param header The header for the column.
     * @param numbers Array of numbers to be added as a column.
     */
    fun addNumberColumn(header: String, numbers: Array<Number>) {
        val doubleArray = numbers.map { it.toDouble() }.toTypedArray()
        columns.add(NumberColumn(header, doubleArray) as Column<Any>)
    }

    /**
     * Adds a column with a specific header and content.
     *
     * @param header The header for the column.
     * @param content The data for the column as an array.
     * @throws IllegalArgumentException If the content is of unsupported type.
     */
    fun addColumn(header: String, content: Array<Any>) {
        when (content.firstOrNull()) {
            is String -> columns.add(StringColumn(header, content as Array<String>) as Column<Any>)
            is Number -> columns.add(NumberColumn(header, content as Array<Double>) as Column<Any>)
            else -> throw IllegalArgumentException("Unsupported column type")
        }
    }

    /**
     * Adds a column with content and no header.
     *
     * @param content The data for the column as an array.
     * @throws IllegalArgumentException If the content is of unsupported type.
     */
    fun addColumn(content: Array<Any>) {
        when (content.firstOrNull()) {
            is String -> columns.add(StringColumn(content = content as Array<String>) as Column<Any>)
            is Number -> columns.add(NumberColumn(content = content as Array<Double>) as Column<Any>)
            else -> throw IllegalArgumentException("Unsupported column type")
        }
    }

    /**
     * Removes a column at a specific index.
     *
     * @param index The index of the column to be removed.
     */
    fun removeColumnWithIndex(index: Int) {
        if (columns.indices.contains(index))
            columns.removeAt(index)
    }

    /**
     * Adds a calculated column based on an array of numeric columns and a specified calculation type.
     *
     * @param columns Array of numeric columns to perform the calculation on.
     * @param calculationType The type of calculation to perform on the columns.
     */
    fun addCalculatedColumn(columns: Array<Array<Double>>, calculationType: ColumnCalculationType) {
        val result = ColumnContentCalculator.calculateColumnContent(columns, calculationType)
        addNumberColumn(result as Array<Number>)
    }

    /**
     * Adds a calculated column with a specific header based on an array of numeric columns and a specified calculation type.
     *
     * @param columns Array of numeric columns to perform the calculation on.
     * @param calculationType The type of calculation to perform on the columns.
     * @param header The header for the calculated column.
     */
    fun addCalculatedColumn(columns: Array<Array<Double>>, calculationType: ColumnCalculationType, header: String) {
        val result = ColumnContentCalculator.calculateColumnContent(columns, calculationType)
        addNumberColumn(header, result as Array<Number>)
    }

    /**
     * Sets columns from a SQL query result by using a database connection.
     *
     * @param query The SQL query to execute.
     * @param connection The database connection.
     */
    fun setColumnsFromSQL(query: String, connection: Connection) {
        columns = mutableListOf()
        addColumnsFromSQL(query, connection)
    }

    /**
     * Adds columns based on the result of a SQL query execution.
     *
     * @param query The SQL query to execute.
     * @param connection The database connection.
     */
    fun addColumnsFromSQL(query: String, connection: Connection) {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(query)
            addColumnsFromResultSet(resultSet)
        }
    }

    /**
     * Adds columns based on the result set from a SQL query execution.
     *
     * @param resultSet The result set of the SQL query.
     */
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

    /**
     * Adds a summary entry with a specific key and value.
     *
     * @param key The key for the summary entry.
     * @param value The value for the summary entry.
     */
    fun addSummaryEntry(key: String, value: Any) {
        this.summary[key] = value
    }

    /**
     * Adds multiple summary entries from a map of key-value pairs.
     *
     * @param entries Map of key-value pairs to be added to the summary.
     */
    fun addSummaryEntries(entries: Map<String, Any>) {
        this.summary.putAll(entries)
    }

    /**
     * Sets the summary entries to the given map of key-value pairs.
     *
     * @param entries Map of key-value pairs to replace the existing summary entries.
     */
    fun setSummaryEntries(entries: Map<String, Any>) {
        this.summary = entries.toMutableMap()
    }

    /**
     * Adds a summary entry calculated from an array of numeric values, applying a specified calculation type and optional condition.
     *
     * @param key The key for the summary entry.
     * @param values Array of numeric values to be used for the calculation.
     * @param summaryType The type of summary calculation to perform.
     * @param condition A condition function to filter the values before calculating the summary (defaults to no filtering).
     */
    fun addSummaryEntry(
        key: String,
        values: Array<Double>,
        summaryType: SummaryCalculationType,
        condition: (Double) -> Boolean = { true }
    ) {
        val summaryValue = SummaryEntryCalculator.calculateSummaryEntry(values, summaryType, condition)
        this.summary[key] = summaryValue
    }

    /**
     * Adds a summary entry calculated from an array of string values, applying the COUNT summary calculation type and optional condition.
     *
     * @param key The key for the summary entry.
     * @param values Array of string values to be used for the calculation.
     * @param condition A condition function to filter the values before calculating the summary (defaults to no filtering).
     */
    fun addSummaryEntry(
        key: String,
        values: Array<String>,
        condition: (String) -> Boolean = { true }
    ) {
        val summaryValue = SummaryEntryCalculator.calculateSummaryEntry(values, SummaryCalculationType.COUNT, condition)
        this.summary[key] = summaryValue
    }

    /**
     * Returns a string representation of the current state of the FileBuilder instance.
     *
     * The string includes:
     * - The filename, title, and whether row numbers are included.
     * - A list of columns with their headers and content.
     * - A summary section with key-value pairs.
     */
    override fun toString(): String {
        val text = StringBuilder(
            "Current FileBuilder state(filename='$filename'\n title='$title'\n includeRowNumbers=$includeRowNumbers\n\n"
        )

        text.append("Colum Index | Column header | Column data\n")
        for (i in columns.indices) {
            val column = columns[i]
            text.append(writeColumn(column, i))
        }
        text.append("\n")

        text.append("Key: Value\n")
        for (kv in summary) {
            text.append("${kv.key}: ${kv.value}\n")
        }

        return text.toString()
    }

    /**
     * Converts a column's header and content into a formatted string.
     *
     * @param column The column to be formatted into a string.
     * @return A string representing the column header followed by its content.
     */
    fun writeColumn(column: Column<Any>, index: Int): String {
        val header = column.header
        val content = column.content.joinToString(", ") { it.toString() }

        return "$index | $header | $content\n"
    }

    /**
     * Builds and returns the final [File] instance with the configured properties.
     *
     * @return A new [File] instance.
     */
   internal open fun build(): File {
        return File(filename, title, columns, includeRowNumbers, summary)
    }

}