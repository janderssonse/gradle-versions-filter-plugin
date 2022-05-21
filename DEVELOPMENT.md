# Development
1. Clone

```console
git clone  
```

2. In the folder X, install the plugin to your local repository

```console
./gradlew publishArtifactToLocalRepo
```

3. In settings.gradle.kts, configure Gradle to look for local plugins (this has to be first in the file)

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

4. Apply the plugin in a project of your choice, this?

```kotlin
plugins {
        id("se.ascp.gradle.gradle-versions-filter") version "x.y.z-SNAPSHOT"
}
```

5. Configure?

```kotlin
versionsFilter {
    exclusiveQualifiers.addAll("rc", "alpha", "beta", "m")
    log.set(true)
}
```

## Run
```console
./gradlew dependencyUpdates 
```
