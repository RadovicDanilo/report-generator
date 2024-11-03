import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import src.main.kotlin.file.File
import src.main.kotlin.file.FormatFile
import src.main.kotlin.file.FormatFileBuilder
import src.main.kotlin.file.format_options.Alignment
import src.main.kotlin.file.format_options.CellFormatOptions
import src.main.kotlin.file.format_options.SummaryFormatOptions
import src.main.kotlin.file.format_options.TitleFormatOptions
import java.awt.Color

class PdfExporterTest {

    private fun createTestFile(includeRowNumbers: Boolean, fileName: String = "test_report"): File {
        val fileBuilder = FormatFileBuilder(fileName)
        fileBuilder.updateTitle("Test Title")
        fileBuilder.titleFormatOptions = TitleFormatOptions(
            true,false,true,36, Alignment.CENTER, Color.RED
        )
        fileBuilder.includeRowNumbers(includeRowNumbers)

        val columnHeaders = arrayOf("Name", "Salary", "City")
        val columnData = arrayOf(
            arrayOf("John", "Michael", "Alice"),
            arrayOf(2000.0, 3000.0, 4000.0),
            arrayOf("New York", "Los Angeles", "Chicago")
        )

        fileBuilder.headerFormatOptions = CellFormatOptions(
            isBold = true,
            isItalic = false,
            isUnderline = true,
            alignment = Alignment.CENTER,
            textColor = Color.BLUE,
            backgroundColor = Color.CYAN
        )

        fileBuilder.summaryFormatOptions = SummaryFormatOptions(
            Color.WHITE,
            Color.YELLOW,
            isKeyBold = true,
            isKeyItalic = true,
            isKeyUnderlined = false,
            Color.GREEN,
            Color.RED,
            isValueBold = true,
            isValueItalic = true,
            isValueUnderlined = false,
            2
        )

        fileBuilder.rowNumberFormat = CellFormatOptions(
            isBold = true,
            isItalic = false,
            isUnderline = false,
            alignment = Alignment.RIGHT,
            textColor = Color.RED,
            backgroundColor = Color.LIGHT_GRAY
        )

        fileBuilder.setColumns(
            columnHeaders, columnData as Array<Array<Any>>, CellFormatOptions(
                isBold = false,
                isItalic = false,
                isUnderline = false,
                alignment = Alignment.RIGHT,
                textColor = Color.GREEN,
                backgroundColor = Color.BLACK
            )
        )

        fileBuilder.addSummaryEntry("Average Salary", arrayOf(2000.0, 3000.0, 4000.0), SummaryCalculationType.AVERAGE)

        return fileBuilder.build()
    }

    @Test
    fun testExportFormattedFileWithRowNumbers() {
        val formattedFile = createTestFile(includeRowNumbers = true, fileName = "format_test_report_with_row_numbers")
        val exporter = PdfExporter()
        exporter.exportFormated(formattedFile as FormatFile)
        assertNotNull(formattedFile)
    }

    @Test
    fun testExportUnformattedFileWithRowNumbers() {
        val unformattedFile = createTestFile(includeRowNumbers = true, fileName = "file_test_report_with_row_numbers")
        val exporter = PdfExporter()
        exporter.export(unformattedFile)
        assertNotNull(unformattedFile)
    }

    @Test
    fun testExportUnformattedFileWithoutRowNumbers() {
        val unformattedFile =
            createTestFile(includeRowNumbers = false, fileName = "file_test_report_without_row_numbers")
        val exporter = PdfExporter()
        exporter.export(unformattedFile)
        assertNotNull(unformattedFile)
    }
}
