import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import src.main.kotlin.file.File
import src.main.kotlin.file.FileBuilder

class CsvExporterTest {

    private fun createTestFile(includeRowNumbers: Boolean, file_name: String = "test_report"): File {
        val fileBuilder = FileBuilder(file_name)
        fileBuilder.updateTitle("Test Title")
        fileBuilder.includeRowNumbers(includeRowNumbers)

        val columnHeaders = arrayOf("name", "age", "city")
        val columnData = arrayOf(
            arrayOf("John", "Michaellllllllllllllll", "Alice"),
            arrayOf("25"),
            arrayOf("New York", "Los Angeles", "Chicago", "Houston")
        )

        fileBuilder.setColumns(columnHeaders, columnData as Array<Array<Any>>)
        fileBuilder.addSummaryEntry("Total", 3)

        return fileBuilder.build()
    }

    @Test
    fun testExportWithRowNumbers() {
        val file = createTestFile(includeRowNumbers = true, file_name = "test_report_nums")
        val exporter = CsvExporter()
        exporter.export(file)
        assertNotNull(file)
    }

    @Test
    fun testExportWithoutRowNumbers() {
        val file = createTestFile(includeRowNumbers = false)
        val exporter = CsvExporter()
        exporter.export(file)
        assertNotNull(file)
    }
}
