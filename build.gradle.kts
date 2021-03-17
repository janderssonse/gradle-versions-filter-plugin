plugins {
    id("java-gradle-plugin")
    kotlin("jvm") version "1.4.31"
    id("maven-publish")
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.4" 
    id("org.owasp.dependencycheck") version "6.1.2"
    id("pl.allegro.tech.build.axion-release") version "1.12.1"
    id("com.gradle.plugin-publish") version "0.13.0"
}

//order matters for axion, configure this before project.version
scmVersion {
    tag.apply {
        prefix = "v"
        versionSeparator = ""
    }
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

pluginBundle {
    website = "https://github.com/jandersson-svt/gradle-versions-filter-plugin"
    vcsUrl = "https://github.com/jandersson-svt/gradle-versions-filter-plugin.git"
    tags = listOf("versions", "filter")
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "se.ascp.gradle.gradle-versions-filter"
            displayName = "Gradle Versions Filter Plugin"
            description = "Extension of Gradle Versions Plugin (discovering dependency updates) with opinionated defaults."
            implementationClass = "se.ascp.gradle.GradleVersionsFilterPlugin"
        }
    }
}

dependencies {
    implementation("com.github.ben-manes:gradle-versions-plugin:[0.38.0,1.0.0)")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}



tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "6.8.3"
}

