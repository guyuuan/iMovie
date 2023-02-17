import cn.chitanda.app.imovie.configureFlavors
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
                val propertis =
                    Properties().apply {
                        load(file("${project.rootProject.projectDir}/local.properties").inputStream())
                    }
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 33
//                configureFlavors(this)
                signingConfigs.create("chitanda") {
                    storeFile =
                        File("${project.rootProject.projectDir.absolutePath}/build-logic/chitanda")
                    storePassword = propertis.getProperty("sign.store.pwd")
                    keyAlias = propertis.getProperty("sign.key.alias")
                    keyPassword = propertis.getProperty("sign.key.pwd")
                }
            }

        }
    }
}