# Development
1. Clone

```
$ git clone  
```

2. In the folder X, install the plugin to your local repository 

```
./gradlew publishToMavenLocal
``` 

3. Apply the plugin in a project of your choice, this?

```kotlin
buildscript {
    repositories {
        mavenLocal()
    }


    dependencies {
        classpath("se.ascp.gradle:gradle-versions-filter:0.1.0-SNAPSHOT")
    }
}

apply(plugin = "se.ascp.gradle.gradle-versions-filter")

```

Configure

```kotlin
versionsFilter {
    exclusiveQualifiers = ["rc", "alpha", "beta", "m"]
    defaultInclusive = true
}
```

## Run
```
./gradlew dependencyUpdates 
```
