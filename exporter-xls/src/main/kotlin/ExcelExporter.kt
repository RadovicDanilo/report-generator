import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.FontUnderline
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import src.main.kotlin.FormatReportExported
import src.main.kotlin.file.File
import src.main.kotlin.file.FormatFile
import src.main.kotlin.file.column.FormatNumberColumn
import src.main.kotlin.file.column.FormatStringColumn
import src.main.kotlin.file.column.NumberColumn
import src.main.kotlin.file.column.StringColumn
import src.main.kotlin.file.format_options.Alignment
import src.main.kotlin.file.format_options.BorderStyle
import src.main.kotlin.file.format_options.CellFormatOptions
import src.main.kotlin.file.format_options.FontStyle
import java.awt.Color
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
                val titleRow = sheet.createRow(rowIndex++)
                val titleCell = titleRow.createCell(0)
                titleCell.setCellValue(file.title)
                rowIndex++
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
        val filePath = "${file.filename}${fileExtension}"

        if (file.columns.isEmpty()) {
            throw IllegalArgumentException("Report cannot be empty")
        }

        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Report")

            var rowIndex = 0

            fun getColor(color: Color): XSSFColor {
                return XSSFColor(
                    byteArrayOf(
                        color.red.toByte(),
                        color.green.toByte(),
                        color.blue.toByte()
                    ), null
                )
            }

            if (file.title.isNotEmpty()) {
                val titleRow = sheet.createRow(rowIndex++)
                val titleCell = titleRow.createCell(0)
                titleCell.setCellValue(file.title)

                val titleFont = workbook.createFont().apply {
                    bold =
                        (file.titleFormatOptions.fontStyle == FontStyle.BOLD || file.titleFormatOptions.fontStyle == FontStyle.BOLD_ITALIC || file.titleFormatOptions.fontStyle == FontStyle.BOLD_UNDERLINE)
                    italic =
                        (file.titleFormatOptions.fontStyle == FontStyle.ITALIC || file.titleFormatOptions.fontStyle == FontStyle.BOLD_ITALIC)
                    underline =
                        if (file.titleFormatOptions.fontStyle == FontStyle.UNDERLINE || file.titleFormatOptions.fontStyle == FontStyle.BOLD_UNDERLINE)
                            FontUnderline.SINGLE.byteValue else FontUnderline.NONE.byteValue
                    fontHeightInPoints = file.titleFormatOptions.fontSize.toShort()
                    this.setColor(
                        getColor(
                            file.titleFormatOptions.color
                        )
                    )
                }

                val titleStyle = workbook.createCellStyle()
                titleStyle.setFont(titleFont)
                titleStyle.alignment = when (file.titleFormatOptions.alignment) {
                    Alignment.LEFT -> HorizontalAlignment.LEFT
                    Alignment.CENTER -> HorizontalAlignment.CENTER
                    Alignment.RIGHT -> HorizontalAlignment.RIGHT
                }

                titleStyle.setFillForegroundColor(
                    getColor(
                        file.titleFormatOptions.backgroundColor
                    )
                )
                titleStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

                titleCell.cellStyle = titleStyle
                sheet.addMergedRegion(
                    CellRangeAddress(
                        rowIndex,
                        rowIndex,
                        0,
                        file.columns.size
                    )
                )
                rowIndex++
            }

            val headerRow = sheet.createRow(rowIndex++)

            val headerFont = workbook.createFont().apply {
                bold =
                    (file.headerFormatOptions.fontStyle == FontStyle.BOLD || file.headerFormatOptions.fontStyle == FontStyle.BOLD_ITALIC || file.headerFormatOptions.fontStyle == FontStyle.BOLD_UNDERLINE)
                italic =
                    (file.headerFormatOptions.fontStyle == FontStyle.ITALIC || file.headerFormatOptions.fontStyle == FontStyle.BOLD_ITALIC)
                underline =
                    if (file.headerFormatOptions.fontStyle == FontStyle.UNDERLINE || file.headerFormatOptions.fontStyle == FontStyle.BOLD_UNDERLINE)
                        FontUnderline.SINGLE.byteValue else FontUnderline.NONE.byteValue
                fontHeightInPoints = file.headerFormatOptions.fontSize.toShort()
                this.setColor(
                    getColor(
                        file.headerFormatOptions.textColor
                    )
                )
            }

            val headerStyle = workbook.createCellStyle().apply {
                setFont(headerFont)
                alignment = when (file.headerFormatOptions.alignment) {
                    Alignment.LEFT -> HorizontalAlignment.LEFT
                    Alignment.CENTER -> HorizontalAlignment.CENTER
                    Alignment.RIGHT -> HorizontalAlignment.RIGHT
                }
                setFillForegroundColor(
                    getColor(
                        file.headerFormatOptions.backgroundColor
                    )
                )
                fillPattern = FillPatternType.SOLID_FOREGROUND
            }

            val headers =
                (if (file.includeRowNumbers) listOf("Row_Nums") else emptyList()) + file.columns.map { it.header }
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                cell.cellStyle = headerStyle
            }

            val rowNumFont = workbook.createFont().apply {
                bold =
                    (file.rowNumberFormat.fontStyle == FontStyle.BOLD || file.rowNumberFormat.fontStyle == FontStyle.BOLD_ITALIC || file.rowNumberFormat.fontStyle == FontStyle.BOLD_UNDERLINE)
                italic =
                    (file.rowNumberFormat.fontStyle == FontStyle.ITALIC || file.rowNumberFormat.fontStyle == FontStyle.BOLD_ITALIC)
                underline =
                    if (file.rowNumberFormat.fontStyle == FontStyle.UNDERLINE || file.rowNumberFormat.fontStyle == FontStyle.BOLD_UNDERLINE)
                        FontUnderline.SINGLE.byteValue else FontUnderline.NONE.byteValue
                fontHeightInPoints = file.rowNumberFormat.fontSize.toShort()
                this.setColor(
                    getColor(
                        file.rowNumberFormat.textColor
                    )
                )
            }

            val rowNumStyle = workbook.createCellStyle().apply {
                setFont(rowNumFont)
                alignment = when (file.rowNumberFormat.alignment) {
                    Alignment.LEFT -> HorizontalAlignment.LEFT
                    Alignment.CENTER -> HorizontalAlignment.CENTER
                    Alignment.RIGHT -> HorizontalAlignment.RIGHT
                }
                setFillForegroundColor(
                    getColor(
                        file.rowNumberFormat.backgroundColor
                    )
                )
                fillPattern = FillPatternType.SOLID_FOREGROUND
            }

            val columnFonts = file.columns.map { column ->
                val formatOptions = when {
                    (column is StringColumn && column as StringColumn is FormatStringColumn) -> (column as FormatStringColumn).columnFormatOptions
                    (column is NumberColumn && column as NumberColumn is FormatNumberColumn) -> (column as FormatNumberColumn).columnFormatOptions
                    else -> CellFormatOptions()
                }

                val rowNumFont = workbook.createFont().apply {
                    bold =
                        (formatOptions.fontStyle == FontStyle.BOLD || formatOptions.fontStyle == FontStyle.BOLD_ITALIC || formatOptions.fontStyle == FontStyle.BOLD_UNDERLINE)
                    italic =
                        (formatOptions.fontStyle == FontStyle.ITALIC || formatOptions.fontStyle == FontStyle.BOLD_ITALIC)
                    underline =
                        if (formatOptions.fontStyle == FontStyle.UNDERLINE || formatOptions.fontStyle == FontStyle.BOLD_UNDERLINE)
                            FontUnderline.SINGLE.byteValue else FontUnderline.NONE.byteValue
                    fontHeightInPoints = formatOptions.fontSize.toShort()
                    this.setColor(
                        getColor(
                            formatOptions.textColor
                        )
                    )
                }

                workbook.createCellStyle().apply {
                    setFont(rowNumFont)
                    alignment = when (formatOptions.alignment) {
                        Alignment.LEFT -> HorizontalAlignment.LEFT
                        Alignment.CENTER -> HorizontalAlignment.CENTER
                        Alignment.RIGHT -> HorizontalAlignment.RIGHT
                    }
                    setFillForegroundColor(
                        getColor(
                            formatOptions.backgroundColor
                        )
                    )
                    fillPattern = FillPatternType.SOLID_FOREGROUND
                }
            }

            val rowCount = file.columns.maxOf { it.content.size }
            for (i in 0 until rowCount) {
                val dataRow = sheet.createRow(rowIndex++)
                var columnIndex = 0

                if (file.includeRowNumbers) {
                    val cell = dataRow.createCell(columnIndex++)
                    cell.setCellValue((i + 1).toString())
                    cell.cellStyle = rowNumStyle
                }

                file.columns.forEach { column ->
                    val cellContent = if (i < column.content.size) column.content[i].toString() else ""
                    val cell = dataRow.createCell(columnIndex++)
                    cell.setCellValue(cellContent)
                    cell.cellStyle = columnFonts[i]
                }
            }

            val startRowIndex = if (file.title.isNotEmpty()) 2 else 1
            val endRowIndex = file.columns.maxOf { it.content.size }
            val startColIndex = 0
            val endColIndex = file.columns.size + (if (file.includeRowNumbers) 0 else -1)

            fun getBorderStyle(borderStyle: BorderStyle): org.apache.poi.ss.usermodel.BorderStyle {
                return when (borderStyle) {
                    BorderStyle.NORMAL -> org.apache.poi.ss.usermodel.BorderStyle.NONE
                    BorderStyle.BOLD -> org.apache.poi.ss.usermodel.BorderStyle.THICK
                    BorderStyle.DASHED -> org.apache.poi.ss.usermodel.BorderStyle.DASHED
                }
            }

            for (row in startRowIndex..endRowIndex) {
                for (col in startColIndex..endColIndex) {
                    val cell = sheet.getRow(row)?.getCell(col) ?: sheet.getRow(row).createCell(col)

                    val currentStyle = cell.cellStyle ?: workbook.createCellStyle()
                    currentStyle.apply {
                        setBorderBottom(getBorderStyle(file.tableFormatOptions.horizontalBorderStyle))
                        setBorderTop(getBorderStyle(file.tableFormatOptions.horizontalBorderStyle))
                        setBorderLeft(getBorderStyle(file.tableFormatOptions.verticalBorderStyle))
                        setBorderRight(getBorderStyle(file.tableFormatOptions.verticalBorderStyle))

                        setBottomBorderColor(getColor(file.tableFormatOptions.horizontalBorderColor))
                        setTopBorderColor(getColor(file.tableFormatOptions.horizontalBorderColor))
                        setLeftBorderColor(getColor(file.tableFormatOptions.verticalBorderColor))
                        setRightBorderColor(getColor(file.tableFormatOptions.verticalBorderColor))
                    }

                    cell.cellStyle = currentStyle
                }
            }

            if (file.summary.isNotEmpty()) {
                val keyFont = workbook.createFont().apply {
                    bold =
                        (file.summaryFormatOptions.keyStyle == FontStyle.BOLD || file.summaryFormatOptions.keyStyle == FontStyle.BOLD_ITALIC || file.summaryFormatOptions.keyStyle == FontStyle.BOLD_UNDERLINE)
                    italic =
                        (file.summaryFormatOptions.keyStyle == FontStyle.ITALIC || file.summaryFormatOptions.keyStyle == FontStyle.BOLD_ITALIC)
                    underline =
                        if (file.summaryFormatOptions.keyStyle == FontStyle.UNDERLINE || file.summaryFormatOptions.keyStyle == FontStyle.BOLD_UNDERLINE)
                            FontUnderline.SINGLE.byteValue else FontUnderline.NONE.byteValue
                    fontHeightInPoints = 13
                    this.setColor(
                        XSSFColor(
                            byteArrayOf(
                                file.summaryFormatOptions.keyColor.red.toByte(),
                                file.summaryFormatOptions.keyColor.green.toByte(),
                                file.summaryFormatOptions.keyColor.blue.toByte()
                            ), null
                        )
                    )
                }

                val keyStyle = workbook.createCellStyle().apply {
                    setFont(keyFont)
                    alignment = when (file.summaryFormatOptions.alignment) {
                        Alignment.LEFT -> HorizontalAlignment.LEFT
                        Alignment.CENTER -> HorizontalAlignment.CENTER
                        Alignment.RIGHT -> HorizontalAlignment.RIGHT
                    }
                    setFillForegroundColor(
                        XSSFColor(
                            byteArrayOf(
                                file.summaryFormatOptions.keyBackgroundColor.red.toByte(),
                                file.summaryFormatOptions.keyBackgroundColor.green.toByte(),
                                file.summaryFormatOptions.keyBackgroundColor.blue.toByte()
                            ), null
                        )
                    )
                    fillPattern = FillPatternType.SOLID_FOREGROUND
                }

                val valueFont = workbook.createFont().apply {
                    bold =
                        (file.summaryFormatOptions.valueStyle == FontStyle.BOLD || file.summaryFormatOptions.valueStyle == FontStyle.BOLD_ITALIC || file.summaryFormatOptions.valueStyle == FontStyle.BOLD_UNDERLINE)
                    italic =
                        (file.summaryFormatOptions.valueStyle == FontStyle.ITALIC || file.summaryFormatOptions.valueStyle == FontStyle.BOLD_ITALIC)
                    underline =
                        if (file.summaryFormatOptions.valueStyle == FontStyle.UNDERLINE || file.summaryFormatOptions.valueStyle == FontStyle.BOLD_UNDERLINE)
                            FontUnderline.SINGLE.byteValue else FontUnderline.NONE.byteValue
                    fontHeightInPoints = 13
                    this.setColor(
                        XSSFColor(
                            byteArrayOf(
                                file.summaryFormatOptions.valueColor.red.toByte(),
                                file.summaryFormatOptions.valueColor.green.toByte(),
                                file.summaryFormatOptions.valueColor.blue.toByte()
                            ), null
                        )
                    )
                }

                val valueStyle = workbook.createCellStyle().apply {
                    setFont(valueFont)
                    alignment = when (file.summaryFormatOptions.alignment) {
                        Alignment.LEFT -> HorizontalAlignment.LEFT
                        Alignment.CENTER -> HorizontalAlignment.CENTER
                        Alignment.RIGHT -> HorizontalAlignment.RIGHT
                    }
                    setFillForegroundColor(
                        XSSFColor(
                            byteArrayOf(
                                file.summaryFormatOptions.valueBackgroundColor.red.toByte(),
                                file.summaryFormatOptions.valueBackgroundColor.green.toByte(),
                                file.summaryFormatOptions.valueBackgroundColor.blue.toByte()
                            ), null
                        )
                    )
                    fillPattern = FillPatternType.SOLID_FOREGROUND
                }

                rowIndex++
                val summaryHeaderRow = sheet.createRow(rowIndex++)
                val summaryHeaderCell = summaryHeaderRow.createCell(0)
                summaryHeaderCell.setCellValue("Summary")
                file.summary.forEach { (key, value) ->
                    val summaryRow = sheet.createRow(rowIndex++)

                    val keyCell = summaryRow.createCell(0)
                    keyCell.setCellValue(key.toString())
                    keyCell.cellStyle = keyStyle

                    val valueCell = summaryRow.createCell(1)
                    valueCell.setCellValue(value.toString())
                    valueCell.cellStyle = valueStyle
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
}