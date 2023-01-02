plugins {
    id("imovie.android.app")
    id("imovie.android.app.compose")
    id("imovie.android.hilt")
}

android {
    namespace = "cn.chitanda.app.imovie"

    defaultConfig {
        applicationId = "cn.chitanda.app.imovie"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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