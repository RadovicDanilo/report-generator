import src.main.kotlin.ReportExporter
import src.main.kotlin.file.File
import java.io.FileWriter
import java.io.IOException

class TextExporter : ReportExporter() {
    override val exporterType: String = "TXT"

    override fun export(file: File) {
        val filePath = "${file.filename}.txt"

        if (file.columns.isEmpty()) {
            throw IllegalArgumentException("Report cannot be empty")
        }

        val columnWidths = (if (file.includeRowNumbers) listOf(8) else emptyList()) +
                file.columns.map { column ->
                    maxOf(
                        column.header.length,
                        column.content.maxOf { it.toString().length }
                    )
                }

        fun formatCell(content: String, width: Int): String = content.padEnd(width + 4, ' ')

        fun writeRow(writer: FileWriter, rowContent: List<String>, widths: List<Int>) {
            rowContent.forEachIndexed { index, content ->
                writer.write(formatCell(content, widths[index]))
            }
            writer.write("\n")
        }

        try {
            FileWriter(filePath).use { writer ->
                if (file.title.isNotEmpty()) {
                    writer.write("${file.title}\n\n")
                }

                val headers =
                    (if (file.includeRowNumbers) listOf("Row_Nums") else emptyList()) + file.columns.map { it.header }
                writeRow(writer, headers, columnWidths)

                val rowCount = file.columns.maxOf { it.content.size }
                for (i in 0 until rowCount) {
                    val rowData = mutableListOf<String>()

                    if (file.includeRowNumbers) {
                        rowData.add((i + 1).toString())
                    }

                    for (column in file.columns) {
                        val cellContent = if (i < column.content.size) {
                            column.content[i].toString()
                        } else {
                            ""
                        }
                        rowData.add(cellContent)
                    }

                    writeRow(writer, rowData, columnWidths)
                }

                if (file.summary.isNotEmpty()) {
                    writer.write("\nSummary:\n")
                    file.summary.forEach { (key, value) ->
                        writer.write("$key: $value\n")
                    }
                }

                println("Export successful! Data saved to $filePath")
            }
        } catch (e: IOException) {
            println("Failed to write file: ${e.message}")
        }
    }
}
