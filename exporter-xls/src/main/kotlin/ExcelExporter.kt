import src.main.kotlin.FormatReportExported
import src.main.kotlin.file.File
import src.main.kotlin.file.FormatFile

class ExcelExporter() : FormatReportExported() {
    override val exporterType: String = "XLS"
    override val fileExtension: String = ".xls"

    override fun export(file: File) {
        TODO("Not yet implemented")
    }

    override fun exportFormated(file: FormatFile) {
        TODO("Not yet implemented")
    }
}