package src.main.kotlin

import src.main.kotlin.file.FormatFile

abstract class FormatReportExported : ReportExporter() {
    abstract fun export(file: FormatFile)
}