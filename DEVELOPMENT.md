# Development
1. Clone

```
$ git clone  
```

2. In the folder X, install the plugin to your local repository 

```
./gradlew publishToMavenLocal
``` 

3. In settings.gradle.kts, configure Gradle to look for local plugins (this has to be first in the file) 

```kotlin
pluginManagement {
repositories {
mavenLocal()
gradlePluginPortal()
// maven(url="https://dl.bintray.com/kotlin/dokka")
}
}
```

4. Apply the plugin in a project of your choice, this?

```kotlin

plugins {

        id("se.ascp.gradle:gradle-versions-filter") version "0.1.0-SNAPSHOT"
    
}


```

5. Configure it if you want to


```kotlin
versionsFilter {
    exclusiveQualifiers.addAll("rc", "alpha", "beta", "m")
}
```

## Run
```
./gradlew dependencyUpdates 
```
