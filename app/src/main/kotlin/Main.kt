import src.main.kotlin.FormatReportExported
import src.main.kotlin.ReportExporter
import src.main.kotlin.file.FileBuilder
import src.main.kotlin.file.FormatFile
import src.main.kotlin.file.FormatFileBuilder
import src.main.kotlin.file.column.NumberColumn
import src.main.kotlin.file.column.StringColumn
import src.main.kotlin.file.format_options.Alignment
import src.main.kotlin.file.format_options.CellFormatOptions
import src.main.kotlin.file.format_options.FontStyle
import java.awt.Color
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
    val fileBuilder = FormatFileBuilder(fileName)

    println("Enter a title for the report (leave blank for no title):")
    val title = readln()
    fileBuilder.updateTitle(title)

    if (title.isNotEmpty()) {
        println("Select font size for the title (default is 24):")
        val titleFontSize = readln().toIntOrNull() ?: 24
        fileBuilder.titleFormatOptions = fileBuilder.titleFormatOptions.copy(fontSize = titleFontSize)

        println("Choose title text color (1: BLACK, 2: RED, 3: GREEN, 4: BLUE):")
        val titleColorChoice = readln().toIntOrNull() ?: 1
        fileBuilder.titleFormatOptions = fileBuilder.titleFormatOptions.copy(color = getColorByChoice(titleColorChoice))
    }

    println("Include row numbers? (Y/N, default is N):")
    val includeRowNumbers = readln().uppercase() == "Y"
    fileBuilder.includeRowNumbers = includeRowNumbers

    if (includeRowNumbers) {
        println("Select font size for row numbers (default is 12):")
        val rowNumFontSize = readln().toIntOrNull() ?: 12
        fileBuilder.rowNumberFormat.fontSize = rowNumFontSize

        println("Choose row number background color (1: BLACK, 2: RED, 3: GREEN, 4: BLUE):")
        val rowBackground = readln().toIntOrNull() ?: 1
        fileBuilder.rowNumberFormat.backgroundColor = getColorByChoice(rowBackground)

        println("Choose row number text color (1: BLACK, 2: RED, 3: GREEN, 4: BLUE):")
        val rowNumColorChoice = readln().toIntOrNull() ?: 1
        fileBuilder.rowNumberFormat.textColor = getColorByChoice(rowNumColorChoice)
    }

    println("Select font size for the header (default is 12):")
    val headerFontSize = readln().toIntOrNull() ?: 12
    fileBuilder.headerFormatOptions.fontSize = headerFontSize

    println("Choose header text color (1: BLACK, 2: RED, 3: GREEN, 4: BLUE):")
    val headerTextColorChoice = readln().toIntOrNull() ?: 1
    fileBuilder.headerFormatOptions.textColor = getColorByChoice(headerTextColorChoice)

    println("Choose header background color (1: WHITE, 2: LIGHT_GRAY, 3: YELLOW, 4: BLUE):")
    val headerBackgroundColorChoice = readln().toIntOrNull() ?: 1
    fileBuilder.headerFormatOptions.backgroundColor = getColorByChoice(headerBackgroundColorChoice)

    println("Choose header text alignment (1: LEFT, 2: CENTER, 3: RIGHT):")
    val headerAlignmentChoice = readln().toIntOrNull() ?: 1
    fileBuilder.headerFormatOptions.alignment = when (headerAlignmentChoice) {
        1 -> Alignment.LEFT
        2 -> Alignment.CENTER
        3 -> Alignment.RIGHT
        else -> Alignment.LEFT
    }

    println("Choose header font style (1: NORMAL, 2: BOLD, 3: ITALIC, 4: BOLD_ITALIC):")
    val headerFontStyleChoice = readln().toIntOrNull() ?: 1
    fileBuilder.headerFormatOptions.fontStyle = when (headerFontStyleChoice) {
        1 -> FontStyle.NORMAL
        2 -> FontStyle.BOLD
        3 -> FontStyle.ITALIC
        4 -> FontStyle.BOLD_ITALIC
        else -> FontStyle.NORMAL
    }

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

            println("\nWould you like to edit the style of a column? (Y/N, default is N):")
            val editStyleChoice = readln().uppercase()
            if (editStyleChoice != "Y") break

            println("Enter the index of the column to edit style:")
            val columnIndex = readln().toIntOrNull()

            if (columnIndex == null || columnIndex !in fileBuilder.columns.indices) {
                println("Invalid column index. Try again.")
                continue
            }

            val column = fileBuilder.columns[columnIndex]

            println("Select font size for column '${column.header}' (default is 12):")
            val columnFontSize = readln().toIntOrNull() ?: 12

            println("Choose column text color for '${column.header}' (1: BLACK, 2: RED, 3: GREEN, 4: BLUE):")
            val columnTextColorChoice = readln().toIntOrNull() ?: 1
            val textColor = getColorByChoice(columnTextColorChoice)

            println("Choose column background color for '${column.header}' (1: WHITE, 2: LIGHT_GRAY, 3: YELLOW, 4: BLUE):")
            val columnBackgroundColorChoice = readln().toIntOrNull() ?: 1
            val backgroundColor = getColorByChoice(columnBackgroundColorChoice)

            val newFormatOptions = CellFormatOptions(
                fontSize = columnFontSize,
                backgroundColor = backgroundColor,
                textColor = textColor
            )

            fileBuilder.changeColumnStyleWithIndex(columnIndex, newFormatOptions)

            println("Style for column '${column.header}' updated successfully!")
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

        (exporter as FormatReportExported).exportFormated(file as FormatFile)
    } catch (e: SQLException) {
        e.printStackTrace()
        println("Error executing query or exporting file.")
    } catch (e: Exception) {
        e.printStackTrace()
        println("An error occurred while exporting the report.")
    }
}

fun getColorByChoice(choice: Int): Color {
    return when (choice) {
        1 -> Color.BLACK
        2 -> Color.RED
        3 -> Color.GREEN
        4 -> Color.BLUE
        else -> Color.BLACK
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

