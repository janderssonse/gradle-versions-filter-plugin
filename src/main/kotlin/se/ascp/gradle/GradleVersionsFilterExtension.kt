// SPDX-FileCopyrightText: 2021 Josef Andersson
//
// SPDX-License-Identifier: Apache-2.0

package se.ascp.gradle

open class GradleVersionsFilterExtension {
    var inclusiveQualifiers: List<String> = listOf()
    var exclusiveQualifiers: List<String> = listOf()
    var defaultInclusive: Boolean = false
    var strictSemVer: Boolean = true
    var log = false
}
