package src.main.kotlin.file

import src.main.kotlin.file.column.Calculation
import src.main.kotlin.file.column.Column
import src.main.kotlin.file.column.FormatCalculatedColumn
import src.main.kotlin.file.column.FormatNumberColumn
import src.main.kotlin.file.column.FormatStringColumn
import src.main.kotlin.file.column.NumberColumn
import src.main.kotlin.file.format_options.CellFormatOptions
import src.main.kotlin.file.format_options.TableFormatOptions
import src.main.kotlin.file.format_options.TitleFormatOptions
import java.sql.Connection
import java.sql.ResultSet

class FormatFileBuilder(private val filename: String) : FileBuilder(filename) {
    var titleFormatOptions: TitleFormatOptions = TitleFormatOptions()
    var tableFormatOptions: TableFormatOptions = TableFormatOptions()
    var headerFormatOptions: CellFormatOptions = CellFormatOptions()
    var rowNumberFormat: CellFormatOptions = CellFormatOptions()
    var summaryFormatOptions: CellFormatOptions = CellFormatOptions()


    fun addFormattedStringColumn(header: String, strings: Array<String>, formatOptions: CellFormatOptions) {
        columns.add(FormatStringColumn(header, strings, formatOptions) as Column<Any>)
    }

    fun addFormattedNumberColumn(header: String, numbers: Array<Number>, formatOptions: CellFormatOptions) {
        columns.add(FormatNumberColumn(header, numbers as Array<Double>, formatOptions) as Column<Any>)
    }

    fun addFormattedCalculatedColumn(
        header: String,
        columns: Array<Array<Number>>,
        calculation: Calculation,
        formatOptions: CellFormatOptions
    ) {
        val arr: MutableList<NumberColumn> = mutableListOf()

        for (column in columns) {
            val doubleColumn = column.map { it.toDouble() }.toTypedArray()
            arr.add(NumberColumn(header, doubleColumn))
        }

        val calculatedColumn = FormatCalculatedColumn(header, arr.toTypedArray(), calculation, formatOptions)
        this.columns.add(calculatedColumn as Column<Any>)
    }

    fun addFormattedCalculatedColumn(
        columns: Array<Array<Number>>,
        calculation: Calculation,
        formatOptions: CellFormatOptions
    ) {
        val arr: MutableList<NumberColumn> = mutableListOf()

        for (column in columns) {
            val doubleColumn = column.map { it.toDouble() }.toTypedArray()
            arr.add(NumberColumn(content = doubleColumn))
        }

        val calculatedColumn = FormatCalculatedColumn(
            header = "",
            columnsForCalculations = arr.toTypedArray(),
            calculation = calculation,
            cellFormatOptions = formatOptions
        )
        this.columns.add(calculatedColumn as Column<Any>)
    }

    fun setColumns(content: Array<Array<Any>>, formatOptions: CellFormatOptions) {
        columns = mutableListOf()
        addColumns(content, formatOptions)
    }

    fun setColumns(headers: Array<String>, content: Array<Array<Any>>, formatOptions: CellFormatOptions) {
        columns = mutableListOf()
        addColumns(headers, content, formatOptions)
    }

    fun setColumns(data: Map<String, Array<Any>>, formatOptions: CellFormatOptions) {
        columns = mutableListOf()
        addColumns(data, formatOptions)
    }

    fun addColumns(content: Array<Array<Any>>, formatOptions: CellFormatOptions) {
        for (column in content) {
            addColumn(column, formatOptions)
        }
    }

    fun addColumns(headers: Array<String>, content: Array<Array<Any>>, formatOptions: CellFormatOptions) {
        require(headers.size == content.size) { "Headers and content arrays must have the same length" }

        for (i in headers.indices) {
            val header = headers[i]
            val column = content[i]
            addColumn(header, column, formatOptions)
        }
    }

    fun addColumns(data: Map<String, Array<Any>>, formatOptions: CellFormatOptions) {
        for ((header, columnData) in data) {
            addColumn(header, columnData, formatOptions)
        }
    }

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

    fun setColumnsFromSQL(query: String, connection: Connection, formatOptions: CellFormatOptions) {
        columns = mutableListOf()
        addColumnsFromSQL(query, connection, formatOptions)
    }

    fun addColumnsFromSQL(query: String, connection: Connection, formatOptions: CellFormatOptions) {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(query)
            addColumnsFromResultSet(resultSet, formatOptions)
        }
    }

    fun addColumnsFromResultSet(resultSet: ResultSet, formatOptions: CellFormatOptions) {
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount

        while (resultSet.next()) {
            for (i in 1..columnCount) {
                val header = metaData.getColumnName(i)
                val value = resultSet.getObject(i)

                if (value is String) {
                    addFormattedStringColumn(header, arrayOf(value), formatOptions)
                } else if (value is Number) {
                    addFormattedNumberColumn(header, arrayOf(value), formatOptions)
                } else {
                    addFormattedStringColumn(header, arrayOf(value.toString()), formatOptions)
                    // Handle unsupported column types by converting to String
                }
            }
        }
    }

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
