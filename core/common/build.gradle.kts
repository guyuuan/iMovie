plugins {
    alias(libs.plugins.chitanda.android.lib)
    alias(libs.plugins.chitanda.android.hilt)
}

android {
    namespace = "cn.chitanda.app.imovie.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    api(libs.androidx.lifecycle.viewModel.ktx)
//    testImplementation(project(":core:testing"))
}
