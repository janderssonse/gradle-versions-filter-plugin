// SPDX-FileCopyrightText: 2021 Josef Andersson
//
// SPDX-License-Identifier: Apache-2.0

package se.ascp.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class GradleVersionsFilterExtension @Inject constructor(objects: ObjectFactory) {

    var inclusiveQualifiers: ListProperty<String> = objects.listProperty(String::class.java).convention(
        listOf(
            "RELEASE",
            "FINAL",
            "GA"
        )
    )
    var exclusiveQualifiers: ListProperty<String> = objects.listProperty(String::class.java).convention(
        listOf(
            "alpha",
            "beta",
            "rc",
            "cr",
            "m",
            "preview",
            "b"
        )
    )
    var strategy: Property<Strategy> = objects.property(Strategy::class.java).convention(Strategy.EXCLUSIVE)
    var strictSemVer: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    var log: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    // DependencyUpdateTask options
    var revision: Property<String> = objects.property(String::class.java).convention("")
    var gradleReleaseChannel: Property<String> = objects.property(String::class.java).convention("release-candidate")
    var outputDir: Property<String> = objects.property(String::class.java).convention("build/dependencyUpdates")
    var reportFileName: Property<String> = objects.property(String::class.java).convention("report")
    var checkForGradleUpdate: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    var checkConstraints: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    var checkBuildEnvironmentConstraints: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    var outPutFormatter: Property<String> = objects.property(String::class.java).convention("plain")
}
