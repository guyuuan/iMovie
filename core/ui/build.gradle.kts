plugins{
    id("imovie.android.lib")
    id("imovie.android.lib.compose")
}

android{
    namespace = "cn.chitanda.app.imovie.core.ui"
}

dependencies {
    implementation(project(":core:design"))
//    implementation(project(":core:model"))
//    implementation(project(":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.kotlinx.datetime)


    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material)
    api(libs.androidx.compose.material3.windowSizeClass)
    debugApi(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.runtime.livedata)
    api(libs.androidx.metrics)
    api(libs.androidx.tracing.ktx)
    api(libs.androidx.navigation.compose)
    api(libs.androidx.hilt.navigation.compose)


}