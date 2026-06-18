plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("info.solidsoft.pitest") version "1.19.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

testing {
    suites {
        val testIntegration by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testIntegration/kotlin"))
                }
                resources {
                    setSrcDirs(listOf("src/testIntegration/resources"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
            targets {
                all {
                    testTask.configure {
                        useJUnitPlatform()
                        environment("DOCKER_API_VERSION", "1.44")
                    }
                }
            }
        }
        val testComponent by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testComponent/kotlin"))
                }
                resources {
                    setSrcDirs(listOf("src/testComponent/resources"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
            targets {
                all {
                    testTask.configure {
                        useJUnitPlatform()
                        environment("DOCKER_API_VERSION", "1.44")
                    }
                }
            }
        }
        val testArchitecture by registering(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/testArchitecture/kotlin"))
                }
                compileClasspath += sourceSets.main.get().output
                runtimeClasspath += sourceSets.main.get().output
            }
            targets {
                all {
                    testTask.configure {
                        useJUnitPlatform()
                    }
                }
            }
        }
    }
}

val testIntegrationImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testComponentImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testArchitectureImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    testImplementation("io.mockk:mockk:1.13.10")

    testComponentImplementation("io.cucumber:cucumber-java:7.14.0")
    testComponentImplementation("io.cucumber:cucumber-spring:7.14.0")
    testComponentImplementation("io.cucumber:cucumber-junit:7.14.0")
    testComponentImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")
    testComponentImplementation("io.rest-assured:rest-assured:5.3.2")
    testComponentImplementation("org.junit.platform:junit-platform-suite:1.10.0")
    testComponentImplementation("org.testcontainers:postgresql:1.20.6")
    testComponentImplementation("org.testcontainers:testcontainers:1.20.6")
    testComponentImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testComponentImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }

    testArchitectureImplementation("com.tngtech.archunit:archunit-junit5:1.0.1")
    testArchitectureImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testArchitectureImplementation("io.kotest:kotest-runner-junit5:5.9.1")

    testIntegrationImplementation("io.mockk:mockk:1.13.8")
    testIntegrationImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testIntegrationImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testIntegrationImplementation("com.ninja-squad:springmockk:4.0.2")
    testIntegrationImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testIntegrationImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testIntegrationImplementation("org.testcontainers:postgresql:1.20.6")
    testIntegrationImplementation("org.testcontainers:jdbc:1.20.6")
    testIntegrationImplementation("org.testcontainers:testcontainers:1.20.6")
    testIntegrationImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
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

tasks.named<Test>("testIntegration") {
    environment("DOCKER_API_VERSION", "1.44")
    systemProperty("DOCKER_API_VERSION", "1.44")
    jvmArgs("-DDOCKER_API_VERSION=1.44")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test, tasks.named("testIntegration"))
    executionData.setFrom(
        fileTree(layout.buildDirectory) { include("jacoco/*.exec") }
    )
    reports {
        xml.required = true
        html.required = true
    }
}

detekt {
    config.setFrom("config/detekt.yml")
    buildUponDefaultConfig = true
}

configurations.matching { it.name.startsWith("detekt") }.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("2.0.10")
        }
    }
}

pitest {
    junit5PluginVersion = "1.2.1"
    targetClasses = setOf("org.example.your.package.*")
    targetTests = setOf("your.package.*")
    mutators = setOf("DEFAULTS")
    outputFormats = setOf("XML", "HTML")
    timestampedReports = false
}
