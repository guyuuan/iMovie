@file:Suppress("UnstableApiUsage")

import cn.chitanda.app.imovie.configureKotlinAndroid
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File
import java.util.Properties

/**
 *@author: Chen
 *@createTime: 2022/11/13 19:29
 *@description:
 **/
class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<ApplicationExtension> {

                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 33
                defaultConfig.versionName = try {
                    System.getenv("APP_VERSION_NAME")
                } catch (_: Throwable) {
                    "0.0.1"
                }
//                configureFlavors(this)
                val propertiesFile = file("${project.rootProject.projectDir}/local.properties")
                if (propertiesFile.exists()) {
                    signingConfigs.create("chitanda") {
                        val properties = Properties().apply {
                            load(propertiesFile.inputStream())
                        }
                        storeFile =
                            File("${project.rootProject.projectDir.absolutePath}/build-logic/chitanda")
                        storePassword = properties.getProperty("sign.store.pwd")!!
                        keyAlias = properties.getProperty("sign.key.alias")!!
                        keyPassword = properties.getProperty("sign.key.pwd")!!
                    }
                }
            }

        }
    }
}