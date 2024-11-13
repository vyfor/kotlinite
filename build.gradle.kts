plugins {
    kotlin("jvm") version "2.0.20"
    application
}

group = "io.github.vyfor"
version = "0.0.1"

repositories {
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-ide-plugin-dependencies")
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-third-party-dependencies")
    maven("https://repo.gradle.org/gradle/libs-releases/")
    mavenCentral()
}

val ktAnalysisApiVersion = "2.1.0-dev-5441"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-compiler:$ktAnalysisApiVersion")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.23.1")
    implementation("org.gradle:gradle-tooling-api:8.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    listOf(
        "org.jetbrains.kotlin:high-level-api-fir-for-ide",
        "org.jetbrains.kotlin:analysis-api-platform-interface-for-ide",
        "org.jetbrains.kotlin:high-level-api-for-ide",
        "org.jetbrains.kotlin:low-level-api-fir-for-ide",
        "org.jetbrains.kotlin:symbol-light-classes-for-ide",
        "org.jetbrains.kotlin:analysis-api-standalone-for-ide",
        "org.jetbrains.kotlin:high-level-api-impl-base-for-ide",
        "org.jetbrains.kotlin:kotlin-compiler-common-for-ide",
        "org.jetbrains.kotlin:kotlin-compiler-fir-for-ide",
        "org.jetbrains.kotlin:kotlin-compiler-ir-for-ide"
    ).forEach {
        implementation("$it:$ktAnalysisApiVersion") { isTransitive = false }
    }
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("io.github.vyfor.kotlinite.KotliniteKt")
}