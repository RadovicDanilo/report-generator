plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.8.10"
    `java-library`

}

group = "com.radovicdanilo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":calculations"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    dependsOn(tasks.dokkaJavadoc)
    doLast {
        println("Javadoc task completed with Dokka output.")
    }
}

tasks.dokkaJavadoc {
    outputDirectory.set(file("build/dokka/javadoc")) // Set the output directory
}

kotlin {
    jvmToolchain(17)
}