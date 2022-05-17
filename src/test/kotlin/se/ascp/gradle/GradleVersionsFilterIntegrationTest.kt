// SPDX-FileCopyrightText: 2021 Josef Andersson
//
// SPDX-License-Identifier: Apache-2.0

package se.ascp.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GradleVersionsFilterIntegrationTest {

    private lateinit var tmpProjectSettingsFile: Path
    private lateinit var tmpProjectBuildFile: Path
    private val tmpdir: String = System.getProperty("java.io.tmpdir")

    @BeforeAll
    fun setup() {

        tmpProjectSettingsFile = testProjectDir.resolve("settings.gradle.kts")
        tmpProjectBuildFile = testProjectDir.resolve("build.gradle.kts")

        publishLocalRepo("0.0.1")
        publishLocalRepo("99.0.2-FINAL")
        publishLocalRepo("99.0.5-RELEASE")
        publishLocalRepo("99.0.1-rc")
        publishLocalRepo("100.nosemver")
    }

    @Test
    fun `exclude release and final qualifiers`() {
        test("""exclusiveQualifiers.addAll("release", "final")""", "99.0.1-rc")
    }

    @Test
    fun `include final qualifier`() {
        test(
            """strategy.set(INCLUSIVE)
            | inclusiveQualifiers.addAll("final")
            """.trimMargin(),
            "99.0.2-FINAL"
        )
    }

    @Test
    fun `includes allows RELEASE`() {
        test("""strategy.set(INCLUSIVE)""", "99.0.5-RELEASE")
    }

    @Test
    fun `or strategy permutation test`() {

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("RELEASE")
            | exclusiveQualifiers.addAll("rc")
            """.trimMargin(),
            "99.0.5-RELEASE"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("RELEASE")
            | exclusiveQualifiers.addAll("")
            """.trimMargin(),
            "99.0.5-RELEASE"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("")
            | exclusiveQualifiers.addAll("rc")
            """.trimMargin(),
            "99.0.5-RELEASE"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("")
            | exclusiveQualifiers.addAll("RELEASE")
            """.trimMargin(),
            "99.0.2-FINAL"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("")
            | exclusiveQualifiers.addAll("")
            """.trimMargin(),
            "99.0.5-RELEASE"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("RELEASE","FINAL")
            | exclusiveQualifiers.addAll("RELEASE")
            """.trimMargin(),
            "99.0.2-FINAL"
        )
    }

    @Test
    fun `default excludes allows RELEASE`() {
        test("", "99.0.5-RELEASE")
    }

    @Test
    fun `no strict semver allowed`() {
        test("", "100.nosemver", false)
    }

    private fun publishLocalRepo(version: String) {

        val settings = "settings.gradle.test"
        val settingsFile = resource(settings)

        Files.copy(settingsFile.toPath(), tmpProjectSettingsFile, StandardCopyOption.REPLACE_EXISTING)
        tmpProjectBuildFile.toFile().writeText(buildtestFile(version))

        GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withArguments("-Dmaven.repo.local=$tmpdir", "publishToMavenLocal")
            .withPluginClasspath()
            .forwardOutput()
            .build()
    }

    private fun test(filterOption: String, version: String, strictSemVer: Boolean = true) {

        val settings = "settings.gradle.test"
        val build = "build.gradle.test"

        Files.copy(resource(settings).toPath(), tmpProjectSettingsFile, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(resource(build).toPath(), tmpProjectBuildFile, StandardCopyOption.REPLACE_EXISTING)

        tmpProjectBuildFile.toFile().appendText(
            """
            versionsFilter {
                    $filterOption
                    strictSemVer.set($strictSemVer)
                }
            """.trimIndent()
        )

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withArguments("-Dmaven.repo.local=$tmpdir", "dependencyUpdates")
            .withPluginClasspath()
            .forwardOutput()
            .build()

        val resultText: String = """The following dependencies have later milestone versions:
        | - se.ascp.gradle:gradle-versions-filter-test [0.0.1 -> $version]
        """.trimMargin()
        assertTrue(result.output.contains(resultText))
        assertEquals(TaskOutcome.SUCCESS, result.task(":dependencyUpdates")?.outcome)
    }

    private fun buildtestFile(version: String) =
        """
    
    plugins {
        id("java-gradle-plugin")
        id("maven-publish")
    }

    repositories {
        maven {
            name = "mavenLocal"
            url = file("$tmpdir").toURI() 
        } 
    }

    group = "se.ascp.gradle"
    version = "$version"

    gradlePlugin {
        plugins {
            create("simplePlugin") {
                id = "se.ascp.gradle"
                implementationClass = "se.ascp.gradle.GradleVersionsFilterPlugin"
            }
        }
    }
    """

    private fun resource(resource: String): File = File(javaClass.classLoader.getResource(resource).file)

    // @AfterAll
    // private fun unpublish() {
    // gradle unpublishToMavenLocal -- is there anything like without doing it manually?
    // In maven one can do:
    // mvn dependency:purge-local-repository -DreResolve=false
    // to remove a projects dependencies from local repo
    // However, as the local repo is set to the tmp dir, this does not really matter
    // }

    companion object {
        @JvmStatic
        @TempDir
        lateinit var testProjectDir: Path
    }
}
