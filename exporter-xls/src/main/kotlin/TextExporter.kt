import src.main.kotlin.FormatReportExported
import src.main.kotlin.ReportExporter
import src.main.kotlin.file.File
import src.main.kotlin.file.FormatFile

class TextExporter() : FormatReportExported() {
    override val exporterType: String = "XLS"
    override val fileExtension: String = ".xls"
    override fun export(file: File) {
        TODO("Not yet implemented")
    }

    override fun export(file: FormatFile) {
        TODO("Not yet implemented")
    }
}