plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.telegram:telegrambots-longpolling:${libs.versions.telegramApi.get()}")
    implementation("org.telegram:telegrambots-client:${libs.versions.telegramApi.get()}")

    implementation("org.yaml:snakeyaml:${libs.versions.yaml.get()}")

    implementation("org.slf4j:slf4j-api:${libs.versions.slf4j.get()}")
    implementation("org.slf4j:slf4j-simple:${libs.versions.slf4j.get()}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${libs.versions.coroutine.get()}")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "org.fcsprepods.Application"
        )
    }
}

group = "org.fcsprepods"
version = "1.1.5"
var buildEnv = "release"

tasks.register("buildRelease") {
    group = "build"
    description = "Builds the Release version"
    doFirst {
        buildEnv = "release"
    }
    finalizedBy(tasks.shadowJar)
}

tasks.register("buildExperimental") {
    group = "build"
    description = "Builds the Experimental version"
    doFirst {
        buildEnv = "experimental"
    }
    finalizedBy(tasks.shadowJar)
}

tasks.register("buildStaging") {
    group = "build"
    description = "Builds the Staging version"
    doFirst {
        buildEnv = "staging"
    }
    finalizedBy(tasks.shadowJar)
}

tasks.shadowJar {
    archiveBaseName.set("suggestion-bot")
    archiveVersion.set("v$version")
    doFirst {
        archiveClassifier.set(buildEnv)
    }
    outputs.upToDateWhen { false }
}

kotlin {
    jvmToolchain(21)
}
