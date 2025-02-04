plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    kotlin("jvm")
}

group = "org.fcsprepods"
version = "1.0.4-release"

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
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "org.fcsprepods.Main"
        )
    }
}

project.tasks.build {
    dependsOn(tasks.shadowJar)
}
kotlin {
    jvmToolchain(21)
}