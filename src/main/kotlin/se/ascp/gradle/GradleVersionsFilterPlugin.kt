// SPDX-FileCopyrightText: 2021 Josef Andersson
//
// SPDX-License-Identifier: Apache-2.0

package se.ascp.gradle

import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ComponentSelectionWithCurrent
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.slf4j.LoggerFactory

class GradleVersionsFilterPlugin : Plugin<Project> {

    //Funny logging - why not Kotlinglogging etc? See https://discuss.gradle.org/t/logging-in-gradle-plugin/31685/2
    val log: Logger = LoggerFactory.getLogger("GradleVersionsFilterPlugin") as Logger

    override fun apply(project: Project) {

        val pluginConfiguration = pluginConfiguration(project)
        applyVersionsPlugin(project)
        configureResolutionStrategy(project, pluginConfiguration)
    }

    private fun pluginConfiguration(project: Project) =
        project.extensions.create("versionsFilter", GradleVersionsFilterExtension::class.java)

    private fun applyVersionsPlugin(project: Project) =
        project.plugins.apply(VersionsPlugin::class.java)

    private fun configureResolutionStrategy(
        project: Project,
        filterOptions: GradleVersionsFilterExtension
    ) {
        project.tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java).configure {
            it.resolutionStrategy {
                it.componentSelection {
                    it.all {
                        val semVerOk = semVerOk(filterOptions, it)
                        if (!semVerOk) {
                            log.quiet("Skipping ${it.candidate.version} due to no semantic versioning")
                            it.reject("Release candidate")
                        }
                        if (semVerOk && rejectVersion(filterOptions, it.candidate.version)) {
                            log.quiet("Skipping ${it.candidate.version}")
                            it.reject("Release candidate")
                        }
                    }
                }
            }
        }
    }

    private fun semVerOk(
        filterOptions: GradleVersionsFilterExtension,
        it: ComponentSelectionWithCurrent
    ): Boolean {
        return when (filterOptions.strictSemVer) {
            true -> it.candidate.version.isSemVer()
            else -> true
        }
    }

    private fun rejectVersion(
        filterOptions: GradleVersionsFilterExtension,
        depVersion: String
    ): Boolean {

        return when {
            filterOptions.exclusiveQualifiers.isNotEmpty() -> {
                log.quiet("exludesfilter: ${filterOptions.exclusiveQualifiers}")
                filterOptions.exclusiveQualifiers.excludes(depVersion)

            }
            filterOptions.inclusiveQualifiers.isNotEmpty() -> {

                log.quiet("depversion ${depVersion}")
                log.quiet("includesfilter ${filterOptions.inclusiveQualifiers}")
                filterOptions.inclusiveQualifiers.includesNot(depVersion)
            }
            else -> {

                log.quiet("ELSE default")
                if (filterOptions.defaultInclusive) {
                    val includes = listOf(
                        "RELEASE",
                        "FINAL",
                        "GA"
                    )

                    log.quiet("DEFAULT INCLUDES")
                    includes.includesNot(depVersion)
                } else {
                    val excludes = listOf(
                        "alpha",
                        "beta",
                        "rc",
                        "cr",
                        "m",
                        "preview",
                        "b"
                    )

                    log.quiet("DEFAULT EXCLUDES")
                    excludes.excludes(depVersion)
                }
            }
        }
    }
}

fun String.isSemVer(): Boolean {
    //Using the official semver regex as found on https://semver.org/
    val semVerRegex =
        """^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?${'$'}""".toRegex()
    return semVerRegex.matches(this)
}

fun List<String>.includesNot(version: String): Boolean {
    return this.any { version.toUpperCase().contains(it.toUpperCase()) }.not()
}

fun List<String>.excludes(version: String): Boolean {

    val result = this.filter { qualifier ->
        """(?i).*[.-]${qualifier.toUpperCase()}[.\d-]*""".toRegex().matches(version.toUpperCase())
    }
    return result.isNotEmpty()
}
