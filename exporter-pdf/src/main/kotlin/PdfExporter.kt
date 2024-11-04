import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfPTableEvent
import com.itextpdf.text.pdf.PdfWriter
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
import java.io.FileOutputStream

class PdfExporter() : FormatReportExported() {
    override val exporterType: String = "PDF"
    override val fileExtension: String = ".pdf"

    override fun export(file: File) {
        val filePath = "${file.filename}${fileExtension}"

        if (file.columns.isEmpty()) {
            throw IllegalArgumentException("Report cannot be empty")
        }

        val document = Document()
        PdfWriter.getInstance(document, FileOutputStream(filePath))
        document.open()

        if (file.filename.isNotEmpty()) {
            val titleFont = FontFactory.getFont(FontFactory.HELVETICA, 24f)
            val title = Paragraph(file.title, titleFont)

            title.alignment = Paragraph.ALIGN_CENTER
            title.spacingAfter = 20f

            document.add(title)
        }

        val table = PdfPTable(
            file.columns.size + (if (file.includeRowNumbers) 1 else 0)
        )
        table.widthPercentage = 100f
        table.spacingAfter = 20f

        if (file.includeRowNumbers || file.columns.any { it.header.isNotEmpty() }) {
            val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f)
            if (file.includeRowNumbers)
                table.addCell(PdfPCell(Paragraph("Row_Nums", headerFont)))
            for (header in file.columns.map { it.header }) {
                table.addCell(PdfPCell(Paragraph(header, headerFont)))
            }
        }

        val rowCount = file.columns.maxOf { it.content.size }
        for (i in 0 until rowCount) {
            if (file.includeRowNumbers)
                table.addCell("${i + 1}")

            for (column in file.columns) {
                val cellContent = if (i < column.content.size) {
                    table.addCell(column.content[i].toString())
                } else {
                    table.addCell("")
                }
                table.addCell(cellContent.toString())
            }
        }

        document.add(table)

