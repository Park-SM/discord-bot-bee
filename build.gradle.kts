import java.net.URI

plugins {
    kotlin("jvm") version "1.8.21"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.smparkworld.discord"
version = "2.3.3"

repositories {
    mavenCentral()
    maven {
        name = "m2-dv8tion"
        url = URI.create("https://m2.dv8tion.net/releases")
    }
    maven {
        url = URI.create("https://maven.lavalink.dev/releases")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation("net.dv8tion:JDA:5.1.2")
    implementation("dev.arbjerg:lavaplayer:2.2.3")
    implementation("dev.lavalink.youtube:common:1.13.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveFileName.set("discord-bot-bee.jar")
    manifest {
        attributes["Main-Class"] = "com.smparkworld.discord.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("discord-bot-bee.jar")
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("com.smparkworld.discord.MainKt")
}
