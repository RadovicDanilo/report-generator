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
    if (args.isEmpty()) {
        throw IllegalArgumentException("Connection string required")
    }

    val connectionString = args[0]

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
            continue
        }

        val selectedExporter = exporters[input.toInt() - 1]

        var useFormatedExmprter = false

        if (selectedExporter is FormatReportExported) {
            println("Would you like to make a report with formating? (Y/N) (Default = Y)")
            val choice = readln()
            useFormatedExmprter = choice.uppercase() != "N"
        }

        if (useFormatedExmprter) {
            createFormatReport(connection, selectedExporter)
        } else {
            creatNormalReport(connection, selectedExporter)
        }

        println("Would you like to generate more reports? (Y/N) (Default = N): ")
        val opt = readln()
        if (opt.uppercase() != "Y")
            break
    }
}

fun creatNormalReport(connection: Connection, exporter: ReportExporter) {
    val query = promptForInput("Enter your SQL query:")
    val fileName = promptForInput("Enter a filename (without extension):")
    val fileBuilder = FileBuilder(fileName)

    val title = promptForInput("Enter a title for the report (leave blank for no title):")
    fileBuilder.updateTitle(title)

    fileBuilder.includeRowNumbers = promptForYesNo("Include row numbers? (Y/N, default is N):")

    try {
        fileBuilder.setColumnsFromSQL(query, connection)

        handleCalculatedColumns(fileBuilder)

        handleSummaryEntries(fileBuilder)

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

fun createFormatReport(connection: Connection, exporter: ReportExporter) {
    val query = promptForInput("Enter your SQL query:")
    val fileName = promptForInput("Enter a filename (without extension):")
    val fileBuilder = FormatFileBuilder(fileName)

    val title = promptForInput("Enter a title for the report (leave blank for no title):")
    fileBuilder.updateTitle(title)

    if (title.isNotEmpty()) {
        fileBuilder.titleFormatOptions = fileBuilder.titleFormatOptions.copy(
            fontSize = promptForInt("Select font size for the title (default is 24):", 24),
            color = promptForColor("Choose title text color "),
            backgroundColor = promptForColor("Choose title text color "),
        )
    }

    fileBuilder.includeRowNumbers = promptForYesNo("Include row numbers? (Y/N, default is N):")

    if (fileBuilder.includeRowNumbers) {
        fileBuilder.rowNumberFormat.fontSize = promptForInt("Select font size for row numbers (default is 12):", 12)
        fileBuilder.rowNumberFormat.backgroundColor =
            promptForColor("Choose row number background color ")
        fileBuilder.rowNumberFormat.textColor =
            promptForColor("Choose row number text color ")
    }

    fileBuilder.headerFormatOptions = fileBuilder.headerFormatOptions.copy(
        fontSize = promptForInt("Select font size for the header (default is 12):", 12),
        textColor = promptForColor("Choose header text color "),
        backgroundColor = promptForColor("Choose header text color "),
        fontStyle = promptForFontStyle("Choose header font style (1: NORMAL, 2: BOLD, 3: ITALIC, 4: BOLD_ITALIC):")
    )

    try {
        fileBuilder.setColumnsFromSQL(query, connection)

        handleCalculatedColumns(fileBuilder)
        handleColumnStyle(fileBuilder)
        handleSummaryEntries(fileBuilder)

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

fun handleCalculatedColumns(fileBuilder: FileBuilder) {
    while (promptForYesNo("Would you like to add a calculated column? (Y/N, default is N):")) {
        val calcType = promptForCalculationType("Choose an operation for the calculated column: ADD, SUB, PROD, DIV:")
        val columnIndices = promptForColumnIndices(
            fileBuilder,
            "Enter the indices of the columns to use for this calculation, separated by spaces:"
        )

        val selectedData = columnIndices.map { fileBuilder.columns[it].content as Array<Double> }.toTypedArray()
        val calculatedContent = ColumnContentCalculator.calculateColumnContent(selectedData, calcType)

        val calculatedHeader = promptForInput("Enter a header for the calculated column:")
        fileBuilder.addNumberColumn(calculatedHeader, calculatedContent as Array<Number>)
        println("Calculated column '$calculatedHeader' added successfully.")
    }
}

fun handleSummaryEntries(fileBuilder: FileBuilder) {
    while (promptForYesNo("Would you like to add a summary entry? (Y/N, default is N):")) {
        val summaryType = promptForSummaryType("Choose a summary type: SUM, AVERAGE, COUNT:")
        val columnIndex =
            promptForColumnIndex(fileBuilder, "Enter the index of the column to calculate the summary for:")

        val column = fileBuilder.columns[columnIndex]
        val summaryResult = when (column) {
            is NumberColumn -> SummaryEntryCalculator.calculateSummaryEntry(
                column.content as Array<Double>,
                summaryType
            ) as Double

            is StringColumn -> SummaryEntryCalculator.calculateSummaryEntry(
                column.content as Array<String>,
                summaryType
            ) as Double

            else -> continue
        }

        val summaryKey = promptForInput(
            "Enter a key name for this summary entry:"
        )
        fileBuilder.addSummaryEntry(summaryKey, summaryResult)
        println("Summary entry '$summaryKey' added successfully with result: $summaryResult")
    }
}

fun handleColumnStyle(fileBuilder: FormatFileBuilder) {
    while (promptForYesNo("Would you like to edit the style of a column? (Y/N, default is N):")) {
        val columnIndex = promptForColumnIndex(fileBuilder, "Enter the index of the column to edit style:")
        val column = fileBuilder.columns[columnIndex]

        val newFormatOptions = CellFormatOptions(
            fontSize = promptForInt("Select font size for column '${column.header}' (default is 12):", 12),
            textColor = promptForColor("Choose column text color for '${column.header}' "),
            backgroundColor = promptForColor("Choose column background color for '${column.header}' ")
        )

        fileBuilder.changeColumnStyleWithIndex(columnIndex, newFormatOptions)
        println("Style for column '${column.header}' updated successfully!")
    }
}

fun promptForInput(prompt: String): String {
    println(prompt)
    return readln()
}

fun promptForYesNo(prompt: String): Boolean {
    println(prompt)
    return readln().uppercase() == "Y"
}

fun promptForInt(prompt: String, defaultValue: Int): Int {
    println(prompt)
    return readln().toIntOrNull() ?: defaultValue
}

fun promptForColor(prompt: String): Color {
    println("$prompt (1: BLACK, 2: WHITE, 3: RED, 4: GREEN, 5: BLUE, 6: YELLOW, 7: GRAY, 8: LIGHT_GRAY: ")
    return getColorByChoice(readln().toIntOrNull() ?: 1)
}

fun promptForCalculationType(prompt: String): ColumnCalculationType {
    println(prompt)
    return ColumnCalculationType.valueOf(readln().uppercase())
}

fun promptForSummaryType(prompt: String): SummaryCalculationType {
    println(prompt)
    return SummaryCalculationType.valueOf(readln().uppercase())
}

fun promptForColumnIndices(fileBuilder: FileBuilder, prompt: String): List<Int> {
    println(prompt)
    return readln().split(" ").mapNotNull { it.toIntOrNull() }.filter { it in fileBuilder.columns.indices }
}

fun promptForColumnIndex(fileBuilder: FileBuilder, prompt: String): Int {
    println(prompt)
    return readln().toIntOrNull()?.takeIf { it in fileBuilder.columns.indices } ?: -1
}

fun promptForAlignment(prompt: String): Alignment {
    println(prompt)
    return when (readln().toIntOrNull() ?: 1) {
        1 -> Alignment.LEFT
        2 -> Alignment.CENTER
        3 -> Alignment.RIGHT
        else -> Alignment.LEFT
    }
}

fun promptForFontStyle(prompt: String): FontStyle {
    println(prompt)
    return when (readln().toIntOrNull() ?: 1) {
        1 -> FontStyle.NORMAL
        2 -> FontStyle.BOLD
        3 -> FontStyle.ITALIC
        4 -> FontStyle.BOLD_ITALIC
        else -> FontStyle.NORMAL
    }
}

fun getColorByChoice(choice: Int): Color {
    return when (choice) {
        1 -> Color.BLACK
        2 -> Color.WHITE
        3 -> Color.RED
        4 -> Color.GREEN
        5 -> Color.BLUE
        6 -> Color.YELLOW
        7 -> Color.GRAY
        8 -> Color.LIGHT_GRAY
        else -> Color.BLACK
    }
}
