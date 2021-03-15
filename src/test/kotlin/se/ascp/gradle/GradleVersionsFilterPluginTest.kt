package se.ascp.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class GradleVersionsFilterPluginTest{

    var qualifiers: List<String> = listOf("beta")
    @Test
    fun applyPlugin(){
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