package cn.chitanda.app.imovie

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 *@author: Chen
 *@createTime: 2022/11/13 18:31
 *@description:
 **/
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*,*,*,*>
){
    commonExtension.apply {
        compileSdk = 33

        defaultConfig { minSdk = 23 }

        compileOptions{
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        kotlinOptions {
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsError :String? by project
            allWarningsAsErrors = warningsAsError.toBoolean()

            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
            )

            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
//        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
//        dependencies {
//            add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
//        }
    }
}

internal  fun CommonExtension<*, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}