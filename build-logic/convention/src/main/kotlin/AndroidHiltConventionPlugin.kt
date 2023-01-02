@file:Suppress("UnstableApiUsage")

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 *@author: Chen
 *@createTime: 2022/11/13 19:39
 *@description:
 **/
class AndroidHiltConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            with(pluginManager){
                apply("org.jetbrains.kotlin.kapt")
                apply("dagger.hilt.android.plugin")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies{
               "implementation"(libs.findLibrary("hilt.android").get())
               "kapt"(libs.findLibrary("hilt.compiler").get())
               "kaptAndroidTest"(libs.findLibrary("hilt.compiler").get())
            }
        }
    }

}