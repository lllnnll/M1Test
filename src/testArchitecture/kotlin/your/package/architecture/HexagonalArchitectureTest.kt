package your.`package`.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import io.kotest.core.spec.style.FunSpec

class HexagonalArchitectureTest : FunSpec({
    val basePackage = "org.example.your.package"

    test("it should respect the hexagonal architecture concept") {
        val importedClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(basePackage)

        val rule = layeredArchitecture().consideringAllDependencies()
            .layer("domain").definedBy("$basePackage.domain..")
            .layer("driving").definedBy("$basePackage.infrastructure.driving..")
            .layer("driven").definedBy("$basePackage.infrastructure.driven..")
            .layer("application").definedBy("$basePackage.infrastructure.application..")
            .layer("Standard API").definedBy("java..", "kotlin..", "kotlinx..", "org.jetbrains.annotations..", "org.springframework..")
            .withOptionalLayers(true)
            .whereLayer("domain").mayOnlyAccessLayers("Standard API")
            .whereLayer("driving").mayNotBeAccessedByAnyLayer()
            .whereLayer("driven").mayOnlyBeAccessedByLayers("application")

        rule.check(importedClasses)
    }
})