        val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f)
        val summaryParagraph = Paragraph("Summary", headerFont)
        summaryParagraph.spacingAfter = 10f
        document.add(summaryParagraph)

        val summaryTable = PdfPTable(2)
        summaryTable.setWidthPercentage(100f)

        for ((key, value) in file.summary) {
            summaryTable.addCell(key)
            summaryTable.addCell(value.toString())
        }

        document.add(summaryTable)

        document.close()
        println("Export successful! Data saved to $filePath")
    }

    override fun exportFormated(file: FormatFile) {
        val filePath = "${file.filename}${fileExtension}"

        if (file.columns.isEmpty()) {
            throw IllegalArgumentException("Report cannot be empty")
        }

        val document = Document()
        PdfWriter.getInstance(document, FileOutputStream(filePath))
        document.open()

        if (file.title.isNotEmpty()) {
            val titleFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                file.titleFormatOptions.fontSize.toFloat(),
                getFontStyle(file.titleFormatOptions.fontStyle),
                BaseColor(
                    file.titleFormatOptions.color.red,
                    file.titleFormatOptions.color.green,
                    file.titleFormatOptions.color.blue
                )
            )
            val title = Paragraph(file.title, titleFont)
            title.alignment = getAlignment(file.titleFormatOptions.alignment)
            title.spacingAfter = 20f
            document.add(title)
        }

        val table = PdfPTable(file.columns.size + (if (file.includeRowNumbers) 1 else 0))
        table.widthPercentage = 100f
        table.spacingAfter = 20f

        val headerFont = FontFactory.getFont(
            FontFactory.HELVETICA,
            file.headerFormatOptions.fontSize.toFloat(),
            getFontStyle(file.headerFormatOptions.fontStyle),
            BaseColor(
                file.headerFormatOptions.textColor.red,
                file.headerFormatOptions.textColor.green,
                file.headerFormatOptions.textColor.blue
            )
        )

        val headerBackgroundColor = BaseColor(
            file.headerFormatOptions.backgroundColor.red,
            file.headerFormatOptions.backgroundColor.green,
            file.headerFormatOptions.backgroundColor.blue
        )

        if (file.includeRowNumbers) {
            val rowNumberCell = PdfPCell(Paragraph("Row_Nums", headerFont))
            rowNumberCell.backgroundColor = headerBackgroundColor
            rowNumberCell.horizontalAlignment = getAlignment(file.headerFormatOptions.alignment)
            table.addCell(rowNumberCell)
        }

        for (header in file.columns.map { it.header }) {
            val cell = PdfPCell(Paragraph(header, headerFont))
            cell.backgroundColor = headerBackgroundColor
            cell.horizontalAlignment = getAlignment(file.headerFormatOptions.alignment)
            table.addCell(cell)
        }

        val numRowFont = FontFactory.getFont(
            FontFactory.HELVETICA,
            file.rowNumberFormat.fontSize.toFloat(),
            getFontStyle(file.rowNumberFormat.fontStyle),
            BaseColor(
                file.rowNumberFormat.textColor.red,
                file.rowNumberFormat.textColor.green,
                file.rowNumberFormat.textColor.blue
            )
        )

        val columnFonts = file.columns.map { column ->
            val formatOptions = when {
                (column is StringColumn && column as StringColumn is FormatStringColumn) -> (column as FormatStringColumn).columnFormatOptions
                (column is NumberColumn && column as NumberColumn is FormatNumberColumn) -> (column as FormatNumberColumn).columnFormatOptions
                else -> CellFormatOptions()
            }

            FontFactory.getFont(
                FontFactory.HELVETICA,
                formatOptions.fontSize.toFloat(),
                getFontStyle(formatOptions.fontStyle),
                BaseColor(
                    formatOptions.textColor.red,
                    formatOptions.textColor.green,
                    formatOptions.textColor.blue
                )
            )
        }

        for (i in 0 until file.columns.maxOf { it.content.size }) {
            if (file.includeRowNumbers) {
                val rowNumberCell = PdfPCell(Paragraph((i + 1).toString(), numRowFont))
                rowNumberCell.backgroundColor = BaseColor(
                    file.rowNumberFormat.backgroundColor.red,
                    file.rowNumberFormat.backgroundColor.green,
                    file.rowNumberFormat.backgroundColor.blue
                )
                rowNumberCell.horizontalAlignment = getAlignment(file.rowNumberFormat.alignment)
                table.addCell(rowNumberCell)
            }

            file.columns.forEachIndexed { index, column ->
                val cellContent = if (i < column.content.size) column.content[i].toString() else ""
                val cellFont = columnFonts[index]

                val cell = PdfPCell(Paragraph(cellContent, cellFont))
                val formatOptions = when {
                    (column is StringColumn && column as StringColumn is FormatStringColumn) -> (column as FormatStringColumn).columnFormatOptions
                    (column is NumberColumn && column as NumberColumn is FormatNumberColumn) -> (column as FormatNumberColumn).columnFormatOptions
                    else -> CellFormatOptions()
                }

                cell.backgroundColor = BaseColor(
                    formatOptions.backgroundColor.red,
                    formatOptions.backgroundColor.green,
                    formatOptions.backgroundColor.blue
                )
                cell.horizontalAlignment = getAlignment(formatOptions.alignment)
                table.addCell(cell)
            }
        }

        for (rowIndex in 0 until table.size()) {
            val row = table.getRow(rowIndex)
            row.cells?.forEach { cell ->
                if (cell != null) {
                    cell.borderWidthTop = getBorderWidth(file.tableFormatOptions.horizontalBorderStyle)
                    cell.borderWidthBottom = cell.borderWidthTop
                    cell.borderColorTop = BaseColor(
                        file.tableFormatOptions.horizontalBorderColor.red,
                        file.tableFormatOptions.horizontalBorderColor.green,
                        file.tableFormatOptions.horizontalBorderColor.blue
                    )
                    cell.borderColorBottom = cell.borderColorTop

                    cell.borderWidthLeft = getBorderWidth(file.tableFormatOptions.verticalBorderStyle)
                    cell.borderWidthRight = cell.borderWidthLeft
                    cell.borderColorLeft = BaseColor(
                        file.tableFormatOptions.verticalBorderColor.red,
                        file.tableFormatOptions.verticalBorderColor.green,
                        file.tableFormatOptions.verticalBorderColor.blue
                    )
                    cell.borderColorRight = cell.borderColorLeft
                }
            }
        }

        table.tableEvent = object : PdfPTableEvent {
            override fun tableLayout(
                table: PdfPTable?, widths: Array<FloatArray>?, heights: FloatArray?,
                headerRows: Int, rowStart: Int, canvases: Array<PdfContentByte>
            ) {
                val outerColor = BaseColor(
                    file.tableFormatOptions.outerBorderColor.red,
                    file.tableFormatOptions.outerBorderColor.green,
                    file.tableFormatOptions.outerBorderColor.blue
                )

                val canvas = canvases[PdfPTable.BASECANVAS]
                canvas.setLineWidth(getBorderWidth(file.tableFormatOptions.outerBorderStyle))
                canvas.setColorStroke(outerColor)
                canvas.rectangle(
                    widths?.firstOrNull()?.firstOrNull() ?: 0f,
                    heights?.lastOrNull() ?: 0f,
                    widths?.lastOrNull()?.lastOrNull()?.minus(widths.first().first()) ?: 0f,
                    heights?.firstOrNull() ?: 0f
                )
                canvas.stroke()
            }
        }

        document.add(table)

        val summaryTable = PdfPTable(2)
        summaryTable.widthPercentage = 100f

        val summaryKeyFont = FontFactory.getFont(
            FontFactory.HELVETICA,
            14f,
            getFontStyle(file.summaryFormatOptions.keyStyle),
            BaseColor(
                file.summaryFormatOptions.keyColor.red,
                file.summaryFormatOptions.keyColor.green,
                file.summaryFormatOptions.keyColor.blue
            )
        )

        val summaryValueFont = FontFactory.getFont(
            FontFactory.HELVETICA,
            14f,
            getFontStyle(file.summaryFormatOptions.valueStyle),
            BaseColor(
                file.summaryFormatOptions.valueColor.red,
                file.summaryFormatOptions.valueColor.green,
                file.summaryFormatOptions.valueColor.blue
            )
        )

        for ((key, value) in file.summary) {
            val keyCell = PdfPCell(Paragraph(key, summaryKeyFont))
            keyCell.backgroundColor = BaseColor(
                file.summaryFormatOptions.keyBackgroundColor.red,
                file.summaryFormatOptions.keyBackgroundColor.green,
                file.summaryFormatOptions.keyBackgroundColor.blue
            )
            keyCell.horizontalAlignment = getAlignment(file.summaryFormatOptions.alignment)

            val valueCell = PdfPCell(
                Paragraph(
                    value.toString(),
                    summaryValueFont
                )
            )
            valueCell.backgroundColor = BaseColor(
                file.summaryFormatOptions.valueBackgroundColor.red,
                file.summaryFormatOptions.valueBackgroundColor.green,
                file.summaryFormatOptions.valueBackgroundColor.blue
            )
            valueCell.horizontalAlignment = getAlignment(file.summaryFormatOptions.alignment)

            summaryTable.addCell(keyCell)
            summaryTable.addCell(valueCell)
        }

        document.add(summaryTable)
        document.close()
        println("Export successful! Data saved to $filePath")
    }

    private fun getFontStyle(style: FontStyle): Int {
        return when (style) {
            FontStyle.NORMAL -> Font.NORMAL
            FontStyle.BOLD -> Font.BOLD
            FontStyle.ITALIC -> Font.ITALIC
            FontStyle.UNDERLINE -> Font.UNDERLINE
            FontStyle.BOLD_ITALIC -> Font.BOLDITALIC
            FontStyle.BOLD_UNDERLINE -> Font.BOLD or Font.UNDERLINE
        }
    }

    private fun getAlignment(alignment: Alignment): Int {
        return when (alignment) {
            Alignment.LEFT -> Element.ALIGN_LEFT
            Alignment.CENTER -> Element.ALIGN_CENTER
            Alignment.RIGHT -> Element.ALIGN_RIGHT
        }
    }

    private fun getBorderWidth(style: BorderStyle): Float = when (style) {
        BorderStyle.NORMAL -> 0.5f
        BorderStyle.BOLD -> 1.5f
        BorderStyle.DASHED -> 0.5f
    }

}