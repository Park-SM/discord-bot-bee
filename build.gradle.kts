import java.net.URI

plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "com.smparkworld.discord"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        name = "m2-dv8tion"
        url = URI.create("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    implementation("net.dv8tion:JDA:5.1.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
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
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("com.smparkworld.discord.MainKt")
}
