import org.apache.poi.xssf.usermodel.XSSFWorkbook
import src.main.kotlin.FormatReportExported
import src.main.kotlin.file.File
import src.main.kotlin.file.FormatFile
import java.io.FileOutputStream
import java.io.IOException

class ExcelExporter() : FormatReportExported() {
    override val exporterType: String = "XLS"
    override val fileExtension: String = ".xlsx"

    override fun export(file: File) {
        val filePath = "${file.filename}${fileExtension}"

        if (file.columns.isEmpty()) {
            throw IllegalArgumentException("Report cannot be empty")
        }

        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Report")

            var rowIndex = 0

            if (file.title.isNotEmpty()) {
                val titleRow = sheet.createRow(rowIndex)
                val titleCell = titleRow.createCell(0)
                titleCell.setCellValue(file.title)
                rowIndex += 2
            }

            val headerRow = sheet.createRow(rowIndex++)
            val headers =
                (if (file.includeRowNumbers) listOf("Row_Nums") else emptyList()) + file.columns.map { it.header }
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
            }

            val rowCount = file.columns.maxOf { it.content.size }
            for (i in 0 until rowCount) {
                val dataRow = sheet.createRow(rowIndex++)
                var columnIndex = 0

                if (file.includeRowNumbers) {
                    val cell = dataRow.createCell(columnIndex++)
                    cell.setCellValue((i + 1).toString())
                }

                file.columns.forEach { column ->
                    val cellContent = if (i < column.content.size) column.content[i].toString() else ""
                    val cell = dataRow.createCell(columnIndex++)
                    cell.setCellValue(cellContent)
                }
            }

            if (file.summary.isNotEmpty()) {
                rowIndex++
                val summaryHeaderRow = sheet.createRow(rowIndex++)
                val summaryHeaderCell = summaryHeaderRow.createCell(0)
                summaryHeaderCell.setCellValue("Summary")
                file.summary.forEach { (key, value) ->
                    val summaryRow = sheet.createRow(rowIndex++)
                    summaryRow.createCell(0).setCellValue(key)
                    summaryRow.createCell(1).setCellValue(value.toString())
                }
            }

            headers.indices.forEach { sheet.autoSizeColumn(it) }

            FileOutputStream(filePath).use { workbook.write(it) }

            workbook.close()

            println("Export successful! Data saved to $filePath")
        } catch (e: IOException) {
            println("Failed to write Excel file: ${e.message}")
        }
    }

    override fun exportFormated(file: FormatFile) {
        TODO("Not yet implemented")
    }
}