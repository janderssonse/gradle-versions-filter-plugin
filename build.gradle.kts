plugins {
    id("java-gradle-plugin")
    kotlin("jvm") version "1.4.31"
    id("maven-publish")
    id("com.github.ben-manes.versions") version "0.38.0"
    id("org.owasp.dependencycheck") version "6.1.2"
    id("pl.allegro.tech.build.axion-release") version "1.12.1"
}

group = "se.ascp.gradle"
project.version = scmVersion.version

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "se.ascp.gradle.gradle-versions-filter"
            implementationClass = "se.ascp.gradle.GradleVersionsFilterPlugin"
        }
    }
}

scmVersion {
    tag.apply {
        prefix = "v"
        versionSeparator = ""
    }
}

dependencies {
    implementation("com.github.ben-manes:gradle-versions-plugin:[0.38.0,1.0.0)")

    implementation("io.github.microutils:kotlin-logging:2.0.6")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}



tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "6.8.3"
}

