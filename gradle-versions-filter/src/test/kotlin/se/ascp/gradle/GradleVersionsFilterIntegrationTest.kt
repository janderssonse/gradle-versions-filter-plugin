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
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GradleVersionsFilterIntegrationTest {

    private val tmpdir: String = System.getProperty("java.io.tmpdir")
    private fun installPluginDir() = "$tmpdir/testinstall/testplugin"
    private fun installLibDir() = "$tmpdir/testinstall/testlib"
    private fun repositoryDir() = "$tmpdir/m2tmp"

    @BeforeAll
    fun setup() {

        Files.createDirectories(Paths.get(repositoryDir()))
        Files.createDirectories(Paths.get(installPluginDir()))
        Files.createDirectories(Paths.get(installLibDir()))

        //install plugin under test
        copyProjectFilesToTestTmpDir(File("../"), installPluginDir())
        var pluginSettingsPath = Paths.get("${installPluginDir()}/settings.gradle.kts")
        var pluginBuildFilePath = Paths.get("${installPluginDir()}/gradle-versions-filter/build.gradle.kts")
        publishLocalRepo(
            pluginSettingsPath,
            pluginBuildFilePath,
            "${installPluginDir()}",
            pluginSettings(),
            pluginBuildFile("200.0.0")
        )

        //install test lib - this is mocked as a real dir, (guava) as the versions plugins needs a real published art
        copyProjectFilesToTestTmpDir(resource("a-java-lib"), installLibDir())
        val libSettingsPath = Paths.get("${installLibDir()}/settings.gradle.kts")
        val libBuildFilePath = Paths.get("${installLibDir()}/lib/build.gradle.kts")
        //setup a few versions
        publishLocalRepo(
            libSettingsPath,
            libBuildFilePath,
            installLibDir(),
            javalibSettings("""project(":lib").name="guava""""),
            javalibBuildfile("99.0.2-FINAL")
        )
        publishLocalRepo(
            libSettingsPath,
            libBuildFilePath,
            installLibDir(),
            javalibSettings("""project(":lib").name="guava""""),
            javalibBuildfile("99.0.5-RELEASE")
        )
        publishLocalRepo(
            libSettingsPath,
            libBuildFilePath,
            installLibDir(),
            javalibSettings("""project(":lib").name="guava""""),
            javalibBuildfile("99.0.1-rc")
        )
        publishLocalRepo(
            libSettingsPath,
            libBuildFilePath,
            installLibDir(),
            javalibSettings("""project(":lib").name="guava""""),
            javalibBuildfile("100.nosemver")
        )

        correctMavenMeta(File(repositoryDir()))
        libSettingsPath.toFile().writeText(javalibSettings())
    }

    private fun correctMavenMeta(path: File) {
        val projectFiles = path.walk().filter { file ->
            file.name.matches(
                Regex(
                    """maven-metadata-local.xml"""
                )
            )
        }
        projectFiles.forEach {
            println(it.absolutePath.toString())
        }

        projectFiles.forEach {
            it.copyTo(File("${it.parent}/maven-metadata.xml"), overwrite = true)
        }
    }

    private fun copyProjectFilesToTestTmpDir(path: File, target: String) {
        val projectFiles = path.listFiles { file ->
            !file.name.matches(
                Regex(
                    """build|.gradle|docs|gradle|gradle|.idea|LICENSES|.reuse|.git|DEVELOPMENT.md|LICENSE"""
                )
            )
        }
        /*fileArray.forEach {
            println(it.name.toString())
            println("${yappPluginTmpDir()}/${it.name}")
        }*/

        projectFiles.forEach {
            it.copyRecursively(File("$target/${it.name}"), overwrite = true)
        }
    }

    @Test
    fun `exclude release and final qualifiers`() {
        test(
            """exclusiveQualifiers.addAll("release", "final")
            | checkForGradleUpdate.set(false)
        """.trimMargin(), "99.0.1-rc"
        )
    }

    @Test
    fun `include final qualifier`() {
        test(
            """strategy.set(se.ascp.gradle.Strategy.INCLUSIVE)
            | inclusiveQualifiers.addAll("final")
            | checkForGradleUpdate.set(false)
            """.trimMargin(),
            "99.0.2-FINAL"
        )
    }

    @Test
    fun `includes allows RELEASE`() {
        test(
            """strategy.set(se.ascp.gradle.Strategy.INCLUSIVE)
            | checkForGradleUpdate.set(false)
        """.trimMargin(), "99.0.5-RELEASE"
        )
    }

    @Test
    fun `or strategy permutation test`() {

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("RELEASE")
            | exclusiveQualifiers.addAll("rc")
            | checkForGradleUpdate.set(false)
            """.trimMargin(),
            "99.0.5-RELEASE"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("RELEASE")
            | exclusiveQualifiers.addAll("")
            | checkForGradleUpdate.set(false)
            """.trimMargin(),
            "99.0.5-RELEASE"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("")
            | exclusiveQualifiers.addAll("rc")
            | checkForGradleUpdate.set(false)
            """.trimMargin(),
            "99.0.5-RELEASE"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("")
            | exclusiveQualifiers.addAll("RELEASE")
            | checkForGradleUpdate.set(false)
            """.trimMargin(),
            "99.0.2-FINAL"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("")
            | exclusiveQualifiers.addAll("")
            | checkForGradleUpdate.set(false)
            """.trimMargin(),
            "99.0.5-RELEASE"
        )

        test(
            """strategy.set(se.ascp.gradle.Strategy.OR)
            | inclusiveQualifiers.addAll("RELEASE","FINAL")
            | exclusiveQualifiers.addAll("RELEASE")
            | checkForGradleUpdate.set(false)
            """.trimMargin(),
            "99.0.2-FINAL"
        )
    }

    @Test
    fun `default excludes allows RELEASE`() {
        test(
            "", "99.0.5-RELEASE"

        )
        val a = ""
    }

    @Test
    fun `no strict semver allowed`() {
        test(
            "", "100.nosemver", false
        )
    }

    private fun publishLocalRepo(
        settingsPath: Path,
        buildfilePath: Path,
        projectDir: String,
        settings: String,
        buildFile: String
    ) {

        settingsPath.toFile().writeText(settings)
        buildfilePath.toFile().writeText(buildFile)

        GradleRunner.create()
            .withProjectDir(File(projectDir))
            .withArguments("-Dmaven.repo.local=${repositoryDir()}", "publishArtifactToLocalRepo")
            .withPluginClasspath()
            .forwardOutput()
            .build()
    }

    private fun test(filterOption: String, version: String, strictSemVer: Boolean = true) {


        val buildFilePath= Paths.get("${installLibDir()}/lib/build.gradle.kts")
        buildFilePath.toFile().writeText(javalibBuildfile(version))
        buildFilePath.toFile().appendText(
            """
            versionsFilter {
                    $filterOption
                    strictSemVer.set($strictSemVer)
                }
            """.trimIndent()
        )

        val result = GradleRunner.create()
            .withProjectDir(Paths.get("${installLibDir()}").toFile())
            .withEnvironment(mapOf("GRADLE_OPTS" to "-Dmaven.repo.local=${repositoryDir()}"))
            .withArguments("clean", "dependencyUpdates")
            .withPluginClasspath()
            .forwardOutput()
            .build()

        val resultText: String = """The following dependencies have later milestone versions:
        | - com.google.guava:guava [31.1.1-jre -> $version]
        """.trimMargin()
        assertTrue(result.output.contains(resultText))
        assertEquals(TaskOutcome.SUCCESS, result.task(":lib:dependencyUpdates")?.outcome)
    }

    private fun pluginBuildFile(version: String) =
        """
    

plugins {
    kotlin("jvm") version "1.6.0" // stick to the supported Gradle plugin version https://docs.gradle.org/current/userguide/compatibility.html
    id("java-gradle-plugin")
    id("se.svt.oss.gradle-yapp-publisher") version "0.1.18"
}

group = "se.ascp.gradle"
version = "$version"

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
}
tasks {
    test {
        useJUnitPlatform()
    }
}

yapp {
    targets.add("gradle_portal")
    gradlePortalPublishing.id.set("se.ascp.gradle.gradle-versions-filter")
    gradlePortalPublishing.implementationClass.set("se.ascp.gradle.GradleVersionsFilterPlugin")
}

dependencies {
    implementation("com.github.ben-manes:gradle-versions-plugin:[0.42.0,1.0.0)")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}
   


    """

    private fun javalibBuildfile(version: String) = """ 
    
    
plugins {
    `java-library`
    id("se.ascp.gradle.gradle-versions-filter") version "200.0.0"
    id("se.svt.oss.gradle-yapp-publisher") version "0.1.18"
}

group="com.google.guava"
version="$version"

repositories {
    // Use Maven Central for resolving dependencies.
    
    maven(url = "${repositoryDir()}")
    mavenCentral()
}

yapp {
    targets.add("maven_central")
}

dependencies {
    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api("org.apache.commons:commons-math3:3.6.1")
    
    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    api("com.google.guava:guava:31.1.1-jre")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.8.1")
        }
    }
}
    
    
""".trimIndent()


    private fun javalibSettings(libname: String = "") =
        """
            
pluginManagement {
    repositories {
        maven(url = "${repositoryDir()}")
        gradlePluginPortal()
    }
}
rootProject.name = "a-java-lib"
include("lib")
$libname 
            
        """.trimIndent()

    private fun pluginSettings() = """
        
pluginManagement {
    repositories {
        maven(url = "${repositoryDir()}")
        gradlePluginPortal()
    }
}

rootProject.name = "gradle-versions-filter-plugin"
include("gradle-versions-filter")

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}
        
        
    """.trimIndent()

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
