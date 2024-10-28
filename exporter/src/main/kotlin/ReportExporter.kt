package src.main.kotlin

import src.main.kotlin.file.File
import src.main.kotlin.file.FileBuilder

abstract class ReportExporter(
    val exporterType: String,
) {
    abstract fun export(fileBuilder: FileBuilder, fileName: String)
    abstract fun export(fileBuilder: FileBuilder)
    abstract fun export(file: File)
}