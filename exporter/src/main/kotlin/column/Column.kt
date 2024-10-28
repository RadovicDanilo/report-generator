package src.main.kotlin.column

import src.main.kotlin.format_options.FormatOptions

abstract class Column<T : Any>(val header: String = "", val content: Array<T>)

open class NumberColumn(header: String = "", content: Array<Double>) : Column<Double>(header, content)

enum class Calculation {
    ADD, SUB, PROD, DIV
}

open class CalculatedColumn(
    header: String = "",
    columnsForCalculations: Array<Column<Double>>,
    calculation: Calculation
) : NumberColumn(header, createContent(columnsForCalculations, calculation)) {

    companion object {
        fun createContent(
            columns: Array<Column<Double>>,
            calculation: Calculation
        ): Array<Double> {
            return when (calculation) {
                Calculation.ADD -> sum(columns)
                Calculation.SUB -> sub(columns)
                Calculation.PROD -> prod(columns)
                Calculation.DIV -> div(columns)
            }
        }

        fun sum(columns: Array<Column<Double>>): Array<Double> {
            require(columns.size >= 2) { "Add operation requires at least two columns." }

            val result = Array(columns[0].content.size) { 0.0 }

            for (i in columns[0].content.indices) {
                var sum = 0.0
                for (column in columns) {
                    sum += column.content[i]
                }
                result[i] = sum
            }

            return result
        }

        fun sub(columns: Array<Column<Double>>): Array<Double> {
            require(columns.size == 2) { "Subtract operation requires exactly two columns." }

            val result = Array(columns[0].content.size) { 0.0 }

            for (i in columns[0].content.indices) {
                result[i] = columns[0].content[i] - columns[1].content[i]
            }

            return result
        }

        fun prod(columns: Array<Column<Double>>): Array<Double> {
            require(columns.size >= 2) { "Product operation requires at least two columns." }

            val result = Array(columns[0].content.size) { 1.0 }

            for (i in columns[0].content.indices) {
                var product = 1.0
                for (column in columns) {
                    product *= column.content[i]
                }
                result[i] = product
            }

            return result
        }

        fun div(columns: Array<Column<Double>>): Array<Double> {
            require(columns.size == 2) { "Division operation requires exactly two columns." }

            val result = Array(columns[0].content.size) { 0.0 }

            for (i in columns[0].content.indices) {
                require(columns[1].content[i] != 0.0) { "Division by zero" }

                result[i] = columns[0].content[i] / columns[1].content[i]
            }

            return result
        }
    }
}

open class StringColumn(header: String = "", content: Array<String>) : Column<String>(header, content)

class FormatNumberColumn(
    header: String = "",
    content: Array<Double>,
    val columnFormatOptions: FormatOptions
) : NumberColumn(header, content)

class FormatStringColumn(
    header: String = "",
    content: Array<String>,
    val columnFormatOptions: FormatOptions
) : StringColumn(header, content)
