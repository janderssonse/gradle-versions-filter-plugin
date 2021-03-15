[![REUSE status](https://api.reuse.software/badge/github.com/fsfe/reuse-tool)](https://api.reuse.software/info/github.com/fsfe/reuse-tool) ![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/jandersson-svt/gradle-versions-filter-plugin)

# Gradle Versions Filter Plugin

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
$ ./gradlew dependencyUpdates
```

And - that should be good enough for most people. For further options, see the [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) project, they *should* all be compatible.
Add an issue if not.


Configurable Options:

Option               | Default                                    | Description
-------------------- | -----------------------------------------  | --------------
defaultInclusive     | false                                      | default strategy mode - inclusive (true) or exclusive (false)
inclusiveQualifiers  | "RELEASE","FINAL","GA"                     | default inclusive mode qualifiers 
exclusiveQualifiers  | "alpha","beta","rc","cr","m","preview","b" | default exclusive mode qualifiers 
strictSemVer         | false                                       | only show strict SemVer-versions
log                  | false                                       | verbose debug messages

.Example Configuration
```kotlin
versionsFilter {
    exclusiveQualifiers = ["rc", "alpha", "beta", "m"]
}
```
## Example output


_Default Gradle Versions-Plugin output_ 

![](<./img/gradleversionsplugin.png>)


_Default Gradle Versions Filter-Plugin output for same project (notice it lists latest stable versions)_

![](<./img/gradleversionsfilterplugin.png>)


## Notes

This plugin should probably be deprecated at some point, if the options are given as a Pull Request (and accepted) to the Gradle Versions plugin.

## License

This project is released under the 

[Apache License 2.0](LICENSE)




