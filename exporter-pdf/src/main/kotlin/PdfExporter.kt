import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import src.main.kotlin.FormatReportExported
import src.main.kotlin.file.File
import src.main.kotlin.file.FormatFile
import src.main.kotlin.file.format_options.Alignment
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
                (if (file.titleFormatOptions.isBold) Font.BOLD else Font.NORMAL) +
                        (if (file.titleFormatOptions.isItalic) Font.ITALIC else Font.NORMAL),
                BaseColor(
                    file.titleFormatOptions.color.red,
                    file.titleFormatOptions.color.green,
                    file.titleFormatOptions.color.blue
                )
            )

            val title = Paragraph(file.title, titleFont)
            title.alignment = when (file.titleFormatOptions.alignment) {
                Alignment.LEFT -> Paragraph.ALIGN_LEFT
                Alignment.CENTER -> Paragraph.ALIGN_CENTER
                Alignment.RIGHT -> Paragraph.ALIGN_RIGHT
            }
            title.spacingAfter = 20f
            document.add(title)
        }

        val table = PdfPTable(file.columns.size + (if (file.includeRowNumbers) 1 else 0))
        table.widthPercentage = 100f
        table.spacingAfter = 20f

        val headerFont = FontFactory.getFont(
            FontFactory.HELVETICA_BOLD,
            12f,
            (if (file.headerFormatOptions.isBold) Font.BOLD else Font.NORMAL) +
                    (if (file.headerFormatOptions.isItalic) Font.ITALIC else Font.NORMAL),
            BaseColor(
                file.headerFormatOptions.textColor.red,
                file.headerFormatOptions.textColor.green,
                file.headerFormatOptions.textColor.blue
            )
        )

        if (file.includeRowNumbers) {
            table.addCell(PdfPCell(Paragraph("Row_Nums", headerFont)))
        }
        for (header in file.columns.map { it.header }) {
            table.addCell(PdfPCell(Paragraph(header, headerFont)))
        }

        val rowCount = file.columns.maxOf { it.content.size }
        for (i in 0 until rowCount) {
            if (file.includeRowNumbers) {
                table.addCell("${i + 1}")
            }
            for (column in file.columns) {
                val cellContent = if (i < column.content.size) column.content[i].toString() else ""
                table.addCell(cellContent)
            }
        }

        document.add(table)

        val summaryHeaderFont = FontFactory.getFont(
            FontFactory.HELVETICA_BOLD,
            12f,
            (if (file.summaryFormatOptions.isKeyBold) Font.BOLD else Font.NORMAL) +
                    (if (file.summaryFormatOptions.isKeyItalic) Font.ITALIC else Font.NORMAL),
            BaseColor(
                file.summaryFormatOptions.keyColor.red,
                file.summaryFormatOptions.keyColor.green,
                file.summaryFormatOptions.keyColor.blue
            )
        )

        val summaryValueFont = FontFactory.getFont(
            FontFactory.HELVETICA,
            12f,
            (if (file.summaryFormatOptions.isValueBold) Font.BOLD else Font.NORMAL) +
                    (if (file.summaryFormatOptions.isValueItalic) Font.ITALIC else Font.NORMAL),
            BaseColor(
                file.summaryFormatOptions.valueColor.red,
                file.summaryFormatOptions.valueColor.green,
                file.summaryFormatOptions.valueColor.blue
            )
        )

        val summaryParagraph = Paragraph("Summary", summaryHeaderFont)
        summaryParagraph.spacingAfter = 10f
        document.add(summaryParagraph)

        val summaryTable = PdfPTable(2)
        summaryTable.widthPercentage = 100f

        for ((key, value) in file.summary) {
            val keyCell = PdfPCell(Paragraph(key, summaryHeaderFont))
            val valueCell = PdfPCell(Paragraph(value.toString(), summaryValueFont))
            summaryTable.addCell(keyCell)
            summaryTable.addCell(valueCell)
        }

        document.add(summaryTable)
        document.close()
    }

}