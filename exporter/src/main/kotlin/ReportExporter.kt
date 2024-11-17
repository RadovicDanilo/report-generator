package src.main.kotlin

import src.main.kotlin.file.File
import src.main.kotlin.file.FileBuilder

/**
 * An abstract class for a non-formatted report exporter.
 *
 * @property exporterType The type of the file exporter.
 * @property fileExtension The extension of the exported report file.
 */
abstract class ReportExporter {
    /**
     * Specifies the type of the exporter.
     */
    abstract val exporterType: String

    /**
     * Defines the extension of the exported report file.
     */
    abstract val fileExtension: String

    /**
     * Exports a non-formatted report based on the provided [file].
     *
     * @param file The file that will be exported.
     */
    abstract fun export(file: FileBuilder)
}
