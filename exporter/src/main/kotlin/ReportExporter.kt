package src.main.kotlin

import src.main.kotlin.file.FileBuilder

abstract class ReportExporter(
    val exporterType: String,
) {
    abstract fun run(fileBuilder: FileBuilder)
}