import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30" //stick to the supported Gradle plugin version https://docs.gradle.org/current/userguide/compatibility.html
    id("java-gradle-plugin")
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.9"
    id("org.owasp.dependencycheck") version "6.2.2"
    id("pl.allegro.tech.build.axion-release") version "1.13.3"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    id("se.svt.oss.gradle-yapp-publisher-plugin") version "0.1.13"
}

group = "se.ascp.gradle"
project.version = scmVersion.version

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
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
    implementation("com.github.ben-manes:gradle-versions-plugin:[0.39.0,1.0.0)")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "7.3.3"
}
