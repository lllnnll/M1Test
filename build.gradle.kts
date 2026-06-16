plugins {
    kotlin("jvm") version "2.3.21"
    jacoco
    id("info.solidsoft.pitest") version "1.19.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    testImplementation("io.mockk:mockk:1.13.10")
}

kotlin {
    jvmToolchain(21)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

pitest {
    junit5PluginVersion = "1.2.1"
    targetClasses = setOf("org.example.your.`package`.*")
    targetTests = setOf("your.`package`.*")
    mutators = setOf("DEFAULTS")
    outputFormats = setOf("XML", "HTML")
    timestampedReports = false
}