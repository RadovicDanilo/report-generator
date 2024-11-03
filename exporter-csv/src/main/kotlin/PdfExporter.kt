import src.main.kotlin.ReportExporter
import src.main.kotlin.file.File

class CsvExporter() : ReportExporter() {
    override val exporterType: String = "CSV"
    override val fileExtension: String = ".csv"
    override fun export(file: File) {
        TODO("Not yet implemented")
    }
}