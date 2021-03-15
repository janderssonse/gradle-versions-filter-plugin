package se.ascp.gradle

open class GradleVersionsFilterExtension {
    var inclusiveQualifiers: List<String> = listOf()
    var exclusiveQualifiers: List<String> = listOf()
    var defaultInclusive: Boolean = false
    var strictSemVer: Boolean = true
}