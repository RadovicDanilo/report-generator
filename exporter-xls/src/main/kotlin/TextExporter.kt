import src.main.kotlin.ReportExporter
import src.main.kotlin.file.File

class TextExporter() : ReportExporter() {
    override val exporterType: String = "XLS"
    override fun export(file: File) {
        TODO("Not yet implemented")
    }
}