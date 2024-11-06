import src.main.kotlin.FormatReportExported
import src.main.kotlin.ReportExporter
import src.main.kotlin.file.FileBuilder
import src.main.kotlin.file.column.NumberColumn
import src.main.kotlin.file.column.StringColumn
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.ServiceLoader

fun main(args: Array<String>) {
    // if (args.isEmpty()) {
    //    throw IllegalArgumentException("Connection string required")
    //}

    val connectionString = "jdbc:mysql://127.0.0.1:3306/report?user=root&password="
    lateinit var connection: Connection
    try {
        connection = DriverManager.getConnection(connectionString)
        println("Connected to the database successfully!")
    } catch (e: SQLException) {
        e.printStackTrace()
        println("Failed to connect to the database.")
    }

    val serviceLoader = ServiceLoader.load(ReportExporter::class.java)
    while (true) {
        println("Registered services\n")
        val exporters = serviceLoader.map { exporter -> exporter }

        for (i in exporters.indices) {
            val exporter = exporters[i]
            println("${i + 1}. ${exporter.exporterType} exporter")
        }
        print("\nEnter the number of the exporter that you would like to use: ")
        val input = readln()

        if (input.toIntOrNull() == null) {
            println("Please enter a number")
            continue
        }

        if (!exporters.indices.contains(input.toInt() - 1)) {
            println("Please enter a valid number")
        }

        val selectedExporter = exporters[input.toInt() - 1]

        var useFormated = false

        if (selectedExporter is FormatReportExported) {
            println("Would you like to make a report with formating? (Y/N) (Default = Y")
            val choice = readln()
            useFormated = choice.uppercase() != "N"
        }

        if (useFormated) {
            createFormatReport(connection, selectedExporter)
        } else {

            CreatNormalReport(connection, selectedExporter)
        }

        println("Would you like to generate more reports? (Y/N) (Default = N): ")
        val opt = readln()
        if (opt.uppercase() != "Y")
            break
    }
}

fun createFormatReport(connection: Connection, exporter: ReportExporter) {
    println("Enter your SQL query:")
    val query = readln()

    println("Enter a filename. dont enter file extension")
    val fileName = readln()
    val fileBuilder = FileBuilder(fileName)

    println("Enter a title for the report (leave blank for no title):")
    val title = readln().ifBlank { "Untitled Report" }
    fileBuilder.updateTitle(title)
    //TODO format title

    println("Include row numbers? (Y/N, default is N):")
    val includeRowNumbers = readln().uppercase() == "Y"
    fileBuilder.includeRowNumbers = includeRowNumbers
    //TODO format row num

    //TODO format table
    //TODO Format headers

    try {
        fileBuilder.setColumnsFromSQL(query, connection)

        while (true) {
            println("\nCurrent columns in the report:")
            fileBuilder.columns.forEachIndexed { index, column ->
                println("${index}. ${column.header}")
            }

            println("\nWould you like to add a calculated column? (Y/N, default is N):")
            val addCalculatedColumn = readln().uppercase()
            if (addCalculatedColumn != "Y") break

            println("Choose an operation for the calculated column: ADD, SUB, PROD, DIV:")
            val calcTypeInput = readln().uppercase()
            val calcType = try {
                ColumnCalculationType.valueOf(calcTypeInput)
            } catch (e: IllegalArgumentException) {
                println("Invalid calculation type. Try again.")
                continue
            }

            println("Enter the indices of the columns to use for this calculation, separated by spaces:")
            val columnIndices = readln().split(" ").mapNotNull { it.toIntOrNull() }

            if (columnIndices.any { it !in fileBuilder.columns.indices }) {
                println("One or more invalid indices entered. Try again.")
                continue
            }

            val selectedData = columnIndices.map { fileBuilder.columns[it].content as Array<Double> }.toTypedArray()

            val calculatedContent = ColumnContentCalculator.calculateColumnContent(selectedData, calcType)
            println("Enter a header for the calculated column:")
            val calculatedHeader = readln().ifBlank { "Calculated Column" }
            fileBuilder.addNumberColumn(calculatedHeader, calculatedContent as Array<Number>)
            println("Calculated column '$calculatedHeader' added successfully.")
        }

        while (true) {
            println("\nCurrent columns in the report:")
            fileBuilder.columns.forEachIndexed { index, column ->
                println("${index}. ${column.header}")
            }
            //TODO format column with index
            break
        }

        while (true) {
            println("\nCurrent columns in the report:")
            fileBuilder.columns.forEachIndexed { index, column ->
                println("${index}. ${column.header}")
            }

            println("\nWould you like to add a summary entry? (Y/N, default is N):")
            val addSummary = readln().uppercase()
            if (addSummary != "Y") break

            println("Choose a summary type: SUM, AVERAGE, COUNT:")
            val summaryTypeInput = readln().uppercase()
            val summaryType = try {
                SummaryCalculationType.valueOf(summaryTypeInput)
            } catch (e: IllegalArgumentException) {
                println("Invalid summary type. Try again.")
                continue
            }

            println("Enter the index of the column to calculate the summary for:")
            val columnIndex = readln().toIntOrNull()

            if (columnIndex == null || columnIndex !in fileBuilder.columns.indices) {
                println("Invalid column index. Try again.")
                continue
            }

            val column = fileBuilder.columns[columnIndex]
            var summaryResult = 0.0

            if (column is NumberColumn) {
                summaryResult =
                    SummaryEntryCalculator.calculateSummaryEntry(column.content as Array<Double>, summaryType) as Double
            } else if (column is StringColumn) {
                SummaryEntryCalculator.calculateSummaryEntry(column.content as Array<String>, summaryType) as Double
            } else {
                println("Invalid column type. Try again.")
                continue
            }

            println("Enter a key name for this summary entry:")
            val summaryKey = readln().ifBlank { "Summary $summaryType for ${fileBuilder.columns[columnIndex].header}" }
            fileBuilder.addSummaryEntry(summaryKey, summaryResult)
            println("Summary entry '$summaryKey' added successfully with result: $summaryResult")
        }

        //TODO Format summary

        val file = fileBuilder.build()

        exporter.export(file)
    } catch (e: SQLException) {
        e.printStackTrace()
        println("Error executing query or exporting file.")
    } catch (e: Exception) {
        e.printStackTrace()
        println("An error occurred while exporting the report.")
    }
}

