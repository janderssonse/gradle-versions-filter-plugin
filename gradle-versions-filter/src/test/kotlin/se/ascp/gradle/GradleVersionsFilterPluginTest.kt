// SPDX-FileCopyrightText: 2021 Josef Andersson
//
// SPDX-License-Identifier: Apache-2.0

package se.ascp.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class GradleVersionsFilterPluginTest {

    @Test
    fun applyPlugin() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(GradleVersionsFilterPlugin::class.java)

        assertNotNull(project.plugins.getPlugin(GradleVersionsFilterPlugin::class.java))
    }

    /* To-do: write some tests for testing includes,excludes etc
    @Test
    fun filterQualifiers(){
        assertFalse(qualifiers.excludes("version"))
    } */
}
