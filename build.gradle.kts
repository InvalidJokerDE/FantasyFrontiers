import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    kotlin("jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "1.9.10"
    id("org.jetbrains.dokka") version "1.9.10"
}

group = "net.fantasyfrontiers"
version = "1.0-SNAPSHOT"

val exposedVersion: String by project

repositories {
    mavenCentral()
    maven("https://repo.flawcra.cc/mirrors")
}

val shadowDependencies = listOf(
    "net.dv8tion:JDA:5.0.0-beta.15",

    // Utilities
    "net.oneandone.reflections8:reflections8:0.11.7",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3",
    "org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.6",
    "com.google.code.gson:gson:2.10.1",
    "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0",
    "com.github.TheFruxz:Ascend:2023.3",
    "io.github.cdimascio:dotenv-kotlin:6.4.1",

    // Database
    "org.jetbrains.exposed:exposed-core:$exposedVersion",
    "org.jetbrains.exposed:exposed-dao:$exposedVersion",
    "org.jetbrains.exposed:exposed-jdbc:$exposedVersion",
    "org.jetbrains.exposed:exposed-java-time:$exposedVersion",
    "com.mysql:mysql-connector-j:8.1.0",
    "com.zaxxer:HikariCP:5.0.1",

    // Sentry
    "io.sentry:sentry:6.31.0",
    "io.sentry:sentry-kotlin-extensions:6.31.0",


)

dependencies {
    testImplementation(kotlin("test"))

    shadowDependencies.forEach { dependency ->
        implementation(dependency)
        shadow(dependency)
    }
}

tasks {

    test {
        useJUnitPlatform()
    }

    build {
        dependsOn("shadowJar")
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    withType<ShadowJar> {
        mergeServiceFiles()
        configurations = listOf(project.configurations.shadow.get())
        archiveFileName.set("${project.name}.jar")
    }

    withType<DokkaTask>().configureEach {
        moduleName.set(project.name)
        moduleVersion.set(project.version.toString())

        dokkaSourceSets.configureEach {
            displayName.set(name)
            jdkVersion.set(17)
            languageVersion.set("17")
            apiVersion.set("17")

            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl.set(URL("https://github.com/FantasyFrontiers/FantasyFrontiers/tree/main/src"))
                remoteLineSuffix.set("#L")
            }
        }
    }

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}