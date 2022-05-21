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
import se.ascp.gradle.Strategy.EXCLUSIVE
import se.ascp.gradle.Strategy.INCLUSIVE

class GradleVersionsFilterPlugin : Plugin<Project> {

    // Why not Kotlinglogging etc? See https://discuss.gradle.org/t/logging-in-gradle-plugin/31685/2
    private val log: Logger = LoggerFactory.getLogger("GradleVersionsFilterPlugin") as Logger

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
        project.tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java).configure { task ->
            task.resolutionStrategy { strategy ->
                strategy.componentSelection { rules ->
                    rules.all {
                        val semVerOk = semVerOk(filterOptions, it)
                        if (!semVerOk) {
                            log("Skipping ${it.candidate.version} due to no semantic versioning", filterOptions)
                            it.reject("Release candidate")
                        }
                        if (semVerOk && rejectVersion(filterOptions, it.candidate.version)) {
                            log("Skipping ${it.candidate.version}", filterOptions)
                            it.reject("Release candidate")
                        }
                    }
                }
            }
            task.apply {
                gradleReleaseChannel = filterOptions.gradleReleaseChannel.get()
                outputDir = filterOptions.outputDir.get()
                reportfileName = filterOptions.reportFileName.get()
                checkForGradleUpdate = filterOptions.checkForGradleUpdate.get()
                checkConstraints = filterOptions.checkConstraints.get()
                checkBuildEnvironmentConstraints = filterOptions.checkBuildEnvironmentConstraints.get()
                outputFormatter = filterOptions.outPutFormatter.get()
            }
        }
    }

    private fun semVerOk(
        filterOptions: GradleVersionsFilterExtension,
        it: ComponentSelectionWithCurrent
    ): Boolean {
        return when (filterOptions.strictSemVer.get()) {
            true -> it.candidate.version.isSemVer()
            else -> true
        }
    }

    private fun rejectVersion(
        filterOptions: GradleVersionsFilterExtension,
        depVersion: String
    ): Boolean {

        log("DependencyVersion: $depVersion", filterOptions)

        return when (filterOptions.strategy.get()) {
            EXCLUSIVE -> {
                log("exclusiveQualifiers: ${filterOptions.exclusiveQualifiers.get().joinToString()}", filterOptions)
                filterOptions.exclusiveQualifiers.get().excludes(depVersion)
            }
            INCLUSIVE -> {
                log("inclusiveQualifiers: ${filterOptions.inclusiveQualifiers.get().joinToString()}", filterOptions)
                filterOptions.inclusiveQualifiers.get().includesNot(depVersion)
            }
            else -> {
                log("OR Strategy: ${filterOptions.inclusiveQualifiers.get().joinToString()}", filterOptions)
                log("OR Strategy: ${filterOptions.exclusiveQualifiers.get().joinToString()}", filterOptions)
                filterOptions.exclusiveQualifiers.get().excludes(depVersion)
                    .or(filterOptions.inclusiveQualifiers.get().includesNot(depVersion))
            }
        }
    }

    private fun log(message: String, filterOptions: GradleVersionsFilterExtension) {
        if (filterOptions.log.get()) {
            log.quiet(message)
        }
    }
}

fun String.isSemVer(): Boolean {
    // Using the official semver regex as found on https://semver.org/
    val semVerRegex =
        """^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)""".plus(
            """(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?${'$'}"""
        ).toRegex()
    return semVerRegex.matches(this)
}

fun List<String>.includesNot(version: String): Boolean {
    return this.any { version.uppercase().contains(it.uppercase()) }.not()
}

fun List<String>.excludes(version: String): Boolean {

    val result = this.filter { qualifier ->
        """(?i).*[.-]${qualifier.uppercase()}[.\d-]*""".toRegex().matches(version.uppercase())
    }
    return result.isNotEmpty()
}
