import org.gradle.kotlin.dsl.invoke

plugins {
    kotlin("jvm")
    `java-library`

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":exporter"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}