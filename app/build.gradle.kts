import org.gradle.kotlin.dsl.invoke

plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.radovicdanilo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly("com.mysql:mysql-connector-j:8.4.0")
    implementation(project(":calculations"))
    implementation(project(":exporter"))
    runtimeOnly(project(":exporter-txt"))
    runtimeOnly(project(":exporter-pdf"))
    runtimeOnly(project(":exporter-csv"))
    runtimeOnly(project(":exporter-xls"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// ovaj ne pakuje dobro servise
/*
tasks.jar {
    manifest {
        attributes["Main-Class"] = "main.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
*/

application {
    mainClass.set("Main")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveFileName.set("app.jar") // Optional: Set the desired jar name
        mergeServiceFiles() // Merge META-INF/services

        manifest {
            attributes["Main-Class"] = "maine.MainKt" // Replace with your main class
        }
    }
}