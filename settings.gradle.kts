pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "gradle-versions-filter-plugin"
include("gradle-versions-filter")

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
