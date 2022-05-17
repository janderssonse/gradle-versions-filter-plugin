
plugins {
    kotlin("jvm") version "1.6.0" // stick to the supported Gradle plugin version https://docs.gradle.org/current/userguide/compatibility.html
    id("java-gradle-plugin")
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.13+"
    id("org.owasp.dependencycheck") version "7.1.0.1"
    id("pl.allegro.tech.build.axion-release") version "1.13.6"
    id("io.gitlab.arturbosch.detekt") version "1.20.0"
    id("se.svt.oss.gradle-yapp-publisher-plugin") version "0.1.16"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

group = "se.ascp.gradle"
project.version = scmVersion.version

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

tasks {
    test {
        useJUnitPlatform()
    }
}

dependencies {
    implementation("com.github.ben-manes:gradle-versions-plugin:[0.42.0,1.0.0)")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "7.4.2"
}
