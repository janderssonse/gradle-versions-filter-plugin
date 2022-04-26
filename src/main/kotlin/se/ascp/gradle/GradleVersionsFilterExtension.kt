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
}
