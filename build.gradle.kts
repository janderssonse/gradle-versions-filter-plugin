plugins {
    kotlin("jvm") version "1.4.32"
    id("java-gradle-plugin")
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.6"
    id("org.owasp.dependencycheck") version "6.1.6"
    id("pl.allegro.tech.build.axion-release") version "1.13.2"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    id("se.svt.oss.gradle-yapp-publisher-plugin") version "0.1.12"
}

group = "se.ascp.gradle"
project.version = scmVersion.version

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

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
    implementation("com.github.ben-manes:gradle-versions-plugin:[0.38.0,1.0.0)")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "7.0.2"
}