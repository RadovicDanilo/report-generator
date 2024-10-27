package src.main.kotlin

abstract class ReportExporter {
    abstract val exporterType: String
    abstract val filename: String
    abstract fun save(data: List<List<String>>)
    abstract fun save(data: List<List<String>>, header: List<String>)

}