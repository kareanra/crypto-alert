import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

plugins {
    kotlin("jvm") version "1.4.21"
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "me.kyleareanraines"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.15.75"))
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.12.1"))
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.14.0"))

    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-java-sdk-ses:1.11.948")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")

    implementation("io.github.microutils:kotlin-logging:2.0.4")

    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    @kotlin.Suppress("DEPRECATION")
    mainClassName = "com.kareanra.crypto.AppRunner"
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
