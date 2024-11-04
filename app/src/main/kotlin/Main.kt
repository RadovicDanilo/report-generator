import src.main.kotlin.ReportExporter
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.ServiceLoader

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        throw IllegalArgumentException("Connection string required")
    }

    val connectionString = args[0]

    try {
        val connection: Connection = DriverManager.getConnection(connectionString)
        println("Connected to the database successfully!")
    } catch (e: SQLException) {
        e.printStackTrace()
        println("Failed to connect to the database.")
    }

    val serviceLoader = ServiceLoader.load(ReportExporter::class.java)

    println("Registered services\n")
    val exporters = serviceLoader.map { exporter -> exporter }


}
