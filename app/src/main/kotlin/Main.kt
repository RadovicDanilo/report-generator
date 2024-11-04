import src.main.kotlin.ReportExporter
import java.util.ServiceLoader

fun main(args: Array<String>) {
    val serviceLoader = ServiceLoader.load(ReportExporter::class.java)

    println("Registered services\n")
    serviceLoader.forEach { pushService ->
        println("   exporter type: ${pushService.exporterType}")
        println("   exporter file extensions: ${pushService.fileExtension}\n")
    }

}
