package src.main.kotlin

import src.main.kotlin.file.File

abstract class ReportExporter {
    abstract val exporterType: String
    abstract fun export(file: File)
}