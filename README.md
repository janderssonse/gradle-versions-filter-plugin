# Gradle Versions Filter Plugin

![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/janderssonse/gradle-versions-filter-plugin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![REUSE status](https://api.reuse.software/badge/github.com/janderssonse/gradle-versions-filter-plugin)](https://api.reuse.software/info/github.com/janderssonse/gradle-versions-filter-plugin)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)

A simple Gradle plugin, built on the [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) with sane no-configuration-defaults and customization options.


## Why?

The Gradle Version Plugin (which we love) leaves it up to the user to [configure and define](https://github.com/ben-manes/gradle-versions-plugin#revisions) what is a stable or an unstable version.

It might become tiresome to add an include or exclude policy to every project in a large codebase - so why not make a plugin out of it.

## How?

The plugin sets a default on what should be seen as the latest release.
It either does this by having an inclusive or an exclusive policy.

It can also be configured to only use [SemVer-compatible](https://semver.org/) releases.

## Usage

Add the plugin to your build.gradle.kts (or build.gradle)

```kotlin
plugins {
   id("se.ascp.gradle.gradle-versions-filter") version "x.y.z"
}
```

Run

```shell
./gradlew dependencyUpdates
```

And - that should be good enough for most people. For further options, see the [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) project, they *should* all be compatible.
Add an issue if not.


Configurable Options:

| Option              | Default                                    | Description                                      |
|---------------------|--------------------------------------------|--------------------------------------------------|
| inclusiveQualifiers | "RELEASE","FINAL","GA"                     | default inclusive mode qualifiers                |
| exclusiveQualifiers | "alpha","beta","rc","cr","m","preview","b" | default exclusive mode qualifiers                |
| strategy            | EXCLUSIVE                                  | default strategy mode - EXCLUSIVE, INCLUSIVE, OR |
| strictSemVer        | false                                      | only show strict SemVer-versions                 |
| log                 | false                                      | verbose debug messages                           |

In other words, if you do not configure anything, exclude strategy would be used and versions containing "alpha","beta","rc","cr","m","preview","b" would not be considered.

Note: Option values are not case-sensitive

( And most of the options you can set in the original Versions plugin can also be set, as follows, if you really need to. They are forwarded.)
But, if you need to start configuring a lot, maybe the original plugin is the way to go).

| Option                           | Default                 | Description                             |
|----------------------------------|-------------------------|-----------------------------------------|
| gradleReleaseChannel             | release-candidate       | See the Versions Plugin for description |
| reportfileName                   | report                  | See the Versions Plugin for description |
| outputDir                        | build/dependencyUpdates | See the Versions Plugin for description |
| checkForGradleUpdate             | true                    | See the Versions Plugin for description |
| checkConstraints                 | false                   | See the Versions Plugin for description |
| checkBuildEnvironmentConstraints | false                   | See the Versions Plugin for description |
| outputFormatter                  | plain                   | See the Versions Plugin for description |

## Example configuration


### Configuration examples

#### Overriding defaults, this would show debug output and does NOT exclude "alpha" releases
build.gradle.kts
```kotlin
versionsFilter {
    exclusiveQualifiers.addAll("beta","rc","cr","m","preview","b" )
    log.set(true)
}
```

#### Overriding defaults, this uses inclusive strategy, and does only consider "FINAL" releases
```kotlin
versionsFilter {
    strategy.set(se.ascp.gradle.Strategy.INCLUSIVE)
    inclusiveQualifiers.addAll("FINAL")
    checkForGradleUpdate.set(false)
    
}
```

## Example output


*Default Gradle Versions-Plugin output*

![Example of default Gradle versions output](<./img/gradleversionsplugin.png>)


*Default Gradle Versions Filter-Plugin output for same project (notice it lists latest stable versions)*

![Example of this plugins output](<./img/gradleversionsfilterplugin.png>)


## Notes

This plugin should probably be deprecated at some point, if the options are given as a Pull Request (and accepted) to the Gradle Versions plugin.

## License

This project is released under the

[Apache License 2.0](LICENSE)




