package src.main.kotlin.file

import ColumnCalculationType
import ColumnContentCalculator
import src.main.kotlin.file.column.Column
import src.main.kotlin.file.column.FormatNumberColumn
import src.main.kotlin.file.column.FormatStringColumn
import src.main.kotlin.file.column.NumberColumn
import src.main.kotlin.file.column.StringColumn
import src.main.kotlin.file.format_options.CellFormatOptions
import src.main.kotlin.file.format_options.SummaryFormatOptions
import src.main.kotlin.file.format_options.TableFormatOptions
import src.main.kotlin.file.format_options.TitleFormatOptions
import java.sql.Connection
import java.sql.ResultSet

/**
 * A builder class for creating formatted file instances with various configuration options.
 * This class extends [FileBuilder] and adds additional functionality to handle cell formatting
 * for both string and numeric columns.
 *
 * @param filename The name of the file to be created.
 */
class FormatFileBuilder(private val filename: String) : FileBuilder(filename) {
    /** Formatting options for the title of the file. */
    var titleFormatOptions: TitleFormatOptions = TitleFormatOptions()

    /** Formatting options for the table borders. */
    var tableFormatOptions: TableFormatOptions = TableFormatOptions()

    /** Formatting options for the table's header cells. */
    var headerFormatOptions: CellFormatOptions = CellFormatOptions()

    /** Formatting options for the row number column (if included). */
    var rowNumberFormat: CellFormatOptions = CellFormatOptions()

    /** Formatting options for the summary section at the bottom of the report. */
    var summaryFormatOptions: SummaryFormatOptions = SummaryFormatOptions()

    /**
     * Adds a formatted string column to the file.
     *
     * @param header The header for the column.
     * @param strings The array of string values to be added as a column.
     * @param formatOptions The format options to be applied to the column.
     */
    fun addFormattedStringColumn(header: String, strings: Array<String>, formatOptions: CellFormatOptions) {
        columns.add(FormatStringColumn(header, strings, formatOptions) as Column<Any>)
    }

    /**
     * Adds a formatted number column to the file.
     *
     * @param header The header for the column.
     * @param numbers The array of numeric values to be added as a column.
     * @param formatOptions The format options to be applied to the column.
     */
    fun addFormattedNumberColumn(header: String, numbers: Array<Number>, formatOptions: CellFormatOptions) {
        columns.add(FormatNumberColumn(header, numbers as Array<Double>, formatOptions) as Column<Any>)
    }

    /**
     * Sets the columns for the file from a 2D array of content and applies a given format to each column.
     *
     * @param content 2D array of data, where each inner array represents a column's content.
     * @param formatOptions The format options to be applied to each column.
     */
    fun setColumns(content: Array<Array<Any>>, formatOptions: CellFormatOptions) {
        columns = mutableListOf()
        addColumns(content, formatOptions)
    }

    /**
     * Sets the columns for the file from headers and a 2D array of content, applying a given format to each column.
     *
     * @param headers The array of headers for the columns.
     * @param content 2D array of data, where each inner array represents a column's content.
     * @param formatOptions The format options to be applied to each column.
     * @throws IllegalArgumentException If headers and content arrays are of different lengths.
     */
    fun setColumns(headers: Array<String>, content: Array<Array<Any>>, formatOptions: CellFormatOptions) {
        columns = mutableListOf()
        addColumns(headers, content, formatOptions)
    }

    /**
     * Sets the columns for the file from a map of headers to column data, applying a given format to each column.
     *
     * @param data Map of column headers to arrays of data.
     * @param formatOptions The format options to be applied to each column.
     */
    fun setColumns(data: Map<String, Array<Any>>, formatOptions: CellFormatOptions) {
        columns = mutableListOf()
        addColumns(data, formatOptions)
    }

    /**
     * Adds columns to the file from a 2D array of content, applying a given format to each column.
     *
     * @param content 2D array of data, where each inner array represents a column.
     * @param formatOptions The format options to be applied to each column.
     */
    fun addColumns(content: Array<Array<Any>>, formatOptions: CellFormatOptions) {
        for (column in content) {
            addColumn(column, formatOptions)
        }
    }

    /**
     * Adds columns to the file from headers and a 2D array of content, applying a given format to each column.
     *
     * @param headers The array of headers for the columns.
     * @param content 2D array of data, where each inner array represents a column's content.
     * @param formatOptions The format options to be applied to each column.
     * @throws IllegalArgumentException If headers and content arrays are of different lengths.
     */
    fun addColumns(headers: Array<String>, content: Array<Array<Any>>, formatOptions: CellFormatOptions) {
        require(headers.size == content.size) { "Headers and content arrays must have the same length" }

        for (i in headers.indices) {
            val header = headers[i]
            val column = content[i]
            addColumn(header, column, formatOptions)
        }
    }

    /**
     * Adds columns to the file from a map of headers to column data, applying a given format to each column.
     *
     * @param data Map of column headers to arrays of data.
     * @param formatOptions The format options to be applied to each column.
     */
    fun addColumns(data: Map<String, Array<Any>>, formatOptions: CellFormatOptions) {
        for ((header, columnData) in data) {
            addColumn(header, columnData, formatOptions)
        }
    }

