package src.main.kotlin

import src.main.kotlin.file.FormatFile
import src.main.kotlin.file.FormatFileBuilder

/**
 * An abstract class for a formatted report exporter.
 *
 * This class extends [ReportExporter] and adds functionality for exporting formatted reports.
 */
abstract class FormatReportExported : ReportExporter() {

    /**
     * Exports a formatted report based on the provided [file].
     *
     * @param file The formatted file that will be exported.
     */
    abstract fun exportFormated(file: FormatFileBuilder)
}
