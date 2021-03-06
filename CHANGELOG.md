<!-- markdownlint-disable MD024 -->
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

## 0.1.15 - 2022-05-19

### Added
- Refactor structure for better following recommended Gradle structure conventions

### Changed
- Updated dependencies for publishing.

## 0.1.14 - 2022-05-17

### Added
- Add linting CI for better project hygiene.
- Hygiene FOSS files added.

### Changed
- Updated dependencies for publishing.

## 0.1.13 - 2022-04-26

### Added
- Forwarding of most Versions plugin configuration options

## 0.1.12 - 2022-04-26

### Added
- Strategy concept: inclusive, exclusive, or (considers both sets)
- Lazy configuration of properties

### Changed
- Breaking: Older configurations that did not use defaults have to be updated, see README examples.

## 0.1.11 - 2022-04-25

### Fixed
- Update dependencies (preparation for issue fixing)

## 0.1.10 - 2021-08-09

### Deprecated
- The Git history - it was rebased

### Fixed
- Use version 0.1.9 of the plugin

## 0.1.9 - 2021-08-09

### Added
- Project REBASED - check out a fresh clone, or git pull --rebase
- Added this CHANGELOG.md, to improve the projects description of changes
- From now on, adhere to the [Conventional Commit](https://www.conventionalcommits.org/en/v1.0.0/) specification
- Set the Java target to version 11 which is the latest LTS
- Add badge for Conventional Commits, Apache License 2.0

### Deprecated
- Java 8 target

### Removed
- Nothing.

### Fixed
- Updated README.md configuration example
- Updated third party dependencies, Gradle 7.1.1. VersionsPlugin 0.39
- Use a Gradle version that is on the compatibility matrix