    /**
     * Adds a column with a specific header and content, applying a given format to the column.
     *
     * @param header The header for the column.
     * @param content The data for the column as an array.
     * @param formatOptions The format options to be applied to the column.
     * @throws IllegalArgumentException If the content is of unsupported type.
     */
    fun addColumn(header: String, content: Array<Any>, formatOptions: CellFormatOptions) {
        when (content.firstOrNull()) {
            is String -> columns.add(FormatStringColumn(header, content as Array<String>, formatOptions) as Column<Any>)
            is Number -> columns.add(
                FormatNumberColumn(
                    header,
                    content as Array<Double>,
                    formatOptions
                ) as Column<Any>
            )

            else -> throw IllegalArgumentException("Unsupported column type")
        }
    }

    /**
     * Adds a column with content and applies a given format to the column.
     *
     * @param content The data for the column as an array.
     * @param formatOptions The format options to be applied to the column.
     * @throws IllegalArgumentException If the content is of unsupported type.
     */
    fun addColumn(content: Array<Any>, formatOptions: CellFormatOptions) {
        when (content.firstOrNull()) {
            is String -> columns.add(
                FormatStringColumn(
                    content = content as Array<String>,
                    columnFormatOptions = formatOptions
                ) as Column<Any>
            )

            is Number -> columns.add(
                FormatNumberColumn(
                    content = content as Array<Double>,
                    columnFormatOptions = formatOptions
                ) as Column<Any>
            )

            else -> throw IllegalArgumentException("Unsupported column type")
        }
    }

    /**
     * Changes the formatting of an existing column at a specific index.
     *
     * @param index The index of the column to be modified.
     * @param formatOptions The new format options to be applied to the column.
     */
    fun changeColumnStyleWithIndex(index: Int, formatOptions: CellFormatOptions) {
        if (!columns.indices.contains(index))
            return
        if (columns[index] is NumberColumn) {
            columns[index] = FormatNumberColumn(
                columns[index].header,
                columns[index].content as Array<Double>,
                formatOptions
            ) as Column<Any>
        } else if (columns[index] is StringColumn) {
            columns[index] = FormatStringColumn(
                columns[index].header,
                columns[index].content as Array<String>,
                formatOptions
            ) as Column<Any>
        }
    }

    /**
     * Adds a calculated column based on the content of other columns and applies a format to it.
     *
     * @param columns Array of numeric columns to perform the calculation on.
     * @param calculationType The type of calculation to perform on the columns.
     * @param formatOptions The format options to be applied to the calculated column.
     */
    fun addCalculatedColumn(
        columns: Array<Array<Double>>,
        calculationType: ColumnCalculationType,
        formatOptions: CellFormatOptions
    ) {
        val result = ColumnContentCalculator.calculateColumnContent(columns, calculationType)
        addColumn(result as Array<Any>, formatOptions)
    }

    /**
     * Adds a calculated column with a specific header based on the content of other columns and applies a format to it.
     *
     * @param columns Array of numeric columns to perform the calculation on.
     * @param calculationType The type of calculation to perform on the columns.
     * @param header The header for the calculated column.
     * @param formatOptions The format options to be applied to the calculated column.
     */
    fun addCalculatedColumn(
        columns: Array<Array<Double>>,
        calculationType: ColumnCalculationType,
        header: String,
        formatOptions: CellFormatOptions
    ) {
        val result = ColumnContentCalculator.calculateColumnContent(columns, calculationType)
        addColumn(header, result as Array<Any>, formatOptions)
    }

    /**
     * Sets the columns for the file by querying a SQL database and applying a format to each column.
     *
     * @param query The SQL query to execute to retrieve the data.
     * @param connection The database connection to execute the query.
     * @param formatOptions The format options to be applied to each column.
     */
    fun setColumnsFromSQL(query: String, connection: Connection, formatOptions: CellFormatOptions) {
        columns = mutableListOf()
        addColumnsFromSQL(query, connection, formatOptions)
    }

    /**
     * Adds columns to the file from the results of a SQL query, applying a format to each column.
     *
     * @param query The SQL query to execute to retrieve the data.
     * @param connection The database connection to execute the query.
     * @param formatOptions The format options to be applied to each column.
     */
    fun addColumnsFromSQL(query: String, connection: Connection, formatOptions: CellFormatOptions) {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(query)
            addColumnsFromResultSet(resultSet, formatOptions)
        }
    }

    /**
     * Adds columns to the file from the result set of a SQL query, applying a format to each column.
     *
     * @param resultSet The result set from the SQL query.
     * @param formatOptions The format options to be applied to each column.
     */
    fun addColumnsFromResultSet(resultSet: ResultSet, formatOptions: CellFormatOptions) {
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
                addFormattedStringColumn(
                    header,
                    values.map { it.toString() }.toTypedArray() as Array<String>,
                    formatOptions
                )
            } else if (values.all { it is Number }) {
                addFormattedNumberColumn(
                    header,
                    values.map { (it as Number).toDouble() }.toTypedArray() as Array<Number>,
                    formatOptions
                )
            } else {
                addFormattedStringColumn(
                    header,
                    values.map { it.toString() }.toTypedArray() as Array<String>,
                    formatOptions
                )
            }
        }
    }

    /**
     * Builds and returns a [FormatFile] instance with the configured properties.
     *
     * @return A new [FormatFile] instance.
     */
    override fun build(): File {
        return FormatFile(
            filename,
            title,
            titleFormatOptions,
            tableFormatOptions,
            headerFormatOptions,
            columns,
            includeRowNumbers,
            rowNumberFormat,
            summary,
            summaryFormatOptions
        )
    }
}
