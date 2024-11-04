import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import src.main.kotlin.ReportExporter
import src.main.kotlin.file.File
import java.io.FileWriter
import java.io.IOException

class CsvExporter() : ReportExporter() {
    override val exporterType: String = "CSV"
    override val fileExtension: String = ".csv"

    override fun export(file: File) {
        val filePath = "${file.filename}${fileExtension}"

        if (file.columns.isEmpty()) {
            throw IllegalArgumentException("Report cannot be empty")
        }

        try {
            FileWriter(filePath).use { writer ->
                val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)

                if (file.title.isNotEmpty()) {
                    csvPrinter.printRecord(file.title)
                    csvPrinter.println()
                }

                val headers =
                    (if (file.includeRowNumbers) listOf("Row_Nums") else emptyList()) + file.columns.map { it.header }
                csvPrinter.printRecord(headers)

                val rowCount = file.columns.maxOf { it.content.size }
                for (i in 0 until rowCount) {
                    val rowData = mutableListOf<String>()

                    if (file.includeRowNumbers) {
                        rowData.add((i + 1).toString())
                    }

                    for (column in file.columns) {
                        val cellContent = if (i < column.content.size) column.content[i].toString() else ""
                        rowData.add(cellContent)
                    }

                    csvPrinter.printRecord(rowData)
                }

                csvPrinter.println()
                csvPrinter.printRecord("Summary")
                file.summary.forEach { (key, value) ->
                    csvPrinter.printRecord(key, value)
                }

                csvPrinter.flush()
                println("Export successful! Data saved to $filePath")
            }
        } catch (e: IOException) {
            println("Failed to write CSV file: ${e.message}")
        }
    }
}