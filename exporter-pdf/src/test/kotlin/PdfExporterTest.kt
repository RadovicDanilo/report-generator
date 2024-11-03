import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import src.main.kotlin.file.File
import src.main.kotlin.file.FormatFile
import src.main.kotlin.file.FormatFileBuilder
import src.main.kotlin.file.format_options.*
import src.main.kotlin.file.format_options.BorderStyle
import java.awt.Color

class PdfExporterTest {

    private fun createTestFile(fileName: String = "test_report"): File {
        val fileBuilder = FormatFileBuilder(fileName)
        fileBuilder.updateTitle("Test Title")
        fileBuilder.titleFormatOptions = TitleFormatOptions(
            fontStyle = FontStyle.BOLD_ITALIC,
            fontSize = 36,
            alignment = Alignment.CENTER,
            color = Color.RED,
            backgroundColor = Color.LIGHT_GRAY
        )
        fileBuilder.includeRowNumbers(true)

        val columnHeaders = arrayOf("Name", "Salary", "City")
        val columnData = arrayOf(
            arrayOf("John", "Michael", "Alice"),
            arrayOf(2000.0, 3000.0, 4000.0),
            arrayOf("New York", "Los Angeles", "Chicago")
        )

        fileBuilder.headerFormatOptions = CellFormatOptions(
            fontStyle = FontStyle.BOLD_UNDERLINE,
            fontSize = 14,
            alignment = Alignment.CENTER,
            textColor = Color.BLUE,
            backgroundColor = Color.CYAN,
        )

        fileBuilder.summaryFormatOptions = SummaryFormatOptions(
            keyColor = Color.WHITE,
            keyBackgroundColor = Color.DARK_GRAY,
            keyStyle = FontStyle.BOLD_ITALIC,
            valueColor = Color.BLACK,
            valueBackgroundColor = Color.YELLOW,
            valueStyle = FontStyle.ITALIC,
            roundingPrecision = 2,
            alignment = Alignment.RIGHT
        )

        fileBuilder.rowNumberFormat = CellFormatOptions(
            fontStyle = FontStyle.BOLD,
            fontSize = 12,
            alignment = Alignment.RIGHT,
            textColor = Color.RED,
            backgroundColor = Color.LIGHT_GRAY,
        )

        fileBuilder.tableFormatOptions = TableFormatOptions(
            outerBorderStyle = BorderStyle.DASHED,
            outerBorderColor = Color.RED,
            horizontalBorderStyle = BorderStyle.DASHED,
            horizontalBorderColor = Color.RED,
            verticalBorderStyle = BorderStyle.DASHED,
            verticalBorderColor = Color.RED,
        )

        fileBuilder.setColumns(
            columnHeaders, columnData as Array<Array<Any>>, CellFormatOptions(
                fontStyle = FontStyle.NORMAL,
                fontSize = 12,
                alignment = Alignment.RIGHT,
                textColor = Color.GREEN,
                backgroundColor = Color.BLACK,
            )
        )

        fileBuilder.addSummaryEntry("Average Salary", arrayOf(2000.0, 3000.0, 4000.0), SummaryCalculationType.AVERAGE)

        return fileBuilder.build()
    }

    @Test
    fun testExportFormattedFile() {
        val formattedFile = createTestFile(fileName = "format_test_report")
        val exporter = PdfExporter()
        exporter.exportFormated(formattedFile as FormatFile)
        assertNotNull(formattedFile)
    }

    @Test
    fun testExportUnformattedFile() {
        val unformattedFile = createTestFile(fileName = "file_test_report")
        val exporter = PdfExporter()
        exporter.export(unformattedFile)
        assertNotNull(unformattedFile)
    }
}
