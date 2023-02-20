@file:Suppress("UnstableApiUsage")

plugins {
    id("imovie.android.app")
    id("imovie.android.app.compose")
    id("imovie.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "cn.chitanda.app.imovie"

    defaultConfig {
        applicationId = "cn.chitanda.app.imovie"
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        val release by getting {
            isMinifyEnabled = true
            try {
                signingConfigs.getByName("chitanda")
            } catch (_: Throwable) {
                null
            }?.let {
                signingConfig = it
            }
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

}

dependencies {

    implementation(project(":core:design"))
    implementation(project(":core:ui"))
    implementation(project(":core:media"))
    implementation(project(":feature:home"))
    implementation(project(":feature:play"))

    androidTestImplementation(libs.androidx.navigation.testing)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.manager)
    implementation(libs.androidx.profileinstaller)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.svg)
    implementation(libs.chitanda.dynamicstatusbar)

}