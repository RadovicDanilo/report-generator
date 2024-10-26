plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "softverske-komponente-1"
include("exporter")
include("exporter-txt")
include("exporter-pdf")
include("exporter-csv")
include("exporter-xls")
include("app")