fun CreatNormalReport(connection: Connection, exporter: ReportExporter) {
    println("Enter your SQL query:")
    val query = readln()

    println("Enter a filename. dont enter file extension")
    val fileName = readln()
    val fileBuilder = FileBuilder(fileName)

    println("Enter a title for the report (leave blank for no title):")
    val title = readln().ifBlank { "Untitled Report" }
    fileBuilder.updateTitle(title)

    println("Include row numbers? (Y/N, default is N):")
    val includeRowNumbers = readln().uppercase() == "Y"
    fileBuilder.includeRowNumbers = includeRowNumbers

    try {
        fileBuilder.setColumnsFromSQL(query, connection)

        while (true) {
            println("\nCurrent columns in the report:")
            fileBuilder.columns.forEachIndexed { index, column ->
                println("${index}. ${column.header}")
            }

            println("\nWould you like to add a calculated column? (Y/N, default is N):")
            val addCalculatedColumn = readln().uppercase()
            if (addCalculatedColumn != "Y") break

            println("Choose an operation for the calculated column: ADD, SUB, PROD, DIV:")
            val calcTypeInput = readln().uppercase()
            val calcType = try {
                ColumnCalculationType.valueOf(calcTypeInput)
            } catch (e: IllegalArgumentException) {
                println("Invalid calculation type. Try again.")
                continue
            }

            println("Enter the indices of the columns to use for this calculation, separated by spaces:")
            val columnIndices = readln().split(" ").mapNotNull { it.toIntOrNull() }

            if (columnIndices.any { it !in fileBuilder.columns.indices }) {
                println("One or more invalid indices entered. Try again.")
                continue
            }

            val selectedData = columnIndices.map { fileBuilder.columns[it].content as Array<Double> }.toTypedArray()

            val calculatedContent = ColumnContentCalculator.calculateColumnContent(selectedData, calcType)
            println("Enter a header for the calculated column:")
            val calculatedHeader = readln().ifBlank { "Calculated Column" }
            fileBuilder.addNumberColumn(calculatedHeader, calculatedContent as Array<Number>)
            println("Calculated column '$calculatedHeader' added successfully.")
        }

        while (true) {
            println("\nCurrent columns in the report:")
            fileBuilder.columns.forEachIndexed { index, column ->
                println("${index}. ${column.header}")
            }

            println("\nWould you like to add a summary entry? (Y/N, default is N):")
            val addSummary = readln().uppercase()
            if (addSummary != "Y") break

            println("Choose a summary type: SUM, AVERAGE, COUNT:")
            val summaryTypeInput = readln().uppercase()
            val summaryType = try {
                SummaryCalculationType.valueOf(summaryTypeInput)
            } catch (e: IllegalArgumentException) {
                println("Invalid summary type. Try again.")
                continue
            }

            println("Enter the index of the column to calculate the summary for:")
            val columnIndex = readln().toIntOrNull()

            if (columnIndex == null || columnIndex !in fileBuilder.columns.indices) {
                println("Invalid column index. Try again.")
                continue
            }

            val column = fileBuilder.columns[columnIndex]
            var summaryResult = 0.0

            if (column is NumberColumn) {
                summaryResult =
                    SummaryEntryCalculator.calculateSummaryEntry(column.content as Array<Double>, summaryType) as Double
            } else if (column is StringColumn) {
                SummaryEntryCalculator.calculateSummaryEntry(column.content as Array<String>, summaryType) as Double
            } else {
                println("Invalid column type. Try again.")
                continue
            }

            println("Enter a key name for this summary entry:")
            val summaryKey = readln().ifBlank { "Summary $summaryType for ${fileBuilder.columns[columnIndex].header}" }
            fileBuilder.addSummaryEntry(summaryKey, summaryResult)
            println("Summary entry '$summaryKey' added successfully with result: $summaryResult")
        }

        val file = fileBuilder.build()

        exporter.export(file)
    } catch (e: SQLException) {
        e.printStackTrace()
        println("Error executing query or exporting file.")
    } catch (e: Exception) {
        e.printStackTrace()
        println("An error occurred while exporting the report.")
    }
}

