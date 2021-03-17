[![REUSE status](https://api.reuse.software/badge/github.com/fsfe/reuse-tool)](https://api.reuse.software/info/github.com/fsfe/reuse-tool) ![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/jandersson-svt/gradle-versions-filter-plugin)

# Gradle Versions Filter Plugin

A simple Gradle plugin, built on the [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) with sane no-configuration-defaults and customization options.


## Why?

The Gradle Version Plugin (which we love) leaves it up to the user to [configure and define](https://github.com/ben-manes/gradle-versions-plugin#revisions) what is a stable or an unstable version.

It might become tiresome to add an include or exclude policy to every project in a big codebase, so why not make a plugin out of it.
Besides that, to aggregate the configuration in the build scripts is better
(and last and least, It is fun to write a Gradle plugin with Kotlin :)

## How?

The plugin sets a sane default on what should be seen as the latest release. It either does this by having an inclusive (only include versions with these qualifiers) or the opposite, an exclusive policy.

It can also optionally be set to use only [SemVer-compatible](https://semver.org/) releases.

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

And that should be good enough for most people. For further options, see the [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) project, they should all be compatible.

Configurable Options:

Option               | Default                                    | Description
-------------------- | -----------------------------------------  | --------------
defaultInclusive     | false                                      | The default strategy, excludes as default i.e. use the exclusiveQualifiers, if true, use inclusive Qualifiers
inclusiveQualifiers  | "RELEASE","FINAL","GA"                     | The default inclusive qualifiers (if inclusive strategy is used) 
exclusiveQualifiers  | "alpha","beta","rc","cr","m","preview","b" | The default exclusive qualifiers (if exclusive strategy is used) 
strictSemVer         | false                                       | Only show strict SemVer-validated versions
log                  | false                                       | More verbose debug messages

.Example Configuration
```kotlin
versionsFilter {
    exclusiveQualifiers = ["rc", "alpha", "beta", "m"]
    defaultInclusive = true
}
```
## Example output


_Default Gradle Versions-Plugin output_ 

![](<./img/gradleversionsplugin.png>)


_Default Gradle Versions Filter-Plugin output for same project (notice it lists latest stable versions)_

![](<./img/gradleversionsfilterplugin.png>)


## Notes

This plugin should probably be deprecated at some point, if the options are given as a Pull Request (and accepted) to the original plugin.

## License

[Apache License 2.0](LICENSE)




